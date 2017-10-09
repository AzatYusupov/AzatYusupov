package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import product.card.R;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.usupov.autopark.config.UserURIConstants;
import com.usupov.autopark.http.HttpHandler;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;

    ProgressDialog progressDialog;

    @InjectView(R.id.input_email)
    EditText emailText;

    @InjectView(R.id.input_password)
    EditText passwordText;

    @InjectView(R.id.text_forgot_password)
    TextView forgotPasswordLink;

    @InjectView(R.id.apbLogin)
    ActionProcessButton loginButton;

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
        if (token != null && !getIntent().hasExtra("unauthorized")) {
            Intent intent = new Intent(this, PartListActivity.class);
            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
            startActivity(intent, bundle);
            finish();
            return;
        }

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ButterKnife.inject(this);
        progressDialog = new ProgressDialog(this,
                R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle(getString(R.string.please_wait));

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
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivityForResult(intent, REQUEST_SIGNUP, bundle);
            }
        });

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
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class), bundle);
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    private void keyboardHide(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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


        progressDialog.show();

        loginButton.setEnabled(false);
//        loginButton.setProgress(1);

        final String email = emailText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();

        String url = UserURIConstants.SIGN_IN;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        HttpHandler.saverAutToken(getApplicationContext(), response);
                        progressDialog.dismiss();
                        onLoginSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error.networkResponse != null && error.networkResponse.statusCode==HttpStatus.SC_NOT_FOUND) {
                            onLoginFailed(getString(R.string.error_check_input_data), true);
                            onLoginFailed(getString(R.string.error_email_or_password), false);
                        }
                        else {
                            onLoginFailed(getString(R.string.no_internet_connection), true);
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("password", password);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void onLoginSuccess() {

        textErrorOrPasswordError.setText(null);
//        loginButton.setProgress(0);

        Intent intent = new Intent(LoginActivity.this, PartListActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivity(intent, bundle);

        finish();
    }

    public void onLoginFailed(String message, boolean toast) {
        if (toast)
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
        else
            textErrorOrPasswordError.setText(message);
        loginButton.setEnabled(true);
        loginButton.setProgress(0);
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
