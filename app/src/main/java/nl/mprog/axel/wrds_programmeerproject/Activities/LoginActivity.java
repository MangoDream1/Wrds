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

import nl.mprog.axel.wrds_programmeerproject.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "TEST";

    private String email;
    private String username;
    private String password;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.register_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        EditText emailEditText = (EditText) findViewById(R.id.email_editText);
        EditText usernameEditText = (EditText) findViewById(R.id.username_editText);
        EditText passwordEditText = (EditText) findViewById(R.id.password_editText);
        EditText password2EditText = (EditText) findViewById(R.id.password2_editText);

        email = emailEditText.getText().toString();
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        String password2 = password2EditText.getText().toString();

        if (email.isEmpty()) {
            emailEditText.setError("Required.");
            isValid = false;
        }

        if (usernameEditText.isEnabled()) {
            if (username.isEmpty()) {
                usernameEditText.setError("Required.");
                isValid = false;
            } else if (!usernameValid(username)) {
                usernameEditText.setError("Username taken");
                isValid = false;
            }
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Required.");
            isValid = false;
        }

        if (password2EditText.isEnabled()) {
            if (password2.isEmpty()) {
                password2EditText.setError("Required");
                isValid = false;
            } else if (!password.equals(password2)) {
                passwordEditText.setError("Passwords are not equal");
                isValid = false;
            }
        }

        return isValid;
    }

    private boolean usernameValid(String username) {
        // TODO check if username already exists

        return true;
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
        findViewById(R.id.register_button).setVisibility(View.GONE);
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

        if (!validateForm()) {
            return;
        }

        // [START create_user_with_email]
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "createUserWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Log in failed",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            showLogin();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn() {
        Log.d(TAG, "signIn:" + email);

        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Login Failed",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            finish();
                        }

                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        firebaseAuth.signOut();
        showLogin();
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
                createAccount();

                break;
        }
    }
}
