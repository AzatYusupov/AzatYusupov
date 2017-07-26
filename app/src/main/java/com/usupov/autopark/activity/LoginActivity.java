package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.config.UserURIConstants;
import com.usupov.autopark.http.HttpHandler;

import org.apache.http.HttpStatus;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_email)
    EditText emailText;

    @InjectView(R.id.input_password)
    EditText passwordText;

    @InjectView(R.id.text_forgot_password)
    TextView forgotPasswordLink;

    @InjectView(R.id.btn_login)
    Button loginButton;

    @InjectView(R.id.text_registration)
    TextView registrationLink;

    @InjectView(R.id.inputLayoutEmail)
    TextInputLayout inputLayoutEmail;

    @InjectView(R.id.inputLayoutPassword)
    TextInputLayout inputLayoutPassword;

    @InjectView(R.id.textErrorOrPasswordError)
    TextView textErrorOrPasswordError;

    private boolean isEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        getActionBar().hide();

        String token = HttpHandler.getLocalServerToken(getApplicationContext());
        if (token != null) {
            finish();
            startActivity(new Intent(this, PartListActivity.class));
            return;
        }


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

        passwordText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()==KeyEvent.ACTION_DOWN && keyCode==KeyEvent.KEYCODE_ENTER) {
                    keyboardHide(v);
                    login();
                    return true;
                }
                return false;
            }
        });


        emailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isFocused()) {
                    keyboardHide(v);
                    v.clearFocus();
                }
            }
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    private void keyboardHide(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
            textErrorOrPasswordError.setText("");
            if (isEmpty)
                onLoginFailed(getString(R.string.error_fill_authoring), true);
            else
                onLoginFailed(getString(R.string.error_check_input_data), true);
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.authoring));
        progressDialog.show();
        final String email = emailText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {

                        HttpHandler handler = new HttpHandler();
                        HashMap<String, String> pairs = new HashMap<>();
                        pairs.put("email", email);
                        pairs.put("password", password);

                        int response = handler.postWithOneFile(UserURIConstants.SIGN_IN, pairs, null, getApplicationContext(), true).getStatusCode();
                        if (response == HttpStatus.SC_NOT_FOUND) {
                            onLoginFailed(getString(R.string.error_check_input_data), true);
                            onLoginFailed(getString(R.string.error_email_or_password), false);
                        }
                        else if (response != HttpStatus.SC_OK)
                            onLoginFailed(getString(R.string.no_internet_connection), true);
                        else
                            onLoginSuccess();

                        progressDialog.dismiss();
                    }
                }, 1000);
    }

    public void onLoginSuccess() {

        finish();
        startActivity(new Intent(this, PartListActivity.class));
//        loginButton.setEnabled(true);
    }

    public void onLoginFailed(String message, boolean toast) {
        if (toast)
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
        else
            textErrorOrPasswordError.setText(message);
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        isEmpty = false;

        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (email.isEmpty()) {
            isEmpty = true;
            inputLayoutEmail.setError(getString(R.string.error_email_empty));
            valid = false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputLayoutEmail.setError(getString(R.string.error_email));
            valid = false;
        }
        else
            inputLayoutEmail.setError(null);

        if (password.isEmpty()) {
            isEmpty = true;
            inputLayoutPassword.setError(getString(R.string.error_password_empty));
            valid = false;
        }
        else
            inputLayoutPassword.setError(null);

        return valid;
    }
}
