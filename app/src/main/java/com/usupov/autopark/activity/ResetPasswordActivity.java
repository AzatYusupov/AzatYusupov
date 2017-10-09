package com.usupov.autopark.activity;

import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ResetPasswordActivity extends AppCompatActivity {

    @InjectView(R.id.btn_reset)
    ActionProcessButton resetPasswordtButton;
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

    private void submit(final String email) {

        String url = UserURIConstants.RESET_PASSWORD;
        resetPasswordtButton.setProgress(1);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        resetPasswordtButton.setProgress(0);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this, R.style.AlertDialog);
                        builder.setMessage(R.string.email_sent_success);
                        builder.setPositiveButton("OK", null);
                        builder.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        resetPasswordtButton.setProgress(0);
                        if (error.networkResponse != null && error.networkResponse.statusCode== HttpStatus.SC_NOT_FOUND)
                            emailLayout.setError(getString(R.string.error_email_check));
                        else
                            Toast.makeText(ResetPasswordActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("email", email);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
