package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.config.UserURIConstants;
import com.usupov.autopark.http.HttpHandler;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.HttpStatus;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar_signup) Toolbar toolbar;
    @InjectView(R.id.next_text) TextView textNext;
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
    @InjectView(R.id.input_repassword) EditText repasswordText;
    @InjectView(R.id.repasswordlayout) TextInputLayout repasswordlayout;
//    @InjectView(R.id.input_company) EditText companyText;
//    @InjectView(R.id.companyLayout) TextInputLayout companyLayout;
//    @InjectView(R.id.input_inn) EditText innText;
//    @InjectView(R.id.input_address) EditText addressText;
    @InjectView(R.id.btn_register) Button registerButton;

    private static String name;
    private static String email;
    private static String password;
    private static String repassword;
//    private static String company;
//    private static String inn;
//    private static String address;

    private boolean isEmptyAtLeastOne;

    private AppCompatCheckBox checkBoxAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
//    private void initCheckBoxAgree() {
//        checkBoxAgree = (AppCompatCheckBox) findViewById(R.id.chBoxAgree);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            checkBoxAgree.setButtonTintList(ContextCompat.getColorStateList(getBaseContext(), R.color.colorGray));
//        }
//        checkBoxAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        checkBoxAgree.setButtonTintList(ContextCompat.getColorStateList(getBaseContext(), R.color.colorPrimary));
//                    }
//                }
//                else {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        checkBoxAgree.setButtonTintList(ContextCompat.getColorStateList(getBaseContext(), R.color.colorGray));
//                    }
//                }
//            }
//        });
//    }

    public void register() {
        if (!validate()) {
            if (isEmptyAtLeastOne)
                onIncorrectData(getString(R.string.error_fill_register));
            else
                onIncorrectData(getString(R.string.error_check_input_data));
            return;
        }
//        if (!checkBoxAgree.isChecked()) {
//            onIncorrectData(getString(R.string.you_need_agree));
//            return;
//        }

        registerButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppCompatAlertDialogStyle);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.registering));

        progressDialog.show();


        final HashMap<String, String> pairs = new HashMap<>();

        pairs.put("name", name);
        pairs.put("email", email);
        pairs.put("password", password);
//        pairs.put("company", company);
//        pairs.put("inn", inn);
//        pairs.put("address", address);


        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HttpHandler handler = new HttpHandler();
                int status = handler.postWithOneFile(UserURIConstants.SIGN_UP, pairs, null, getApplicationContext(), true).getStatusCode();

                switch (status) {
                    case HttpStatus.SC_OK :
                        setResult(RESULT_OK);
                        finish();
                        Intent intent = new Intent(SignupActivity.this,  CarListActivity.class);
                        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                        intent.putExtra("name", name);
                        startActivity(intent, bundle);
                        break;
                    case HttpStatus.SC_CONFLICT :
                        emailLayout.setError(getString(R.string.error_email_exist));
                        registerButton.setEnabled(true);
                        onIncorrectData(getString(R.string.error_email_exist));
                        break;
                    default:
                        onIncorrectData(getString(R.string.no_internet_connection));
                        break;
                }
                progressDialog.dismiss();
            }
        }, 1000);

    }

    public void onIncorrectData(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

        registerButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        isEmptyAtLeastOne = false;

        name = nameText.getText().toString().trim();
        email = emailText.getText().toString().trim();
        password = passwordText.getText().toString().trim();
        repassword = repasswordText.getText().toString().trim();
//        company = companyText.getText().toString().trim();
//        inn = innText.getText().toString().trim();
//        address = addressText.getText().toString().trim();

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
        else if (password.length() < 4 || password.length() > 12) {
            passwordHint.setVisibility(View.GONE);
            passwordView.setVisibility(View.GONE);
            passwordLayout.setError(getString(R.string.error_password));
            valid = false;
        }
        else
            passwordLayout.setError(null);

        if (repassword.isEmpty()) {
            if (!password.isEmpty()) {
                repasswordlayout.setError(getString(R.string.error_password_empty));
                isEmptyAtLeastOne = true;
                valid = false;
            }
        }
        else if (!repassword.equals(password)) {
            repasswordlayout.setError(getString(R.string.error_passwords_not_coincide));
            valid = false;
        }
        else
            repasswordlayout.setError(null);

//        if (company.isEmpty()) {
//            companyLayout.setError(getString(R.string.error_company_empty));
//            isEmptyAtLeastOne = true;
//            valid = false;
//        }
//        else if (company.length() < 2) {
//            companyLayout.setError(getString(R.string.error_company));
//            valid = false;
//        }
//        else
//            companyLayout.setError(null);

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

        textNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
