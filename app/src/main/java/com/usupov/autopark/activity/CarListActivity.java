package com.usupov.autopark.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import product.card.R;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.usupov.autopark.adapter.CarsListAdapter;
import com.usupov.autopark.config.CarRestURIConstants;
import com.usupov.autopark.http.Headers;
import com.usupov.autopark.model.CarModel;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CarListActivity extends BasicActivity{

    public static TextView textView;
    public static View emptyView;
    public static List<CarModel> carList;
    private RecyclerView recyclerView;
    public static TextView tvEmptyCarList;
    public static String msgCarsNotFound, msgCarNotYet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_car_list, null, false);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.addView(contentView, 0);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initToolbar();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_car_list);

        if (getIntent().hasExtra("name")) {
            initFirstIn();
            return;
        }

        initEmptyView();
        initRecyclerview();
        loadCarList();
        initFabNewCar();
    }

    private void loadCarList() {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbCarList);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        String url = CarRestURIConstants.GET_ALL;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson g = new Gson();
                        carList = g.fromJson(response, new TypeToken<List<CarModel>>(){}.getType());
                        for (CarModel car : carList) {
                            car.setFullName();
                        }
                        progressBar.setVisibility(View.GONE);
                        findViewById(R.id.scrollCarList).setVisibility(View.VISIBLE);
                        initCarsList(carList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && (error.networkResponse.statusCode== HttpStatus.SC_UNAUTHORIZED || error.networkResponse.statusCode==HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
                            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                            Intent intent = new Intent(CarListActivity.this, LoginActivity.class);
                            intent.putExtra("unauthorized", true);
                            startActivity(intent, bundle);
                            finishAffinity();
                        }
                        else
                            Toast.makeText(CarListActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
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

    private void initFirstIn() {

        LinearLayout layoutCarList = (LinearLayout) findViewById(R.id.layout_car_list);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        layoutCarList.setLayoutParams(params);

        inflate = getLayoutInflater();
        View greetingView = inflate.inflate(R.layout.greeting, layoutCarList, true);

        TextView greetingName = (TextView) greetingView.findViewById(R.id.greeting_name);
        String name = getIntent().getStringExtra("name");
        greetingName.setText("Привет, "+name+"!");

        initFabNewCar();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

//    private boolean initInternetConnection(boolean firstTime) {
//        parentLayout = (LinearLayout) findViewById(R.id.layout_car_list);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        if (!isNetworkAvailable()) {
//            findViewById(R.id.fab_new_car).setVisibility(View.GONE);
//            if (firstTime) {
//                LayoutInflater.from(this).inflate(R.layout.no_internet_conn, (LinearLayout) findViewById(R.id.layout_car_list), true);
//                findViewById(R.id.textView).setVisibility(View.GONE);
//                params.gravity = Gravity.CENTER;
//                parentLayout.setLayoutParams(params);
//                initTryAgain();
//            }
//            return false;
//        }
//        else{
//            pbMain.setVisibility(View.VISIBLE);
//            MyTask mt = new MyTask();
//            mt.execute();
//            findViewById(R.id.fab_new_car).setVisibility(View.VISIBLE);
//            if (!firstTime) {
//                findViewById(R.id.btn_try_again).setVisibility(View.GONE);
//                findViewById(R.id.text_again).setVisibility(View.GONE);
//                params.gravity = Gravity.TOP;
//                parentLayout.setLayoutParams(params);
//            }
//            return true;
//        }
//    }
//
//    private void initTryAgain() {
//        Button btnTryAgain =(Button)findViewById(R.id.btn_try_again);
//        btnTryAgain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initInternetConnection(false);
//            }
//        });
//    }
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }
    /**
     * Initial toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_car_list);
        toolbar.setTitle("Гараж");
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Initial car list
     */
    private static LayoutInflater inflate;
    private static LinearLayout parentLayout;

    private void  initRecyclerview() {
        recyclerView = (RecyclerView) findViewById(R.id.list_car);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
    }

    public void initCarsList(List<CarModel> carList1) {
        if (carList==null)
            carList = new ArrayList<>();
        if (carList1==null)
            carList1 = new ArrayList<>();
        tvEmptyCarList.setText("");
        textView.setVisibility(View.VISIBLE);
        CarsListAdapter adapter = new CarsListAdapter(this, carList1);
        recyclerView.setAdapter(adapter);

        if (carList.isEmpty()) {
            textView.setVisibility(View
                    .GONE);
            tvEmptyCarList.setText(getString(R.string.car_list_empty));
            return;
        }
        if (carList1.isEmpty()) {
            textView.setVisibility(View.GONE);
            tvEmptyCarList.setText(getString(R.string.car_list_empty1));
            return;
        }
    }

    private void initEmptyView() {
        textView = (TextView) findViewById(R.id.textView);
        msgCarsNotFound = getString(R.string.car_list_empty1);
        msgCarNotYet = getString(R.string.car_list_empty);

        inflate = getLayoutInflater();
        parentLayout = (LinearLayout) findViewById(R.id.layout_car_list);
        emptyView = inflate.inflate(R.layout.empty_car_list, parentLayout, true);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parentLayout.setLayoutParams(params);
//        params.gravity = Gravity.CENTER;
        tvEmptyCarList = (TextView) emptyView.findViewById(R.id.empty_car_list);
    }

    public static void tryEmpty() {
        textView.setVisibility(View.GONE);
        if (carList.isEmpty()) {
            tvEmptyCarList.setText(msgCarNotYet);
            return;
        }
        tvEmptyCarList.setText(msgCarsNotFound);
    }

    private void initFabNewCar() {

        FloatingActionButton fabNewCar = (FloatingActionButton) findViewById(R.id.fab_new_car);
        fabNewCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CarNewActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(intent, bundle);
                finish();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case requestCodeCarNew :
//                if (resultCode==RESULT_OK) {
//                    finish();
//                    Intent intent = getIntent();
//                    if (intent.hasExtra("name"))
//                        intent.removeExtra("name");
//                    startActivity(intent);
//                }
//                else {
//                }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtreCars(newText);
                return false;
            }
        });

        return true;
    }

    private void filtreCars(String matchString) {
        if(carList == null)
            return;
        matchString = matchString.toLowerCase().trim();
        List<CarModel> filteredCarList = new ArrayList<>();
        for (CarModel car : carList) {
            String fulName = car.getFullName().toLowerCase();
            if (fulName.indexOf(matchString) >= 0) {
                filteredCarList.add(car);
            }
        }
        initCarsList(filteredCarList);
    }


}
