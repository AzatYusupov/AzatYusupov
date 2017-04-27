package com.usupov.autopark.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.json_to_list.CarNew;
import com.usupov.autopark.model.CatalogBrand;
import com.usupov.autopark.model.CatalogModel;
import com.usupov.autopark.model.CatalogYear;
import com.usupov.autopark.service.SpeachRecogn;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CarNewActivity extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;
    protected static final int REQUEST_ADD = 2;
    TextView tvVinError;
    private KeyboardView mKeyboardView;
    private Keyboard vinKeyboard;
    private EditText vinEditText;
    private Button new_car_find_btn;
    ProgressBar pbSelectCatalog;

    private int selectedBrand = -1;
    private int selectedModel = -1;
    private int selectedYear = -1;
    private int selectedBrandListId = -1;
    private int selectedModelLisId = -1;
    private int selectedYearListId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_new);

        new_car_find_btn = (Button) findViewById(R.id.new_car_find_btn);

        initToolbar();

        initVinKeyboard();

        tvVinError = (TextView) findViewById(R.id.tvVinError);
        tvVinError.setTextColor(Color.RED);

        initVoiceBtn();
        initVinEdittext();
        initCatalogSelect();
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
                            startActivityForResult(intent, REQUEST_ADD);
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
                finish();
            }
        });
    }

    private void initVoiceBtn() {

        final EditText edt = (EditText) findViewById(R.id.edittext_vin_number);
        final ImageView steret_text = (ImageView) findViewById(R.id.steret_text);
        ImageView voice_button = (ImageView) findViewById(R.id.voice_button);
        steret_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.setText("");
            }
        });
        voice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarNewActivity.this, RecognizerSampleActivity.class);
                startActivityForResult(intent, RESULT_SPEECH);
            }
        });
//        edt.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                final int DRAWABLE_LEFT = 0;
//                final int DRAWABLE_TOP = 1;
//                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_BOTTOM = 3;
//
//                if(event.getAction() == MotionEvent.ACTION_UP) {
//
//                    if(event.getRawX() >= (edt.getRight() - edt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//
//                        Intent intent = new Intent(CarNewActivity.this, RecognizerSampleActivity.class);
//                        startActivityForResult(intent, RESULT_SPEECH);
//
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });

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
                    String edt_text = SpeachRecogn.vinSpeach(text).toUpperCase();
                    if (edt_text.length() > 17)
                        edt_text = edt_text.substring(0, 17);
                    edt.setText(edt_text);
                }
                break;
            }
            case REQUEST_ADD: {
                if (resultCode==RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void initCatalogSelect() {
        LinearLayout linearLayoutBrand = (LinearLayout) findViewById(R.id.brand);

        linearLayoutBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List<CatalogBrand> brandList = CarNew.getBradList();
                if (brandList==null) {
                    Toast.makeText(CarNewActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    return;
                }

                final AlertDialog.Builder builderBrand = new AlertDialog.Builder(CarNewActivity.this);
                builderBrand.setTitle(getString(R.string.select_brand));

                String[]brandNames = new String[brandList.size()];
                for (int i = 0; i < brandNames.length; i++) {
                    brandNames[i] = brandList.get(i).getName();
                }
                builderBrand.setSingleChoiceItems(brandNames, selectedBrandListId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedBrand = brandList.get(which).getId();
                        String brandName =  brandList.get(which).getName();
                        TextView tvBrandName = (TextView)findViewById(R.id.selectedBranName);
                        tvBrandName.setTextColor(Color.BLACK);
                        tvBrandName.setText(brandName);

                        TextView tvModelName = (TextView)findViewById(R.id.selectedModelName);
                        tvModelName.setText(getString(R.string.select_model));
                        tvModelName.setTextColor(Color.GRAY);

                        TextView tvYearName = (TextView)findViewById(R.id.selectedYear);
                        tvYearName.setText(getString(R.string.select_year));
                        tvYearName.setTextColor(Color.GRAY);

                        selectedModel = -1;
                        selectedYear =  -1;
                        selectedBrandListId = which;
                        new_car_find_btn.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
                builderBrand.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderBrand.show();
            }
        });
        LinearLayout linearLayoutModel = (LinearLayout) findViewById(R.id.model);
        linearLayoutModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBrand==-1)
                    return;
                final List<CatalogModel> modelList = CarNew.getModels(selectedBrand);
                if (modelList==null) {
                    Toast.makeText(CarNewActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    return;
                }
                final AlertDialog.Builder builderModel = new AlertDialog.Builder(CarNewActivity.this);
                builderModel.setTitle(getString(R.string.select_model));

                String[]modelNames = new String[modelList.size()];
                for (int i = 0; i < modelNames.length; i++) {
                    modelNames[i] = modelList.get(i).getName();
                }
                builderModel.setSingleChoiceItems(modelNames, selectedModelLisId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedModel = modelList.get(which).getId();
                        String brandName =  modelList.get(which).getName();
                        TextView tvBrandName = (TextView)findViewById(R.id.selectedModelName);
                        tvBrandName.setTextColor(Color.BLACK);
                        tvBrandName.setText(brandName);

                        TextView tvYearName = (TextView)findViewById(R.id.selectedYear);
                        tvYearName.setText(getString(R.string.select_year));
                        tvYearName.setTextColor(Color.GRAY);
                        selectedYear =  -1;
                        selectedModelLisId = which;
                        new_car_find_btn.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
                builderModel.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderModel.show();
            }
        });
        LinearLayout linearLayoutYear = (LinearLayout) findViewById(R.id.year);
        linearLayoutYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBrand==-1 || selectedModel==-1)
                    return;
                final List<CatalogYear> yearList = CarNew.getYears(selectedBrand, selectedModel);
                if (yearList==null) {
                    Toast.makeText(CarNewActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    return;
                }

                final AlertDialog.Builder builderYear = new AlertDialog.Builder(CarNewActivity.this);
                builderYear.setTitle(getString(R.string.select_year));

                String[]yearNames = new String[yearList.size()];
                for (int i = 0; i < yearNames .length; i++) {
                    yearNames [i] = yearList.get(i).getName();
                }
                builderYear.setSingleChoiceItems(yearNames, selectedYearListId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedYear = yearList.get(which).getId();
                        String yearName =  yearList.get(which).getName();
                        TextView tvYearName = (TextView)findViewById(R.id.selectedYear);
                        tvYearName.setTextColor(Color.BLACK);
                        tvYearName.setText(yearName);
                        selectedYearListId= which;
                        new_car_find_btn.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                builderYear.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderYear.show();
            }
        });
    }
}
