package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.gson.Gson;
import product.card.R;

import com.google.gson.reflect.TypeToken;
import com.usupov.autopark.config.CarRestURIConstants;
import com.usupov.autopark.config.CatalogRestURIConstants;
import com.usupov.autopark.fragment.RecognizerSampleFragment;
import com.usupov.autopark.http.Headers;
import com.usupov.autopark.json.Car;
import com.usupov.autopark.model.CarModel;
import com.usupov.autopark.model.CatalogBrand;
import com.usupov.autopark.model.CatalogModel;
import com.usupov.autopark.model.CatalogYear;

import org.apache.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CarNewActivity extends BasicActivity implements RecognizerSampleFragment.EditNameDialogListener {

    protected static final int REQUEST_ADD = 2;
    TextView tvVinError;
    private EditText vinEditText;


    private List<CatalogBrand> brandList;
    private List<CatalogModel> modelList;
    private List<CatalogYear> yearList;
    private final int TASK_LOAD_BRAND = 1, TASK_LOAD_MODEL = 2, TASK_LOAD_YEAR = 3, TASK_LOAD_CAR_BY_VIN = 4;

    private int selectedBrand = -1;
    private int selectedModel = -1;
    private int selectedYear = -1;

    private String vin;
    private CarModel car;
    private ImageView clearBtnImage, voiceBtnImage;
    ProgressBar progressBar;
    ActionProcessButton apbFindCar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_car_new, null, false);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerLayout.addView(contentView, 0);

        progressDialog = new ProgressDialog(this,
                R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle(getString(R.string.please_wait));

        tvVinError = (TextView) findViewById(R.id.tvVinError);
        tvVinError.setTextColor(getResources().getColor(R.color.squarecamera__red));

        initVoiceBtn();
        initVinEdittext();
        initActioProcessButtonFindCar();
        initCatalogSelect();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_car_add);

        progressBar = (ProgressBar) findViewById(R.id.progress_select_catalog);
        loadListTask(TASK_LOAD_BRAND);
    }

    private void initActioProcessButtonFindCar() {

        apbFindCar = (ActionProcessButton) findViewById(R.id.apbFindCar);
        apbFindCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                apbFindCar.setProgress(1);
                progressDialog.show();
                apbFindCar.setEnabled(false);
                String url = String.format(CarRestURIConstants.GET_BY_CATALOG, brandList.get(selectedBrand).getId(),
                        modelList.get(selectedModel).getId(), yearList.get(selectedYear).getId());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(CarNewActivity.this, CarFoundActivity.class);
                                intent.putExtra("car", response);
                                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                                startActivityForResult(intent, REQUEST_ADD, bundle);
//                                apbFindCar.setProgress(0);
                                apbFindCar.setEnabled(true);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                if (error.networkResponse != null && (error.networkResponse.statusCode== HttpStatus.SC_UNAUTHORIZED || error.networkResponse.statusCode==HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
                                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                                    Intent intent = new Intent(CarNewActivity.this, LoginActivity.class);
                                    intent.putExtra("unauthorized", true);
                                    startActivity(intent, bundle);
                                    finishAffinity();
                                }
                                else {
                                    Toast.makeText(CarNewActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                                }
//                                apbFindCar.setProgress(0);
                                apbFindCar.setEnabled(true);
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return  Headers.headerMap(getApplicationContext());
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        });

    }

    private void loadListTask(final int typeLoad) {
        progressBar.setVisibility(View.VISIBLE);
        String url = null;
        switch (typeLoad) {
            case TASK_LOAD_BRAND : url = CatalogRestURIConstants.BRAND_GET_ALL; break;
            case TASK_LOAD_MODEL : url = String.format(CatalogRestURIConstants.MODEL_GET_ALL, brandList.get(selectedBrand).getId()); break;
            case TASK_LOAD_YEAR : url = String.format(CatalogRestURIConstants.YEAR_GET_ALL, brandList.get(selectedBrand).getId(), modelList.get(selectedModel).getId()); break;
            case TASK_LOAD_CAR_BY_VIN : url = String.format(CarRestURIConstants.GET_BY_VIN, vin); break;
            default: break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson g = new Gson();
                        switch (typeLoad) {
                            case TASK_LOAD_BRAND : brandList = g.fromJson(response, new TypeToken<List<CatalogBrand>>(){}.getType()); break;
                            case TASK_LOAD_MODEL : modelList = g.fromJson(response, new TypeToken<List<CatalogModel>>(){}.getType()); break;
                            case TASK_LOAD_YEAR : yearList = g.fromJson(response, new TypeToken<List<CatalogYear>>(){}.getType()); break;
                            case TASK_LOAD_CAR_BY_VIN : {
                                car = g.fromJson(response, CarModel.class);
                                if (car==null)
                                    tvVinError.setText(getString(R.string.error_vin));
                                else {
                                    tvVinError.setText("");
                                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                                    Intent intent = new Intent(CarNewActivity.this, CarFoundActivity.class);
                                    intent.putExtra("car", g.toJson(car));
                                    startActivityForResult(intent, REQUEST_ADD, bundle);
                                }
                                break;
                            }
                            default: break;
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && (error.networkResponse.statusCode== HttpStatus.SC_UNAUTHORIZED || error.networkResponse.statusCode==HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
                            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                            Intent intent = new Intent(CarNewActivity.this, LoginActivity.class);
                            intent.putExtra("unauthorized", true);
                            startActivity(intent, bundle);
                            finishAffinity();
                        }
                        else
                            Toast.makeText(CarNewActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Headers.headerMap(getApplicationContext());
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

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
                if (text==null || text.isEmpty()) {
                    clearBtnImage.setVisibility(View.GONE);
                    return;
                }
                else
                    clearBtnImage.setVisibility(View.VISIBLE);

                if (text.length()==17) {
                    vin = vinEditText.getText()+"";
                    loadListTask(TASK_LOAD_CAR_BY_VIN);


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
                    loadListTask(TASK_LOAD_BRAND);
                    return;
                }

                final AlertDialog.Builder builderBrand = new AlertDialog.Builder(CarNewActivity.this);
                builderBrand.setTitle(getString(R.string.select_brand));

                builderBrand.setSingleChoiceItems(CatalogBrand.getNamesArray(brandList), selectedBrand, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectedBrand != which) {
                            selectedBrand = which;
                            notifyBrandChange();
                        }
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
                if (modelList==null) {
                    loadListTask(TASK_LOAD_MODEL);
                    return;
                }
                final AlertDialog.Builder builderModel = new AlertDialog.Builder(CarNewActivity.this);
                builderModel.setTitle(getString(R.string.select_model));

                builderModel.setSingleChoiceItems(CatalogModel.getNamesArray(modelList), selectedModel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != selectedModel) {
                            selectedModel = which;
                            notifyModelChange();
                        }
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
                if (selectedModel==-1)
                    return;
                if (yearList==null) {
                    loadListTask(TASK_LOAD_YEAR);
                    return;
                }
                final AlertDialog.Builder builderYear = new AlertDialog.Builder(CarNewActivity.this);
                builderYear.setTitle(getString(R.string.select_year));

                builderYear.setSingleChoiceItems(CatalogYear.getNamesArray(yearList), selectedYear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       if (which != selectedYear) {
                           selectedYear = which;
                           notifyYearChange();
                       }
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

    private void notifyBrandChange() {
        TextView brandNameText = (TextView) findViewById(R.id.selectedBranName);
        if (selectedBrand==-1) {
            brandNameText.setTextColor(getResources().getColor(R.color.secondaryText));
            brandNameText.setText("не выбрана");
            brandList = null;
        }
        else {
            brandNameText.setTextColor(getResources().getColor(R.color.primaryText));
            brandNameText.setText(brandList.get(selectedBrand).getName());
            loadListTask(TASK_LOAD_MODEL);
        }
        selectedModel = -1;
        notifyModelChange();
    }

    private void notifyModelChange() {
        TextView modelNameText = (TextView) findViewById(R.id.selectedModelName);
        if (selectedModel==-1) {
            modelNameText.setTextColor(getResources().getColor(R.color.secondaryText));
            modelNameText.setText("не выбрана");
            modelList = null;
        }
        else {
            modelNameText.setTextColor(getResources().getColor(R.color.primaryText));
            modelNameText.setText(modelList.get(selectedModel).getName());
            loadListTask(TASK_LOAD_YEAR);
        }
        selectedYear = -1;
        notifyYearChange();
    }

    private void notifyYearChange() {
        TextView yearNameText = (TextView) findViewById(R.id.selectYearName);
        if (selectedYear==-1) {
            yearNameText.setText("не выбран");
            yearNameText.setTextColor(getResources().getColor(R.color.secondaryText));
            apbFindCar.setVisibility(View.GONE);
            yearList = null;
        }
        else {
            yearNameText.setText(yearList.get(selectedYear).getName());
            yearNameText.setTextColor(getResources().getColor(R.color.primaryText));
            apbFindCar.setVisibility(View.VISIBLE);
            apbFindCar.setProgress(0);
        }
    }

    @Override
    public void onFinishEditDialog(String resultTextSpeech, String word) {
        if (resultTextSpeech.length() > 17)
            resultTextSpeech = resultTextSpeech.substring(0, 17);
        vinEditText.setText(resultTextSpeech);
    }

}
