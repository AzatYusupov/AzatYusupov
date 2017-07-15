package com.usupov.autopark.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.icu.util.TimeUnit;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.TextView;

import com.usupov.autopark.R;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        TextView textView = (TextView) findViewById(R.id.text_main);
//        textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Regular.ttf"));

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
//        toolbar.setNavigationIcon(R.drawable.ic_action_stream);
//        toolbar.setTitle(getString(R.string.app_name));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(MainActivity.this, PartListActivity.class));
                finish();
            }
        }, 3000);
    }

}
