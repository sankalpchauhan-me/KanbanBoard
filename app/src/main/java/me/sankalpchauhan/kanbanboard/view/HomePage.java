package me.sankalpchauhan.kanbanboard.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.model.User;
import me.sankalpchauhan.kanbanboard.viewmodel.HomePageViewModel;

import static me.sankalpchauhan.kanbanboard.util.Constants.USER;

public class HomePage extends AppCompatActivity {
    private Button mRegistrationBTN, mLoginBTN, mForgetPassBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mRegistrationBTN = findViewById(R.id.reg_btn);
        mLoginBTN = findViewById(R.id.login_btn);
        mForgetPassBTN = findViewById(R.id.forgetpassword_btn);

        mRegistrationBTN.setOnClickListener(view -> {
            Intent i = new Intent(HomePage.this, SignUpActivity.class);
            startActivity(i);
            overridePendingTransition(0,0);
        });

        mLoginBTN.setOnClickListener(view -> {
            Intent i = new Intent(HomePage.this, LoginActivity.class);
            startActivity(i);
            overridePendingTransition(0,0);
        });

        mForgetPassBTN.setOnClickListener(view -> {
         Intent i = new Intent(HomePage.this, LoginActivity.class);
         i.putExtra("ForgotPass", true);
         startActivity(i);
         overridePendingTransition(0,0);
        });
    }
}
