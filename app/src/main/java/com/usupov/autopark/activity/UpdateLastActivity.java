package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.usupov.autopark.config.LocationURIConstants;
import com.usupov.autopark.config.UserURIConstants;
import com.usupov.autopark.http.Headers;
import com.usupov.autopark.model.UserModel;
import com.usupov.autopark.model.location.City;
import com.usupov.autopark.model.location.District;
import com.usupov.autopark.model.location.Region;
import com.usupov.autopark.model.location.Subway;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import product.card.R;

public class UpdateLastActivity extends AppCompatActivity {

    UserModel user;
    @InjectView(R.id.account_edit_region)
    TextView regionText;
    @InjectView(R.id.account_edit_city)
    TextView cityText;
    @InjectView(R.id.account_edit_subway)
    TextView subwayText;
    @InjectView(R.id.account_edit_district)
    TextView districtText;
    @InjectView(R.id.account_edit_delivety)
    ToggleButton toggleDelivety;
    @InjectView(R.id.account_edit_warehouse)
    EditText warehouseText;
    @InjectView(R.id.account_edit_prepaid)
    ToggleButton togglePrepaid;
    @InjectView(R.id.apbSave)
    ActionProcessButton btnSave;
    @InjectView(R.id.pbUpdateLast)
    ProgressBar progressUpdateLast;

    @InjectView(R.id.layoutDistrict)
        View layoutSelectDistrict;
    @InjectView(R.id.layoutSubway)
        View layoutSelectSubway;

    List<Region> regionList;
    List<City> cityList;
    List<District> districtList;
    List<Subway> subwayList;

    ProgressDialog progressDialog;

    final int TASK_LOAD_REGIONS = 1, TASK_LOAD_CITIES = 2, TASK_LOAD_DISTRICTS = 3, TASK_LOAD_SUBWAYS = 4;
    int selectedRegion = -1, selectedCity = -1, selectedDistrict = -1, selectedSubway = -1;

    boolean changedSelection = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_last);
        ButterKnife.inject(this);

        progressDialog = new ProgressDialog(this,
                R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle(getString(R.string.please_wait));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        Gson g = new Gson();
        user = g.fromJson(getIntent().getExtras().getString("user"), UserModel.class);

        initLocationInitialValues();
        initSelection();
        initSubmitBtn();
        initToolbar();
    }

    private void initSubmitBtn() {
        final ActionProcessButton actionProcessButton = (ActionProcessButton) findViewById(R.id.apbSave);
        actionProcessButton.setMode(ActionProcessButton.Mode.ENDLESS);

        actionProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRegion==-1) {
                    Toast.makeText(UpdateLastActivity.this, getString(R.string.select_region), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedCity==-1) {
                    Toast.makeText(UpdateLastActivity.this, getString(R.string.select_city), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedDistrict==-1 && cityList.get(selectedCity).isHasDistricts()) {
                    Toast.makeText(UpdateLastActivity.this, getString(R.string.select_district), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedSubway==-1 && cityList.get(selectedCity).isHasSubways()) {
                    Toast.makeText(UpdateLastActivity.this, getString(R.string.select_subway), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (changedSelection) {
                    user.setRegionId(regionList.get(selectedRegion).getId());
                    user.setCityId(cityList.get(selectedCity).getId());
                    if (selectedDistrict != -1)
                        user.setDistrictId(districtList.get(selectedDistrict).getId());
                    else
                        user.setDistrictId(0);
                    if (selectedSubway != -1)
                        user.setSubwayId(subwayList.get(selectedSubway).getId());
                    else
                        user.setSubwayId(0);
                }

                user.setWarehouse(((EditText)findViewById(R.id.account_edit_warehouse)).getText().toString());

                user.setDelivety(((ToggleButton)findViewById(R.id.account_edit_delivety)).isChecked());
                user.setPrepayment(((ToggleButton)findViewById(R.id.account_edit_prepaid)).isChecked());
                System.out.println(user.getRegionId()+" "+user.getCityId());

//                actionProcessButton.setProgress(1);
                progressDialog.show();
                actionProcessButton.setEnabled(false);
                String url = UserURIConstants.USER_UPDATE;

                Gson g = new Gson();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(g.toJson(user, UserModel.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getApplicationContext(), getString(R.string.data_successful_saved), Toast.LENGTH_SHORT).show();
//                                actionProcessButton.setProgress(0);
                                progressDialog.dismiss();
                                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                                startActivity(new Intent(UpdateLastActivity.this, UpdateActivity.class), bundle);
                                finishAffinity();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(UpdateLastActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                VolleyLog.e("Error: ", error.getMessage());
//                                actionProcessButton.setProgress(0);
                                actionProcessButton.setEnabled(true);
                            }
                        }
                        ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        super.getHeaders();
                        Map<String, String> map = Headers.headerMap(getApplicationContext());
                        map.put("Content-Type", "application/json; charset=utf-8");
                        return map;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
            }
        });
    }

    private void initSelection() {
        loadListTask(TASK_LOAD_REGIONS);


        findViewById(R.id.layout_select_region).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (regionList==null) {
                    loadListTask(TASK_LOAD_REGIONS);
                    return;
                }
                final AlertDialog.Builder dialogSelectRegion = new AlertDialog.Builder(UpdateLastActivity.this);
                dialogSelectRegion.setTitle("Выберите регион");
                dialogSelectRegion.setSingleChoiceItems(Region.getRegionNamesAsArray(regionList), selectedRegion, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != selectedRegion) {
                            selectedRegion = which;
                            notifyChangeRegionSelection();
                        }
                        dialog.dismiss();
                    }
                });
                dialogSelectRegion.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialogSelectRegion.show();
            }
        });

        findViewById(R.id.layout_select_city).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRegion==-1)
                    return;
                if (cityList==null) {
                    loadListTask(TASK_LOAD_CITIES);
                    return;
                }
                final AlertDialog.Builder dialogSelectCity = new AlertDialog.Builder(UpdateLastActivity.this);
                dialogSelectCity.setSingleChoiceItems(City.getCityNameAsArray(cityList), selectedCity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != selectedCity) {
                            selectedCity = which;
                            notifyChangeCitySelection();
                        }
                        dialog.dismiss();
                    }
                });
                dialogSelectCity.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialogSelectCity.show();

            }
        });

        findViewById(R.id.layout_select_district).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (districtList==null) {
                    loadListTask(TASK_LOAD_DISTRICTS);
                    return;
                }
                final AlertDialog.Builder dialogSelectDistrict = new AlertDialog.Builder(UpdateLastActivity.this);
                dialogSelectDistrict.setSingleChoiceItems(District.districtNameAsArray(districtList), selectedDistrict, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != selectedDistrict) {
                            selectedDistrict = which;
                            notifyChangeDistrictSelection();
                        }
                        dialog.dismiss();
                    }
                });
                dialogSelectDistrict.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialogSelectDistrict.show();
            }
        });

        findViewById(R.id.layout_select_subway).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subwayList==null) {
                    loadListTask(TASK_LOAD_SUBWAYS);
                    return;
                }
                final AlertDialog.Builder dialogSelectSubway = new AlertDialog.Builder(UpdateLastActivity.this);
                dialogSelectSubway.setSingleChoiceItems(Subway.getSubwayNamesArray(subwayList), selectedSubway, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != selectedSubway) {
                            selectedSubway = which;
                            notifyChangeSubwaySelection();
                        }
                        dialog.dismiss();
                    }
                });
                dialogSelectSubway.show();
            }
        });

    }

    private void loadListTask(final int typeLoadQuery) {
        progressUpdateLast.setVisibility(View.VISIBLE);
        String url = null;
        switch (typeLoadQuery) {
            case TASK_LOAD_REGIONS: {
                url = LocationURIConstants.GET_REGIONS;
                break;
            }
            case TASK_LOAD_CITIES: {
                if (selectedRegion==-2) {
                    if (regionList==null)
                        return;
                    for (int i = 0; i < regionList.size(); i++) {
                        if (regionList.get(i).getId()==user.getRegionId()) {
                            selectedRegion = i;
                            break;
                        }
                    }
                }
                url = String.format(LocationURIConstants.GET_CITIES, regionList.get(selectedRegion).getId());
                break;
            }
            case TASK_LOAD_DISTRICTS: {
                if (selectedCity==-2) {
                    if (cityList==null)
                        return;
                    for (int i = 0; i < cityList.size(); i++) {
                        if (cityList.get(i).getId()==user.getCityId()) {
                            selectedCity = i;
                            break;
                        }
                    }
                }
                url = String.format(LocationURIConstants.GET_DISTRICTS, cityList.get(selectedCity).getId());
                break;
            }
            case TASK_LOAD_SUBWAYS: {
                if (selectedCity==-2) {
                    if (cityList==null)
                        return;
                    for (int i = 0; i < cityList.size(); i++) {
                        if (cityList.get(i).getId()==user.getCityId()) {
                            selectedCity = i;
                            break;
                        }
                    }
                }
                url = String.format(LocationURIConstants.GET_SUBWAYS, cityList.get(selectedCity).getId());
                break;
            }
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressUpdateLast.setVisibility(View.GONE);
                        try {
                            Gson g = new Gson();
                            switch (typeLoadQuery) {
                                case TASK_LOAD_REGIONS: {
                                    regionList = g.fromJson(response, new TypeToken<List<Region>>() {}.getType());
                                    if (selectedCity==-2)
                                        loadListTask(TASK_LOAD_CITIES);
                                    break;
                                }
                                case TASK_LOAD_CITIES: cityList = g.fromJson(response, new TypeToken<List<City>>(){}.getType()); break;
                                case TASK_LOAD_DISTRICTS: districtList = g.fromJson(response, new TypeToken<List<District>>(){}.getType()); break;
                                case TASK_LOAD_SUBWAYS: subwayList = g.fromJson(response, new TypeToken<List<Subway>>(){}.getType()); break;
                                default: break;
                            }

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Headers.headerMap(getApplicationContext());
            }
        };
        //creating a request queue

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //adding the string request to request queue

        requestQueue.add(stringRequest);
    }

    private void initLocationInitialValues() {
        String regionName = getIntent().getExtras().getString("regionName");
        String cityName = getIntent().getExtras().getString("cityName");
        String districtName = getIntent().getExtras().getString("districtName");
        String subwayName = getIntent().getExtras().getString("subwayName");

        if (regionName != null) {
            selectedRegion = -2;
            selectedCity = -2;
            selectedDistrict = -2;
            selectedSubway = -2;
            regionText.setText(regionName);
            regionText.setTextColor(getResources().getColor(R.color.primaryText));
        }
        if (cityName != null) {
            cityText.setText(cityName);
            cityText.setTextColor(getResources().getColor(R.color.primaryText));
        }
        if (districtName != null) {
            findViewById(R.id.layoutDistrict).setVisibility(View.VISIBLE);
            districtText.setText(districtName);
            districtText.setTextColor(getResources().getColor(R.color.primaryText));
        }
        if (subwayName != null) {
            findViewById(R.id.layoutSubway).setVisibility(View.VISIBLE);
            subwayText.setText(subwayName);
            subwayText.setTextColor(getResources().getColor(R.color.primaryText));
        }
        toggleDelivety.setChecked(user.isDelivety());
        warehouseText.setText(user.getWarehouse());
        togglePrepaid.setChecked(user.isPrepayment());
    }

    private void notifyChangeRegionSelection() {
        changedSelection = true;
        TextView regionText = (TextView) findViewById(R.id.account_edit_region);

        if (selectedRegion==-1) {
            regionText.setTextColor(getResources().getColor(R.color.secondaryText));
            cityText.setText("не выбран");
            regionList = null;
        }
        else {
            regionText.setTextColor(getResources().getColor(R.color.primaryText));
            regionText.setText(regionList.get(selectedRegion).getName());
            loadListTask(TASK_LOAD_CITIES);
        }
        layoutSelectDistrict.setVisibility(View.GONE);
        layoutSelectSubway.setVisibility(View.GONE);
        selectedCity = -1;
        notifyChangeCitySelection();
    }

    private void notifyChangeCitySelection() {
        changedSelection = true;
        TextView cityText = (TextView) findViewById(R.id.account_edit_city);
        if (selectedCity==-1) {
            cityText.setTextColor(getResources().getColor(R.color.secondaryText));
            cityText.setText("не выбран");
            cityList = null;
        }
        else {
            cityText.setTextColor(getResources().getColor(R.color.primaryText));
            cityText.setText(cityList.get(selectedCity).getName());
            if (cityList.get(selectedCity).isHasDistricts()) {
                layoutSelectDistrict.setVisibility(View.VISIBLE);
                loadListTask(TASK_LOAD_DISTRICTS);
            }
            else
                layoutSelectDistrict.setVisibility(View.GONE);
            if (cityList.get(selectedCity).isHasSubways()){
                layoutSelectSubway.setVisibility(View.VISIBLE);
                loadListTask(TASK_LOAD_SUBWAYS);
            }
            else
                layoutSelectSubway.setVisibility(View.GONE);
        }
        selectedDistrict = -1;
        selectedSubway = -1;
        notifyChangeDistrictSelection();
        notifyChangeSubwaySelection();
    }

    private void notifyChangeDistrictSelection() {
        changedSelection = true;
        TextView districtText = (TextView) findViewById(R.id.account_edit_district);
        if (selectedDistrict==-1) {
            districtText.setTextColor(getResources().getColor(R.color.secondaryText));
            districtText.setText("не выбран");
            districtList = null;
        }
        else {
            districtText.setTextColor(getResources().getColor(R.color.primaryText));
            districtText.setText(districtList.get(selectedDistrict).getName());
        }
    }

    private void notifyChangeSubwaySelection() {
        changedSelection = true;
        TextView subwayText = (TextView) findViewById(R.id.account_edit_subway);
        if (selectedSubway==-1) {
            subwayText.setTextColor(getResources().getColor(R.color.secondaryText));
            subwayText.setText("не выбран");
            subwayList = null;
        }
        else {
            subwayText.setTextColor(getResources().getColor(R.color.primaryText));
            subwayText.setText(subwayList.get(selectedSubway).getName());
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_udate_last);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
