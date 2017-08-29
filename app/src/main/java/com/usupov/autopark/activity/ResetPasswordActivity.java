package com.usupov.autopark.activity;

import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.productcard.R;
import com.usupov.autopark.config.UserURIConstants;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CustomHttpResponse;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.HttpStatus;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ResetPasswordActivity extends AppCompatActivity {

    @InjectView(R.id.btn_reset)
    Button resetPasswordtButton;
    @InjectView((R.id.input_email))
    EditText emailText;
    @InjectView(R.id.inputLayoutEmail)
    TextInputLayout emailLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ButterKnife.inject(this);
        initToolbar();

        resetPasswordtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();

                if (email.isEmpty())
                    emailLayout.setError(getString(R.string.error_email_empty));
                else if (!EmailValidator.getInstance().isValid(email))
                    emailLayout.setError(getString(R.string.error_email));
                else {
                    emailLayout.setError(null);
                    submit(email);
                }
            }
        });
    }

    private void submit(String email) {
        HttpHandler handler = new HttpHandler();
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        CustomHttpResponse response = handler.postWithOneFile(UserURIConstants.RESET_PASSWORD, map, null, getApplicationContext(), true);
        if (response.getStatusCode()== HttpStatus.SC_NOT_FOUND)
            emailLayout.setError(getString(R.string.error_email_check));
        else {
            emailLayout.setError(null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
            builder.setMessage(R.string.email_sent_success);
            builder.setPositiveButton("OK", null);
            builder.show();
        }
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_reset_password);
        toolbar.setTitle("Забыли пароль?");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
