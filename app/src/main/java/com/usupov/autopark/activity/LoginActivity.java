package com.usupov.autopark.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.http.Config;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends Activity {
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_email) EditText emailText;
    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.text_forgot_password) TextView forgotPasswordLink;
    @InjectView(R.id.btn_login) Button loginButton;
    @InjectView(R.id.text_registration) TextView registrationLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        getActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

        initInternetConnection(true);
    }

    private void initInternetConnection(boolean b) {

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
                        String serverToken = Config.getUrlSignIn();
                        if (serverToken==null)
                            onLoginFailed();
                        else {
                            SharedPreferences sharedPreferences = getSharedPreferences(Config.APP_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Config.TOKEN, serverToken);
                            onLoginSuccess();
                        }
                        progressDialog.dismiss();
                    }
                }, 1000);
    }

    public void onLoginSuccess() {
        finish();

        loginButton.setEnabled(true);
        startActivity(new Intent(this, PartListActivity.class));
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
