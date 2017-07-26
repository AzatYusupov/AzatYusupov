package com.usupov.autopark.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.usupov.autopark.R;
import com.usupov.autopark.json.Car;
import com.usupov.autopark.json.CarCat;
import com.usupov.autopark.model.CarModel;
import com.usupov.autopark.model.CatalogBrand;
import com.usupov.autopark.model.CatalogModel;
import com.usupov.autopark.model.CatalogYear;
import com.usupov.autopark.service.SpeachRecogn;

import java.util.ArrayList;
import java.util.List;

public class CarNewActivity extends BasicActivity {

    protected static final int RESULT_SPEECH = 1;
    protected static final int REQUEST_ADD = 2;
    TextView tvVinError;
    private KeyboardView mKeyboardView;
    private EditText vinEditText;
    private Button newCarFindBtn;

    private int selectedBrand = -1;
    private int selectedModel = -1;
    private int selectedYear = -1;
    private int selectedBrandListId = -1;
    private int selectedModelLisId = -1;
    private int selectedYearListId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_car_new, null, false);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.addView(contentView, 0);
//        setContentView(R.layout.activity_car_new);

        newCarFindBtn = (Button) findViewById(R.id.new_car_find_btn);

        initToolbar();

        initVinKeyboard();

        tvVinError = (TextView) findViewById(R.id.tvVinError);
        tvVinError.setTextColor(Color.RED);

        initVoiceBtn();
        initVinEdittext();
        initCatalogSelect();
        newCarFindBtn.setOnClickListener(catalogFindBtnClick);
    }
    private View.OnClickListener catalogFindBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CarModel car = Car.getCarByCatalog(selectedBrand, selectedModel, selectedYear, getApplicationContext());
            if (car==null)
                Toast.makeText(CarNewActivity.this, getString(R.string.by_catalog_not_found), Toast.LENGTH_LONG).show();
            else {
                Intent intent = new Intent(CarNewActivity.this, CarFoundActivity.class);
                Gson g = new Gson();
                intent.putExtra("car", g.toJson(car));
                startActivityForResult(intent, REQUEST_ADD);
            }
        }
    };
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
        if( v!=null)((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
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
                    CarModel car = Car.getCarByVin(vin, getApplicationContext());
                    if (car==null) {
                        tvVinError.setText(getString(R.string.error_vin));
                        vinEditText.setBackgroundResource(R.drawable.vin_error_border);
                    }
                    else {
                        tvVinError.setText("");
                        vinEditText.setBackgroundResource(R.drawable.vin_right_border);
                        Intent intent = new Intent(CarNewActivity.this, CarFoundActivity.class);
                        Gson g = new Gson();
                        intent.putExtra("car", g.toJson(car));
                        startActivityForResult(intent, REQUEST_ADD);
                    }
                }
                else if (vinEditText.getText().length() > 17) {
                    vinEditText.setText(vinEditText.getText().toString().substring(0, 17));
                }
                else {
                    if (vinEditText.getText()==null || vinEditText.getText().length()==0)
                        vinEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_voice_black, 0);
                    else
                        vinEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_close, 0);
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
        edt.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {

                    if(event.getRawX() >= (edt.getRight() - edt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (edt.getText()==null || edt.getText().length()==0) {
                            Intent intent = new Intent(CarNewActivity.this, RecognizerSampleActivity.class);
                            startActivityForResult(intent, RESULT_SPEECH);
                        }
                        else {
                            vinEditText.setText("");
                        }
                        return true;
                    }
                    else {
                        openKeyboard(vinEditText);
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
                    String edt_text = SpeachRecogn.vinSpeach(text, this).toUpperCase();
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

                final List<CatalogBrand> brandList = CarCat.getBradList(getApplicationContext());
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
                        newCarFindBtn.setVisibility(View.GONE);
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
                final List<CatalogModel> modelList = CarCat.getModels(selectedBrand, getApplicationContext());
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
                        newCarFindBtn.setVisibility(View.GONE);
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
                final List<CatalogYear> yearList = CarCat.getYears(selectedBrand, selectedModel, getApplicationContext());
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
                        newCarFindBtn.setVisibility(View.VISIBLE);
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
