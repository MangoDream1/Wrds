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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import nl.mprog.axel.wrds_programmeerproject.Database.FirebaseDBManager;
import nl.mprog.axel.wrds_programmeerproject.R;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "TEST";

    private String email;
    private String username;
    private String password;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDB;
    private FirebaseDBManager fdbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseDB = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        fdbm = FirebaseDBManager.getInstance();

        findViewById(R.id.register_button).setOnClickListener(this);
        findViewById(R.id.log_in_button).setOnClickListener(this);
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
        ((TextView) findViewById(R.id.title_textView)).setText(getString(R.string.title_log_in));

        EditText usernameEditText = (EditText) findViewById(R.id.username_editText);
        EditText password2EditText = (EditText) findViewById(R.id.password2_editText);

        usernameEditText.setEnabled(false);
        usernameEditText.setVisibility(View.GONE);

        password2EditText.setEnabled(false);
        password2EditText.setVisibility(View.GONE);

        findViewById(R.id.log_in_button).setVisibility(View.VISIBLE);
    }

    private void showRegister() {
        ((TextView) findViewById(R.id.title_textView)).setText(getString(R.string.title_register));

        EditText usernameEditText = (EditText) findViewById(R.id.username_editText);
        EditText password2EditText = (EditText) findViewById(R.id.password2_editText);

        usernameEditText.setEnabled(true);
        usernameEditText.setVisibility(View.VISIBLE);

        password2EditText.setEnabled(true);
        password2EditText.setVisibility(View.VISIBLE);

        findViewById(R.id.log_in_button).setVisibility(View.GONE);
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
                            Toast.makeText(LogInActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            showLogin();
                            signIn();

                            fdbm.createUser(username,
                                    FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }

                        hideProgressBar();
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
                            Toast.makeText(LogInActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            finish();
                        }

                        hideProgressBar();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_in_button:
                showProgressBar();
                showLogin();
                signIn();

                break;

            case R.id.register_button:
                showRegister();

                // If reset is valid check if username is taken
                // if not taken it will start createAccount()
                if (validateForm()) {
                    showProgressBar();
                    usernameTaken();
                }

                break;
        }
    }

    private void usernameTaken() {
        firebaseDB.getReference().child("usernames").child(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "usernameTaken:" + dataSnapshot.getValue());

                        if (dataSnapshot.getValue() != null) {
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

    private void showProgressBar() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);

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
            emailEditText.setError(getString(R.string.error_required));
            return false;
        }

        return true;
    }

    private boolean validateUsername(boolean usernameTaken) {
        EditText usernameEditText = (EditText) findViewById(R.id.username_editText);
        username = usernameEditText.getText().toString().trim().toLowerCase();

        if (usernameEditText.isEnabled()) {
            if (username.isEmpty()) {
                usernameEditText.setError(getString(R.string.error_required));
                return false;
            } else if (username.contains(" ")) {
                usernameEditText.setError(getString(R.string.error_username_space));
                return false;
            } else if (usernameTaken) {
                usernameEditText.setError(getString(R.string.error_username_taken));
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
            passwordEditText.setError(getString(R.string.error_required));
            valid = false;
        }

        if (password2EditText.isEnabled()) {
            if (password2.isEmpty()) {
                password2EditText.setError(getString(R.string.error_required));
                valid = false;
            } else if (!password.equals(password2)) {
                passwordEditText.setError(getString(R.string.error_passwords_not_equal));
                valid = false;
            }
        }

        return valid;
    }
}
