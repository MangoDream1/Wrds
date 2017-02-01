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


/**
 * LoginActivity where the user can create an account or log in to an existing one
 */

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

    /**
     * Show EditTexts for login and hide those for register
     */
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

    /**
     * Show EditTexts for register and hide those for login
     */
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

    /**
     * Create Firebase account
     */
    private void createAccount() {
        Log.d(TAG, "createAccount:" + email);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        createAccount(task);
                    }
                });
    }

    /**
     * Process task created from createAccount()
     * @param task firebase task
     */
    private void createAccount(Task task) {
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

    /**
     * Sign in user after form is validated
     */
    private void signIn() {
        Log.d(TAG, "signIn:" + email);

        if (!validateForm()) {
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        signIn(task);
                    }
                });
    }

    /**
     * Process task from signIn()
     * @param task firebase task
     */
    private void signIn(Task task) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_in_button:
                logInButton();
                break;

            case R.id.register_button:
                registerButton();
                break;
        }
    }

    /**
     * Show login and try to log in user
     */
    private void logInButton() {
        showProgressBar();
        showLogin();
        signIn();
    }

    /**
     * Show register and try to register user if form is valid
     */
    private void registerButton() {
        showRegister();

        // If reset is valid check if username is taken
        // if not taken it will start createAccount()
        if (validateForm()) {
            showProgressBar();
            usernameTaken();
        }

    }

    /**
     * Check if username is taken
     */
    private void usernameTaken() {
        firebaseDB.getReference().child("usernames").child(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usernameTaken(dataSnapshot);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Process username taken
     * @param dataSnapshot firebase database snapshot
     */
    private void usernameTaken(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() != null) {
            ((EditText) findViewById(R.id.username_editText))
                    .setError(getString(R.string.error_username_taken));
            hideProgressBar();
        } else {
            createAccount();
        }
    }

    /**
     * Show progressBar
     */
    private void showProgressBar() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    /**
     * Hide progressBar
     */
    private void hideProgressBar() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    /**
     * Validate entire form
     * @return true if valid else false
     */
    private boolean validateForm() {
        // If statement is lazy thus not possible thus check all first then find false
        Boolean[] isValid = new Boolean[]{validateEmail(), validatePassword(), validateUsername()};

        return !Arrays.asList(isValid).contains(false);
    }

    /**
     * Validate email of user. Set error if not.
     * @return true if valid else false
     */
    private boolean validateEmail() {
        EditText emailEditText = (EditText) findViewById(R.id.email_editText);
        email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError(getString(R.string.error_required));
            return false;
        }

        return true;
    }

    /**
     * Validate username. Set error if not
     * @return true if valid else false
     */
    private boolean validateUsername() {
        EditText usernameEditText = (EditText) findViewById(R.id.username_editText);
        username = usernameEditText.getText().toString().trim().toLowerCase();

        if (usernameEditText.isEnabled()) {
            if (username.isEmpty()) {
                usernameEditText.setError(getString(R.string.error_required));
                return false;
            } else if (username.contains(" ")) {
                usernameEditText.setError(getString(R.string.error_username_space));
                return false;
            }
        }

        return true;
    }

    /**
     * Validate password. Set error if not.
     * @return true if valid else false
     */
    private boolean validatePassword() {
        password = ((EditText) findViewById(R.id.password_editText)).getText().toString();
        String password2 = ((EditText) findViewById(R.id.password2_editText)).getText().toString();

        Boolean[] isValid = new Boolean[]{validatePassword(password),
                validatePassword2(password, password2)};

        return !Arrays.asList(isValid).contains(false);
    }

    /**
     * Validate password if filled in
     * @param password password
     * @return boolean if filled in
     */
    private boolean validatePassword(String password) {
        EditText passwordEditText = (EditText) findViewById(R.id.password_editText);

        if (password.isEmpty()) {
            passwordEditText.setError(getString(R.string.error_required));
            return false;
        }

        return true;
    }

    /**
     * Check password2 is filled in and if password is equal to password2
     * @param password password
     * @param password2 repeat password
     * @return boolean if valid
     */
    private boolean validatePassword2(String password, String password2) {
        EditText passwordEditText = (EditText) findViewById(R.id.password_editText);
        EditText password2EditText = (EditText) findViewById(R.id.password2_editText);

        if (password2EditText.isEnabled()) {
            if (password2.isEmpty()) {
                password2EditText.setError(getString(R.string.error_required));
                return false;
            } else if (!password.equals(password2)) {
                passwordEditText.setError(getString(R.string.error_passwords_not_equal));
                return false;
            }
        }
        return true;
    }
}
