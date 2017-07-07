package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_email) EditText emailText;
    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.text_forgot_password) TextView forgotPasswordLink;
    @InjectView(R.id.btn_login) Button loginButton;
    @InjectView(R.id.text_registration) TextView registrationLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ButterKnife.inject(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        registrationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_SIGNUP :
                if (resultCode==RESULT_OK)
                    finish();
                break;
            default:
                break;
        }
    }

    public void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Аутентификация...");
        progressDialog.show();

        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();



        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {

                        if (email.equals("azat.1990@mail.ru") && password.equals("123456"))
                            onLoginSuccess();
                        else
                            onLoginFailed();

                        progressDialog.dismiss();
                    }
                }, 1000);
    }

    public void onLoginSuccess() {
        finish();

        loginButton.setEnabled(true);
        startActivity(new Intent(getBaseContext(), MainActivity.class));
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Ошибка входа", Toast.LENGTH_SHORT).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Введите действительный адрес электронной почты");
            valid = false;
        }
        else
            emailText.setError(null);

        if (password.isEmpty() || password.length() < 4 || password.length() > 12) {
            passwordText.setError("Введите от 4 до 12 буквенно-цифровых символов");
            valid = false;
        }
        else
            passwordText.setError(null);

        return valid;
    }
}
