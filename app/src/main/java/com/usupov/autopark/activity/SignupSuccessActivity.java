package com.usupov.autopark.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.productcard.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupSuccessActivity extends AppCompatActivity {

    @InjectView(R.id.text_login) TextView loginText;
    @InjectView(R.id.text_name) TextView nameText;
    @InjectView(R.id.text_feedback) TextView feedbackText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_success);
        ButterKnife.inject(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String name = getIntent().getStringExtra("name");

        nameText.setText(nameText.getText().toString() + name + "!");

        String text = feedbackText.getText().toString();
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), 123, 142, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        feedbackText.setText(spannable, TextView.BufferType.SPANNABLE);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(SignupSuccessActivity.this, LoginActivity.class));
            }
        });

    }
}
