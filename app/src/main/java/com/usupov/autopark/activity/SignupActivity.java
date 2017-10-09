package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
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

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar_signup) Toolbar toolbar;
    @InjectView(R.id.input_name) EditText nameText;
    @InjectView(R.id.nameLayout) TextInputLayout nameLayout;
    @InjectView(R.id.nameHint) TextView nameHint;
    @InjectView(R.id.nameView) View nameView;
    @InjectView(R.id.input_email) EditText emailText;
    @InjectView(R.id.emailLayout) TextInputLayout emailLayout;
    @InjectView(R.id.passwordHint) TextView passwordHint;
    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.passwordLayout) TextInputLayout passwordLayout;
    @InjectView(R.id.passwordView) View passwordView;
//    @InjectView(R.id.input_repassword) EditText repasswordText;
//    @InjectView(R.id.repasswordlayout) TextInputLayout repasswordlayout;
    @InjectView(R.id.apbRegisterBtn)
    ActionProcessButton registerButton;
    ProgressDialog progressDialog;

    private static String name;
    private static String email;
    private static String password;
    private static String repassword;

    private boolean isEmptyAtLeastOne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        progressDialog = new ProgressDialog(this,
                R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle(getString(R.string.please_wait));

        initToolbar();

        nameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    nameHint.setVisibility(View.VISIBLE);
                    nameView.setVisibility(View.VISIBLE);
                    nameLayout.setErrorEnabled(false);
                }
                else {
                    nameHint.setVisibility(View.GONE);
                    nameView.setVisibility(View.GONE);
                    nameLayout.setErrorEnabled(true);
                }
            }
        });


        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    passwordHint.setVisibility(View.VISIBLE);
                    passwordView.setVisibility(View.VISIBLE);
                    passwordLayout.setErrorEnabled(false);
                }
                else {
                    passwordHint.setVisibility(View.GONE);
                    passwordView.setVisibility(View.GONE);
                    passwordLayout.setErrorEnabled(true);
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        initRulesTextBtn();

    }
    private void initRulesTextBtn() {
        TextView textRules = (TextView) findViewById(R.id.textRules);
        textRules.setMovementMethod(LinkMovementMethod.getInstance());
        textRules.setText(getString(R.string.rules), TextView.BufferType.SPANNABLE);
        Spannable mySpannable = (Spannable)textRules.getText();
        ClickableSpan myClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(new Intent(SignupActivity.this, PrivacyActivity.class), bundle);
            }
        };
        mySpannable.setSpan(myClickableSpan, 32, 60, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textRules.setMovementMethod(LinkMovementMethod.getInstance());

    }


    public void register() {
        if (!validate()) {
            if (isEmptyAtLeastOne)
                onIncorrectData(getString(R.string.error_fill_register));
            else
                onIncorrectData(getString(R.string.error_check_input_data));
            return;
        }
//        registerButton.setProgress(1);
        progressDialog.show();
        registerButton.setEnabled(false);
        String url = UserURIConstants.SIGN_UP;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        HttpHandler.saverAutToken(getApplicationContext(), response);
                        onRegisterSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error.networkResponse != null && error.networkResponse.statusCode==HttpStatus.SC_CONFLICT) {
                            emailLayout.setError(getString(R.string.error_email_exist));
                            onIncorrectData(getString(R.string.error_email_exist));
                        }
                        else
                            onIncorrectData(getString(R.string.no_internet_connection));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("email", email);
                map.put("password", password);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void onRegisterSuccess() {
//        registerButton.setProgress(0);
        Intent intent = new Intent(SignupActivity.this,  CarListActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        intent.putExtra("name", name);
        startActivity(intent, bundle);
        finishAffinity();
    }

    public void onIncorrectData(String text) {
        registerButton.setProgress(0);
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

        registerButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        isEmptyAtLeastOne = false;

        name = nameText.getText().toString().trim();
        email = emailText.getText().toString().trim();
        password = passwordText.getText().toString().trim();
//        repassword = repasswordText.getText().toString().trim();

        if (name.isEmpty()) {
            nameLayout.setError(getString(R.string.error_name_empty));
            nameHint.setVisibility(View.GONE);
            nameView.setVisibility(View.GONE);
            isEmptyAtLeastOne = true;
            valid = false;
        }
        else if (name.length() < 3) {
            nameLayout.setError(getString(R.string.error_name));
            nameHint.setVisibility(View.GONE);
            nameView.setVisibility(View.GONE);
            valid = false;
        }
        else
            nameLayout.setError(null);

        if (email.isEmpty()) {
            emailLayout.setError(getString(R.string.error_email_empty));
            isEmptyAtLeastOne = true;
            valid = false;
        }
        else if (!EmailValidator.getInstance().isValid(email)) {
            emailLayout.setError(getString(R.string.error_email));
            valid = false;
        }
        else
            emailLayout.setError(null);


        if (password.isEmpty()) {
            passwordHint.setVisibility(View.GONE);
            passwordView.setVisibility(View.GONE);
            passwordLayout.setError(getString(R.string.error_password_empty));
            isEmptyAtLeastOne = true;
            valid = false;
        }
        else if (password.length() < 6 || password.length() > 20) {
            passwordHint.setVisibility(View.GONE);
            passwordView.setVisibility(View.GONE);
            passwordLayout.setError(getString(R.string.error_password));
            valid = false;
        }
        else {
            boolean existDigit = false, existLetter = false;
            for (int i = 0; i < password.length(); i++) {
                if (Character.isDigit(password.charAt(i)))
                    existDigit = true;
                if (Character.isLetter(password.charAt(i)))
                    existLetter = true;
            }
            if (existDigit && existLetter)
                passwordLayout.setError(null);
            else {
                passwordHint.setVisibility(View.GONE);
                passwordView.setVisibility(View.GONE);
                passwordLayout.setError(getString(R.string.error_password_digit_letter));
                valid = false;
            }
        }

//        if (repassword.isEmpty()) {
//            if (!password.isEmpty()) {
//                repasswordlayout.setError(getString(R.string.error_password_empty));
//                isEmptyAtLeastOne = true;
//                valid = false;
//            }
//        }
//        else if (!repassword.equals(password)) {
//            repasswordlayout.setError(getString(R.string.error_passwords_not_coincide));
//            valid = false;
//        }
//        else
//            repasswordlayout.setError(null);

        return valid;
    }

    public void initToolbar() {
        toolbar.setTitle("Регистрация");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
