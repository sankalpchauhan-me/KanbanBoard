package me.sankalpchauhan.kanbanboard.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.model.User;
import me.sankalpchauhan.kanbanboard.viewmodel.LoginViewModel;
import me.sankalpchauhan.kanbanboard.viewmodel.SignUpViewModel;

import static me.sankalpchauhan.kanbanboard.util.Constants.USER;
import static me.sankalpchauhan.kanbanboard.util.HelperClass.isEmailValid;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextView terms;
    EditText email, password, confpass, name;
    Button signUpButton;
    View parent;
    ImageView ImgUpdate;
    SignUpViewModel signUpViewModel;
    ProgressBar mLoginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        terms = findViewById(R.id.terms);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confpass = findViewById(R.id.confirm_pass);
        name = findViewById(R.id.name);
        signUpButton = findViewById(R.id.regBTN);
        mAuth = FirebaseAuth.getInstance();
        parent = findViewById(R.id.parentSignUp);
        ImgUpdate = findViewById(R.id.ImgSet);
        mLoginProgressBar = findViewById(R.id.progressbar_signuplogin);
        initSignUnButton();
        initAuthViewModel();

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(SignUpActivity.this, "Not yet implemented", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSignUnButton() {
        signUpButton.setOnClickListener(v->signUp());
    }

    private void initAuthViewModel() {
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
    }

    private void signUp(){
        if(validateForm()) {
            signUpWithEmail(this, email.getText().toString(), password.getText().toString(), name.getText().toString());
        } else {
            Toast.makeText(SignUpActivity.this, "Please correct the errors", Toast.LENGTH_LONG).show();
        }
    }

    private void signUpWithEmail(Context context, String email, String password, String name) {
            signUpViewModel.signUpWithEmail(context, email, password, name);
            signUpButton.setVisibility(View.INVISIBLE);
            signUpViewModel.authenticatedUserLiveData.observe(this, authenticatedUser -> {
                if (authenticatedUser.isNew) {
                    createNewUser(authenticatedUser);
                } else {
                    goToMainActivity(authenticatedUser);
                }
            });
    }

    private void createNewUser(User authenticatedUser) {
        signUpViewModel.createUser(authenticatedUser);
        signUpViewModel.createdUserLiveData.observe(this, user -> {
            if (user.isCreated) {
                toastMessage(user.name);
            }
            goToMainActivity(user);
        });
    }

    private void toastMessage(String name) {
        Toast.makeText(this, "Hi " + name + "!\n" + "Your account was successfully created. A verification mail was sent.", Toast.LENGTH_LONG).show();
    }

    private void goToMainActivity(User user) {
        finishAffinity();
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }


    //FireBase
    private boolean validateForm() {
        boolean valid = true;

        String useremail = email.getText().toString();
        if (TextUtils.isEmpty(useremail.trim())) {
            email.setError("Required");
            valid = false;
        } else {
            email.setError(null);
        }

        if(!password.getText().toString().equals(confpass.getText().toString())){
            confpass.setError("Passwords Do Not Match");
            valid = false;
        }

        String username = name.getText().toString();
        if (TextUtils.isEmpty(username.trim())) {
            name.setError("Required.");
            valid = false;
        } else {
            name.setError(null);
        }

        String userpass = password.getText().toString();
        if (TextUtils.isEmpty(userpass.trim())) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        if(userpass.length()<6){
            password.setError("Password should be at least 6 characters");
        }


        if(!isEmailValid(useremail)){
            email.setError("Email Not Valid");
            valid = false;
        }


        return valid;
    }

    public void setSignUpVisibility(){
        signUpButton.setVisibility(View.VISIBLE);
    }
}
