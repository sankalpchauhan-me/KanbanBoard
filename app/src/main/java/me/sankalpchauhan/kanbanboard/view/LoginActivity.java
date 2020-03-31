package me.sankalpchauhan.kanbanboard.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.model.User;
import me.sankalpchauhan.kanbanboard.viewmodel.LoginViewModel;

import static me.sankalpchauhan.kanbanboard.util.Constants.RC_SIGN_IN;
import static me.sankalpchauhan.kanbanboard.util.Constants.USER;
import static me.sankalpchauhan.kanbanboard.util.HelperClass.isEmailValid;
import static me.sankalpchauhan.kanbanboard.util.HelperClass.logErrorMessage;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel authViewModel;
    private GoogleSignInClient googleSignInClient;
    private SignInButton googleSignInButton;
    ProgressBar mProgressGSignIn, mProgressBarEmail;
    Button mSignIn;
    TextView signup, or;
    EditText emailTB;
    EditText passTB;
    View parent;
    Button forgotpasswordBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressGSignIn = findViewById(R.id.progressbar_googlesignin);
        mProgressBarEmail = findViewById(R.id.progressbar_emailsignin);
        signup = findViewById(R.id.signup);
        emailTB = findViewById(R.id.email);
        passTB = findViewById(R.id.password);
        parent = findViewById(R.id.parent);
        forgotpasswordBTN = findViewById(R.id.forgotpassword);
        mSignIn = findViewById(R.id.logBTN);
        googleSignInButton = findViewById(R.id.google_sign_in_button);
        or = findViewById(R.id.or);
        boolean b = getIntent().getBooleanExtra("ForgotPass", false);
        if(b){
            passTB.setVisibility(View.GONE);
            signup.setVisibility(View.GONE);
            or.setVisibility(View.GONE);
            mSignIn.setVisibility(View.GONE);
            googleSignInButton.setVisibility(View.GONE);
            mProgressGSignIn.setVisibility(View.GONE);
            mProgressBarEmail.setVisibility(View.GONE);

        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            signup.setText(Html.fromHtml("<p>Not a user yet? <span style=\"color: #0000ff;\"><strong>Sign Up</strong></span></p>",Html.FROM_HTML_MODE_LEGACY));
        } else {
            signup.setText(Html.fromHtml("<p>Not a user yet? <span style=\"color: #0000ff;\"><strong>Sign Up</strong></span></p>"));
        }

        initGoogleSignInButton();
        initAuthViewModel();
        initGoogleSignInClient();
        initEmailSignInButton();
        initPasswordReset();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
    }

    private void initEmailSignInButton(){
        mSignIn.setOnClickListener(v->emailSignIn());
    }

    private void initPasswordReset(){
        forgotpasswordBTN.setOnClickListener(view -> {
            String email =emailTB.getText().toString();
            if (TextUtils.isEmpty(email)) {
                emailTB.setError("Required.");
            }
            else if(!isEmailValid(email)){
                emailTB.setError("Invalid Email");
            }
            else {
                emailTB.setError(null);
                authViewModel.sendPasswordReset(LoginActivity.this, email);
            }
        });
    }

    private void initGoogleSignInButton() {
        googleSignInButton.setOnClickListener(v -> signIn());
    }

    private void initAuthViewModel() {
        authViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclientid))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void signIn() {
        googleSignInButton.setVisibility(View.INVISIBLE);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void emailSignIn(){
        if(validateForm()){
            authViewModel.signInWithEmail(this, emailTB.getText().toString(), passTB.getText().toString());
            mSignIn.setVisibility(View.INVISIBLE);
            authViewModel.authenticatedUserLiveData.observe(this, authenticatedUser -> {
                if (authenticatedUser.isNew) {
                    createNewUser(authenticatedUser);
                } else {
                    goToMainActivity(authenticatedUser);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    getGoogleAuthCredential(googleSignInAccount);
                }
            } catch (ApiException e) {
                logErrorMessage(e.getMessage());
                googleSignInButton.setVisibility(View.VISIBLE);
            }
        } else {
            googleSignInButton.setVisibility(View.VISIBLE);
        }
    }

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithGoogleAuthCredential(googleAuthCredential);
    }

    private void signInWithGoogleAuthCredential(AuthCredential googleAuthCredential) {
        authViewModel.signInWithGoogle(googleAuthCredential);
        authViewModel.authenticatedUserLiveData.observe(this, authenticatedUser -> {
            if (authenticatedUser.isNew) {
                createNewUser(authenticatedUser);
            } else {
                goToMainActivity(authenticatedUser);
            }
        });
    }

    private void createNewUser(User authenticatedUser) {
        authViewModel.createUser(authenticatedUser);
        authViewModel.createdUserLiveData.observe(this, user -> {
            if (user.isCreated) {
                toastMessage(user.name);
            }
            goToMainActivity(user);
        });
    }

    private void toastMessage(String name) {
        Toast.makeText(this, "Hi " + name + "!\n" + "Your account was successfully created.", Toast.LENGTH_LONG).show();
    }

    private void goToMainActivity(User user) {
        finishAffinity();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;

        String email =emailTB.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailTB.setError("Required.");
            valid = false;
        } else {
            emailTB.setError(null);
        }

        String password = passTB.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passTB.setError("Required.");
            valid = false;
        } else {
            passTB.setError(null);
        }

        return valid;
    }
    public void setSignInVisible(){
        mSignIn.setVisibility(View.VISIBLE);
    }
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
}
