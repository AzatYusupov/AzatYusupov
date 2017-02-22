package com.usupov.autopark.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.usupov.autopark.R;

import java.util.ArrayList;

public class CarNewActivity extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_new);

        initToolbar();

        initVoiceBtn();

    }

    /**
     * Initial toolbar
     */
    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_car_new);
        setSupportActionBar(toolbar);

    }

    private void initVoiceBtn() {

        final EditText edt = (EditText) findViewById(R.id.edittext_vin_number);
        edt.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {

                    if(event.getRawX() >= (edt.getRight() - edt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        Intent intent = new Intent(
                                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                        try {
                            startActivityForResult(intent, RESULT_SPEECH);
                            EditText edt = (EditText) findViewById(R.id.edittext_vin_number);
                            edt.setText("");
                        } catch (ActivityNotFoundException a) {
                            Toast t = Toast.makeText(getApplicationContext(),
                                    "Opps! Your device doesn't support Speech to Text",
                                    Toast.LENGTH_SHORT);
                            t.show();
                        }

                        return true;

                    }

                }

                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    EditText edt = (EditText) findViewById(R.id.edittext_vin_number);
                    edt.setText(text.toString());
                }
                break;
            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
