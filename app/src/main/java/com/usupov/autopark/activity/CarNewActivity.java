package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.android.productcard.R;
import com.usupov.autopark.fragment.RecognizerSampleFragment;
import com.usupov.autopark.json.Car;
import com.usupov.autopark.json.CarCat;
import com.usupov.autopark.model.CarModel;
import com.usupov.autopark.model.CatalogBrand;
import com.usupov.autopark.model.CatalogModel;
import com.usupov.autopark.model.CatalogYear;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CarNewActivity extends BasicActivity implements RecognizerSampleFragment.EditNameDialogListener {

    protected static final int REQUEST_ADD = 2;
    TextView tvVinError;
    private EditText vinEditText;
    private Button newCarFindBtn;

    private List<CatalogBrand> brandList;
    private List<CatalogModel> modelList;
    private List<CatalogYear> yearList;
    private final int TASK_LOAD_BRAND = 1, TASK_LOAD_MODEL = 2, TASK_LOAD_YEAR = 3, TASK_LOAD_CAR_BY_VIN = 4;

    private int selectedBrand = -1;
    private int selectedModel = -1;
    private int selectedYear = -1;
    private int selectedBrandListId = -1;
    private int selectedModelLisId = -1;
    private int selectedYearListId = -1;

    LoadCatalogTask loadCatalogTask;
    ProgressDialog progressDialog;
    private String vin;
    private CarModel car;
    private ImageView clearBtnImage, voiceBtnImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_car_new, null, false);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerLayout.addView(contentView, 0);

        newCarFindBtn = (Button) findViewById(R.id.new_car_find_btn);


        tvVinError = (TextView) findViewById(R.id.tvVinError);
        tvVinError.setTextColor(getResources().getColor(R.color.squarecamera__red));

        initVoiceBtn();
        initVinEdittext();
        initCatalogSelect();
        newCarFindBtn.setOnClickListener(catalogFindBtnClick);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_car_add);

        progressDialog = new ProgressDialog(this,
                R.style.AppCompatAlertDialogStyle);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);

        loadCatalogTask = new LoadCatalogTask();
        loadCatalogTask.execute(TASK_LOAD_BRAND);

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
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivityForResult(intent, REQUEST_ADD, bundle);
            }
        }
    };

    public void initVinEdittext() {
        vinEditText = (EditText)findViewById(R.id.edittext_vin_number);
        vinEditText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        vinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = vinEditText.getText().toString();
                if (text.isEmpty())
                    clearBtnImage.setVisibility(View.GONE);
                else
                    clearBtnImage.setVisibility(View.VISIBLE);

                if (text.length()==17) {
                    vin = vinEditText.getText()+"";
                    loadCatalogTask = new LoadCatalogTask();
                    try {
                        loadCatalogTask.execute(TASK_LOAD_CAR_BY_VIN).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if (car==null)
                        tvVinError.setText(getString(R.string.error_vin));
                    else {
                        tvVinError.setText("");
                        Intent intent = new Intent(CarNewActivity.this, CarFoundActivity.class);
                        Gson g = new Gson();
                        intent.putExtra("car", g.toJson(car));
                        startActivityForResult(intent, REQUEST_ADD);
                    }
                }
                else if (text.length() > 17) {
                    vinEditText.setText(text.substring(0, 17));
                }
                else {
//                    if (vinEditText.getText()==null || vinEditText.getText().length()==0)
//                        vinEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_voice_black, 0);
//                    else
//                        vinEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_close, 0);
                    tvVinError.setText("");
                }
            }
        });
    }

    private void initVoiceBtn() {

        clearBtnImage = (ImageView) findViewById(R.id.clearBtnImage);
        clearBtnImage.setVisibility(View.GONE);
        clearBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vinEditText.setText("");
            }
        });

        voiceBtnImage = (ImageView) findViewById(R.id.voiceBtnImage);
        voiceBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                RecognizerSampleFragment speechDialog = RecognizerSampleFragment.newInstance(R.string.yandex_speech_vin);
                FragmentTransaction transaction = manager.beginTransaction();
                speechDialog.setCancelable(true);
                speechDialog.show(transaction, "dialog");
            }
        });

        final EditText edt = (EditText) findViewById(R.id.edittext_vin_number);
        edt.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {

//                    if(event.getRawX() >= (edt.getRight() - edt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                        if (edt.getText()==null || edt.getText().length()==0) {
//                            FragmentManager manager = getSupportFragmentManager();
//                            RecognizerSampleFragment speechDialog = RecognizerSampleFragment.newInstance(R.string.yandex_speech_vin);
//                            FragmentTransaction transaction = manager.beginTransaction();
//                            speechDialog.setCancelable(true);
//                            speechDialog.show(transaction, "dialog");
//                        }
//                        else {
//                            vinEditText.setText("");
//                        }
//                        return true;
//                    }
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

            case REQUEST_ADD: {
                if (resultCode==RESULT_OK) {
                    Intent intent = new Intent(CarNewActivity.this, CarListActivity.class);
                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                    startActivity(intent, bundle);
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
                        loadCatalogTask = new LoadCatalogTask();
                        loadCatalogTask.execute(TASK_LOAD_MODEL);
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
                yearList = null;
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
                        loadCatalogTask = new LoadCatalogTask();
                        loadCatalogTask.execute(TASK_LOAD_YEAR);
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

    @Override
    public void onFinishEditDialog(String resultTextSpeech) {
        if (resultTextSpeech.length() > 17)
            resultTextSpeech = resultTextSpeech.substring(0, 17);
        vinEditText.setText(resultTextSpeech);
    }

    class LoadCatalogTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int param = params[0];
            switch (param) {
                case TASK_LOAD_BRAND : brandList = CarCat.getBradList(getApplicationContext()); break;
                case TASK_LOAD_MODEL : yearList = null; modelList = CarCat.getModels(selectedBrand, getApplicationContext());  break;
                case TASK_LOAD_YEAR : yearList = CarCat.getYears(selectedBrand, selectedModel, getApplicationContext()); break;
                case TASK_LOAD_CAR_BY_VIN : car = Car.getCarByVin(vin, getApplicationContext()); break;
                default: break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            progressDialog.dismiss();
        }
    }
}
