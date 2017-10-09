package com.usupov.autopark.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import product.card.R;

import com.usupov.autopark.config.LocationURIConstants;
import com.usupov.autopark.config.UserURIConstants;
import com.usupov.autopark.http.Headers;
import com.usupov.autopark.model.UserModel;
import com.usupov.autopark.model.location.City;
import com.usupov.autopark.model.location.District;
import com.usupov.autopark.model.location.Region;
import com.usupov.autopark.model.location.Subway;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UpdateActivity extends BasicActivity {

    @InjectView(R.id.account_email)
    TextView emailTextView;
    @InjectView(R.id.account_name)
    TextView nameTextView;

//    @InjectView(R.id.account_edit_name)
//    EditText nameText;
//    @InjectView(R.id.layout_edit_name)
//    TextInputLayout nameLayout;

    @InjectView(R.id.account_edit_company)
    EditText companyText;
    @InjectView(R.id.layout_edit_company)
    TextInputLayout companyLayout;

    @InjectView(R.id.account_edit_manager)
    EditText managerText;
    @InjectView(R.id.layout_edit_manager)
    TextInputLayout managerLayout;

    @InjectView(R.id.account_edit_phone)
    EditText phoneText;
    @InjectView(R.id.layout_edit_phone)
    TextInputLayout phoneLayout;

    @InjectView(R.id.account_edit_avito_mail)
    ToggleButton avitoMailToggleBtn;

    @InjectView(R.id.account_edit_text_next)
    TextView nextText;

//    private static String name;
    private static String company;
    private static String manager;
    private static String phone;

    UserModel user;
    String regionName, cityName, districtName, subwayName;
    final int QUERY_LOAD_USER = 1, QUERY_LOAD_REGION = 2, QUERY_LOAD_CITY = 3, QUERY_LOAD_DISTRICT = 4, QUERY_LOAD_SUBWAY = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_update, null, false);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.addView(contentView, 0);

        ButterKnife.inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        loadUserData(QUERY_LOAD_USER);
//        nameText.setOnFocusChangeListener(onFocusChangeListener);
        companyText.setOnFocusChangeListener(onFocusChangeListener);
        managerText.setOnFocusChangeListener(onFocusChangeListener);
        phoneText.setOnFocusChangeListener(onFocusChangeListener);

        nextText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toNextPage();
            }
        });

//        LoadUserTask loadTask = new LoadUserTask();
//        loadTask.execute();

    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
//                case R.id.account_edit_name :  nameLayout.setErrorEnabled(!hasFocus); break;
                case R.id.account_edit_company : companyLayout.setErrorEnabled(!hasFocus); break;
                case R.id.account_edit_manager : managerLayout.setErrorEnabled(!hasFocus); break;
                case R.id.account_edit_phone : phoneLayout.setErrorEnabled(!hasFocus); break;
                default: break;
            }
        }
    };

    public void toNextPage() {
        if (!validate()) {
            Toast.makeText(UpdateActivity.this, getString(R.string.error_check_input_data), Toast.LENGTH_LONG).show();
            return;
        }

        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        Intent intent = new Intent(UpdateActivity.this, UpdateLastActivity.class);
//        user.setName(name);
        user.setCompany(company);
        user.setManager(manager);
        user.setPhone(phone);
        user.setAvitoMail(avitoMailToggleBtn.isChecked());
        Gson g = new Gson();
        intent.putExtra("user", g.toJson(user, UserModel.class));
        intent.putExtra("regionName", regionName);
        intent.putExtra("cityName", cityName);
        intent.putExtra("districtName", districtName);
        intent.putExtra("subwayName", subwayName);

        startActivity(intent, bundle);
    }

    public boolean validate() {
        boolean valid = true;

//        name = nameText.getText().toString().trim();
        company = companyText.getText().toString().trim();
        manager = managerText.getText().toString().trim();
        phone = phoneText.getText().toString().trim();

//        if (name.isEmpty()) {
//            nameLayout.setError(getString(R.string.error_name_empty));
//            Toast.makeText(UpdateActivity.this, R.string.error_name_empty, Toast.LENGTH_LONG).show();
//            valid = false;
//        }
//        else if (name.length() < 3) {
//            nameLayout.setError(getString(R.string.error_name));
//            Toast.makeText(UpdateActivity.this, R.string.error_name, Toast.LENGTH_LONG).show();
//            valid = false;
//        }
//        else
//            nameLayout.setError(null);

        if (company.isEmpty()) {
            companyLayout.setError(getString(R.string.error_company_empty));
            valid = false;
        }
        else if (company.length() < 2) {
            companyLayout.setError(getString(R.string.error_company));
            valid = false;
        }
        else
            companyLayout.setError(null);

        if (manager.isEmpty()) {
            managerLayout.setError(getString(R.string.error_manager_empty));
            valid = false;
        }
        else if (manager.length() < 3) {
            managerLayout.setError(getString(R.string.error_manager_error));
            valid = false;
        }
        else
            managerLayout.setError(null);


        if (phone.isEmpty()) {
            phoneLayout.setError(getString(R.string.error_phone_empty));
            valid = false;
        }
        else {
            String phoneOnlyDigits = "";
            boolean phoneRight = true;
            for (int i = 0; i  < phone.length(); i++) {
                if (Character.isDigit(phone.charAt(i)))
                    phoneOnlyDigits += phone.charAt(i);
            }
            phoneRight = false;
            if (phoneOnlyDigits.length()==10
                    || phoneOnlyDigits.length()==11 && (phoneOnlyDigits.charAt(0)=='7' || phoneOnlyDigits.charAt(0)=='8'))
                phoneRight = true;

            if (!phoneRight) {
                phoneLayout.setError(getString(R.string.error_phone_error));
                valid = false;
            }
            else
                phoneLayout.setError(null);
        }
        return valid;
    }

    private void loadUserData(final int typeQuery) {

        String url = null;
        switch (typeQuery) {
            case QUERY_LOAD_USER : url = UserURIConstants.USER_INFO; break;
            case QUERY_LOAD_REGION : url = String.format(LocationURIConstants.GET_REGION_BY_ID, user.getRegionId()); break;
            case QUERY_LOAD_CITY : url = String.format(LocationURIConstants.GET_CITY_BY_ID, user.getRegionId(), user.getCityId()); break;
            case QUERY_LOAD_DISTRICT : url = String.format(LocationURIConstants.GET_DISTRICT_BY_ID, user.getCityId(), user.getDistrictId()); break;
            case QUERY_LOAD_SUBWAY : url = String.format(LocationURIConstants.GET_SUBWAY_BY_ID, user.getCityId(), user.getSubwayId()); break;
            default: break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        findViewById(R.id.layoutUpdate).setVisibility(View.VISIBLE);
                        findViewById(R.id.pbUpdate).setVisibility(View.GONE);
                        Gson g = new Gson();
                        switch (typeQuery) {
                            case QUERY_LOAD_USER : {
                                user = g.fromJson(response, UserModel.class);
                                nameTextView.setText(user.getName());
                                emailTextView.setText(user.getEmail());
                    //          nameText.setText(user.getName());
                                companyText.setText(user.getCompany());
                                managerText.setText(user.getManager());
                                phoneText.setText(user.getPhone());
                                avitoMailToggleBtn.setChecked(user.isAvitoMail());
                                loadUserData(QUERY_LOAD_REGION);
                                loadUserData(QUERY_LOAD_CITY);
                                break;
                            }
                            case QUERY_LOAD_REGION : regionName = g.fromJson(response, Region.class).getName(); break;
                            case QUERY_LOAD_CITY : {
                                City city = g.fromJson(response, City.class);
                                cityName = city.getName();
                                if (city.isHasDistricts())
                                    loadUserData(QUERY_LOAD_DISTRICT);
                                if (city.isHasSubways())
                                    loadUserData(QUERY_LOAD_SUBWAY);
                                break;
                            }
                            case QUERY_LOAD_DISTRICT : districtName = g.fromJson(response, District.class).getName(); break;
                            case QUERY_LOAD_SUBWAY : subwayName = g.fromJson(response, Subway.class).getName(); break;
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Headers.headerMap(getApplicationContext());
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        if (typeQuery != QUERY_LOAD_USER && user.getRegionId()==0)
            return;
        requestQueue.add(stringRequest);
    }
}
