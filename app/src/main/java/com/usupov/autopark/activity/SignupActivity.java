package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar_signup) Toolbar toolbar;
    @InjectView(R.id.next_text) TextView textNext;
    @InjectView(R.id.input_name) EditText nameText;
    @InjectView(R.id.input_lastname) EditText lastnameText;
    @InjectView(R.id.input_phone) EditText phoneText;
    @InjectView(R.id.input_email) EditText emailText;
    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.input_repassword) EditText repasswordText;
    @InjectView(R.id.btn_register) Button registerButton;

    private static String name;
    private static String lastname;
    private static String phone;
    private static String email;
    private static String password;
    private static String repassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        initToolbar();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void register() {
        if (!validate()) {
            onIncorrectData("Ошибки в полях");
            return;
        }

        registerButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat_DayNight);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Регистрация...");
        progressDialog.show();

        List<NameValuePair> pairs = new ArrayList<>();

        pairs.add(new BasicNameValuePair("name", name));
        pairs.add(new BasicNameValuePair("lastname", lastname));
        pairs.add(new BasicNameValuePair("phone", phone));
        pairs.add(new BasicNameValuePair("email", email));
        pairs.add(new BasicNameValuePair("password", password));

        HttpHandler handler = new HttpHandler();
        int status = handler.doSimplePost(Config.getUrlSignup(), pairs);
        System.out.println(Config.getUrlSignup());
        System.out.println(name+" "+lastname+" "+phone+" "+email+" "+password);
        System.out.println("STATUSSSSSSSSSSSSSSSSSSSSSWWWWWWW="+status);
        progressDialog.dismiss();
        switch (status) {
            case HttpStatus.SC_OK :
                setResult(RESULT_OK);
                finish();
                startActivity(new Intent(this,  SignupSuccessActivity.class));
                break;
            case HttpStatus.SC_CONFLICT :
                onIncorrectData("Пользователь с таким электронном почтом уже существует");
                break;
            default:
                onIncorrectData(getString(R.string.no_internet_connection));
                break;
        }


    }

    public void onIncorrectData(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

        registerButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        name = nameText.getText().toString();
        lastname = lastnameText.getText().toString();
        phone = phoneText.getText().toString();
        email = emailText.getText().toString();
        password = passwordText.getText().toString();
        repassword = repasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("не менее 3 символов");
            valid = false;
        }
        else
            nameText.setError(null);

        if (lastname.isEmpty() || lastname.length() < 3) {
            lastnameText.setError("не менее 3 символов");
            valid = false;
        }
        else
            lastnameText.setError(null);

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

        if (repassword.isEmpty() || !repassword.equals(password)) {
            repasswordText.setError("паролы не совпадают");
            valid = false;
        }
        else
            repasswordText.setError(null);

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

}
