package com.usupov.autopark.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.usupov.autopark.R;

public class NewCarActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);

        initToolbar();

    }
    /**
     * Initial toolbar
     */
    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_car);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
