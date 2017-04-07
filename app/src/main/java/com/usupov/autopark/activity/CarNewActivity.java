package com.usupov.autopark.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.usupov.autopark.R;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.service.SpeachRecogn;

import org.json.JSONObject;

import java.util.ArrayList;

public class CarNewActivity extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;
    TextView tvVinError;
    private KeyboardView mKeyboardView;
    private Keyboard vinKeyboard;
    private EditText vinEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_new);

        initToolbar();

        initVinKeyboard();

        tvVinError = (TextView) findViewById(R.id.tvVinError);
        tvVinError.setTextColor(Color.RED);

        initVoiceBtn();
        initVinEdittext();
    }

    /**
     * Initial toolbar
     */
    public void initVinKeyboard() {
        Keyboard vinKeyboard = new Keyboard(this, R.xml.keyboard_vin);
        mKeyboardView = (KeyboardView) findViewById(R.id.keyboardview);
        mKeyboardView.setKeyboard(vinKeyboard);

        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
    }
    public void openKeyboard(View v)
    {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if( v!=null)((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {
            if (primaryCode==-1 || primaryCode==100 || primaryCode==101)
                mKeyboardView.setPreviewEnabled(false);
        }
        @Override
        public void onRelease(int primaryCode) {
            mKeyboardView.setPreviewEnabled(true);
        }
        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            int postionCursor = vinEditText.getSelectionStart();
            StringBuffer textValue = new StringBuffer(vinEditText.getText());
            if (primaryCode==-1) {
                if (postionCursor==0)
                    return;
                textValue.deleteCharAt(postionCursor-1);
                vinEditText.setText(textValue);
                vinEditText.setSelection(postionCursor-1);
            }
            else if (primaryCode==100) {
                mKeyboardView.setVisibility(View.GONE);
            }
            else if (primaryCode==101) {
                vinEditText.setText("");
            }
            else {
                if (textValue.length()==17)
                    return;
                if (primaryCode==81 || primaryCode==79)
                    primaryCode = 48;
                if (primaryCode==73)
                    primaryCode = 49;
                textValue.insert(postionCursor, (char)(primaryCode)+"");
                vinEditText.setText(textValue);
                vinEditText.setSelection(postionCursor+1);
            }
        }
        @Override
        public void onText(CharSequence text) {
        }
        @Override
        public void swipeLeft() {
        }
        @Override
        public void swipeRight() {
        }
        @Override
        public void swipeDown() {
        }
        @Override
        public void swipeUp() {
        }
    };
    public void initVinEdittext() {
        vinEditText = (EditText)findViewById(R.id.edittext_vin_number);
        vinEditText.setBackgroundResource(R.drawable.vin_right_border);
        final HttpHandler handler = new HttpHandler();
        final String urlVin = Config.getUrlVin();
        vinEditText.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                openKeyboard(vinEditText);
            }
        });
        vinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (vinEditText.getText().length()==17) {
                    String vin = vinEditText.getText()+"";
                    String url = urlVin+vinEditText.getText();
                    String jSonString = handler.ReadHttpResponse(url);
//                    String jSonString = "{name : \"Mersedes\", description : \"Benz\"}";
                    if (jSonString==null) {
                        tvVinError.setText(getString(R.string.error_vin));
                        vinEditText.setBackgroundResource(R.drawable.vin_error_border);
//                        Toast.makeText(CarNewActivity.this, getString(R.string.error_vin), Toast.LENGTH_LONG).show();
                    }
                    else {
                        tvVinError.setText("");
                        vinEditText.setBackgroundResource(R.drawable.vin_right_border);
                        JSONObject jObject = null;
                        try {
                            jObject = new JSONObject(jSonString);
                            String name = jObject.getString("name");
                            String description = jObject.getString("description");
                            Intent intent = new Intent(CarNewActivity.this, CarFoundActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("name", name);
                            bundle.putString("description", description);
                            bundle.putString("vin", vin);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        catch (Exception e) {
//                            Toast.makeText(CarNewActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else if (vinEditText.getText().length() > 17) {
                    vinEditText.setText(vinEditText.getText().toString().substring(0, 17));
//                    Toast.makeText(CarNewActivity.this, getString(R.string.max_limit), Toast.LENGTH_LONG).show();
                }
                else {
                    tvVinError.setText("");
                    vinEditText.setBackgroundResource(R.drawable.vin_right_border);
                }
            }
        });
    }
    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_car_new);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarNewActivity.this, MainActivity.class));
                finish();
            }
        });


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

                        Intent intent = new Intent(CarNewActivity.this, RecognizerSampleActivity.class);
                        startActivityForResult(intent, RESULT_SPEECH);

                        return true;
                    }
                }
                return false;
            }
        });

    }
//3VWBB61C4WM050210
//45RT78WEDST12
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra("all_results");
                    EditText edt = (EditText) findViewById(R.id.edittext_vin_number);
//                    edt.setText(text.get(0));

//                    String edt_text = "";
//                    if (edt.getText() != null)
//                        edt_text = edt.getText()+"";
                    String edt_text = SpeachRecogn.vinSpeach(text);
//                    Toast.makeText(CarNewActivity.this, text.size()+"", Toast.LENGTH_LONG).show();
//                    String edt_text = data.getExtras().getString("recognated_string");
//                    Toast.makeText(CarNewActivity.this, edt_text, Toast.LENGTH_LONG).show();
                    if (edt_text.length() > 17)
                        edt_text = edt_text.substring(0, 17);
                    edt.setText(edt_text);
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
