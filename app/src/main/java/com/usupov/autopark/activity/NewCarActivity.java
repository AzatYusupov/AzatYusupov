package com.usupov.autopark.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.usupov.autopark.R;

public class NewCarActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
