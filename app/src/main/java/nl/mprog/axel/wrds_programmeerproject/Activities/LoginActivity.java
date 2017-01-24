package nl.mprog.axel.wrds_programmeerproject.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

import nl.mprog.axel.wrds_programmeerproject.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "TEST";

    private String email;
    private String username;
    private String password;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseDB = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.register_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void showLogin() {
        ((TextView) findViewById(R.id.title_textView)).setText("Log in");

        EditText usernameEditText = (EditText) findViewById(R.id.username_editText);
        EditText password2EditText = (EditText) findViewById(R.id.password2_editText);

        usernameEditText.setEnabled(false);
        usernameEditText.setVisibility(View.GONE);

        password2EditText.setEnabled(false);
        password2EditText.setVisibility(View.GONE);

        findViewById(R.id.login_button).setVisibility(View.VISIBLE);
    }

    private void showRegister() {
        ((TextView) findViewById(R.id.title_textView)).setText("Register");

        EditText usernameEditText = (EditText) findViewById(R.id.username_editText);
        EditText password2EditText = (EditText) findViewById(R.id.password2_editText);

        usernameEditText.setEnabled(true);
        usernameEditText.setVisibility(View.VISIBLE);

        password2EditText.setEnabled(true);
        password2EditText.setVisibility(View.VISIBLE);

        findViewById(R.id.login_button).setVisibility(View.GONE);
        findViewById(R.id.register_button).setVisibility(View.VISIBLE);
    }

    private void createAccount() {
        Log.d(TAG, "createAccount:" + email);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "createUserWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            showLogin();
                            signIn();

                            firebaseDB.getReference().child("users")
                                    .child(username).setValue(createUserHashmap());

                        }
                    }
                });
    }

    private void signIn() {
        Log.d(TAG, "signIn:" + email);

        if (!validateForm()) {
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            finish();
                        }

                    }
                });
    }

    private HashMap<String, Object> createUserHashmap(){
        HashMap<String, Object> user = new HashMap<>();

        user.put("id", firebaseAuth.getCurrentUser().getUid());
        user.put("email", email);

        return user;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                showLogin();
                signIn();

                break;

            case R.id.register_button:
                showRegister();

                // If reset is valid check if username is taken
                // if not taken it will start createAccount()
                if (validateForm()) {
                    usernameTaken();
                }

                break;
        }
    }

    private void usernameTaken() {
        firebaseDB.getReference().child(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "usernameTaken:"+dataSnapshot.getValue());

                        if (dataSnapshot.getValue() == null) {
                            validateUsername(true);
                        } else {
                            createAccount();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private boolean validateForm() {
        // If statement is lazy thus not possible thus check all first then find false
        Boolean[] isValid = new Boolean[]{validateEmail(), validatePassword(), validateUsername()};

        return !Arrays.asList(isValid).contains(false);
    }

    private boolean validateEmail() {
        EditText emailEditText = (EditText) findViewById(R.id.email_editText);
        email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Required.");
            return false;
        }

        return true;
    }

    private boolean validateUsername(boolean usernameTaken) {
        EditText usernameEditText = (EditText) findViewById(R.id.username_editText);
        username = usernameEditText.getText().toString().trim().toLowerCase();

        if (usernameEditText.isEnabled()) {
            if (username.isEmpty()) {
                usernameEditText.setError("Required.");
                return false;
            } else if (username.contains(" ")) {
                usernameEditText.setError("Username contains space");
                return false;
            } else if (usernameTaken) {
                usernameEditText.setError("Username taken");
                return false;
            }
        }

        return true;
    }

    private boolean validateUsername() {
        return validateUsername(false);
    }

    private boolean validatePassword() {
        boolean valid = true;

        EditText passwordEditText = (EditText) findViewById(R.id.password_editText);
        EditText password2EditText = (EditText) findViewById(R.id.password2_editText);

        password = passwordEditText.getText().toString();
        String password2 = password2EditText.getText().toString();

        if (password.isEmpty()) {
            passwordEditText.setError("Required.");
            valid = false;
        }

        if (password2EditText.isEnabled()) {
            if (password2.isEmpty()) {
                password2EditText.setError("Required");
                valid = false;
            } else if (!password.equals(password2)) {
                passwordEditText.setError("Passwords are not equal");
                valid = false;
            }
        }

        return valid;
    }
}
