package com.usupov.autopark.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.adapter.CarsListAdapter;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CarModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

//    @Override
//    protected void onResume() {
//        Toast.makeText(MainActivity.this, "Resumed", Toast.LENGTH_LONG).show();
//    }
    public static TextView textView;
    public static View emptyView;
    private ProgressBar pbMain;
    public static List<CarModel> carList;
    private RecyclerView recyclerView;
    public static TextView tvEmptyCarList;
    public static String msgCarsNotFound, msgCarNotYet;

    private final int requestCodeCarNew = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        carList = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        pbMain = (ProgressBar) findViewById(R.id.pbMain);

        initToolbar();
        initEmptyView();
        initRecyclerview();
        initInternetConnection(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class MyTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return getCarList();
        }
        @Override
        protected void onPostExecute(Boolean ok) {
            super.onPostExecute(ok);
            if (!ok) {
                Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            }
            pbMain.setVisibility(View.GONE);
            initCarsList(carList);
            initFabNewCar();
        }
    }

    private boolean initInternetConnection(boolean firstTime) {
        parentLayout = (LinearLayout) findViewById(R.id.layout_car_list);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (!isNetworkAvailable()) {
            findViewById(R.id.fab_new_car).setVisibility(View.GONE);
            if (firstTime) {
                LayoutInflater.from(this).inflate(R.layout.no_internet_conn, (LinearLayout) findViewById(R.id.layout_car_list), true);
                findViewById(R.id.textView).setVisibility(View.GONE);
                params.gravity = Gravity.CENTER;
                parentLayout.setLayoutParams(params);
                initTryAgain();
            }
            return false;
        }
        else{
            pbMain.setVisibility(View.VISIBLE);
            MyTask mt = new MyTask();
            mt.execute();
            findViewById(R.id.fab_new_car).setVisibility(View.VISIBLE);
            if (!firstTime) {
                findViewById(R.id.btn_try_again).setVisibility(View.GONE);
                findViewById(R.id.text_again).setVisibility(View.GONE);
                params.gravity = Gravity.TOP;
                parentLayout.setLayoutParams(params);
            }
            return true;
        }
    }
    private void initTryAgain() {
        Button btnTryAgain =(Button)findViewById(R.id.btn_try_again);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initInternetConnection(false);
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    /**
     * Initial toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    private boolean getCarList() {
        carList = new ArrayList<>();
        HttpHandler handler = new HttpHandler();
//        String url = Config.getMetaData(this, Config.apiUrlCars);
        String url = Config.getUrlCars();
        String jsonStr = handler.ReadHttpResponse(url);
//        String jsonStr = "[{id : \"10\", imageUri : \"https://i.otto.de/i/otto/5431264/rc-auto-jamara-lamborghini-murcielago-lp-670-4-orange.jpg?$formatz$\", name : \"Name\", description : \"Desc\"}]";
        if (jsonStr == null) {
            return false;
        }
        JSONArray carsArray = null;
        try {
            carsArray = new JSONArray(jsonStr);
            for (int i = 0; i < carsArray.length(); i++) {
                carList.add(new CarModel(carsArray.getJSONObject(i).getInt("id"), carsArray.getJSONObject(i).getString("imageUri"), carsArray.getJSONObject(i).getString("name"), carsArray.getJSONObject(i).getString("description")));
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void  initRecyclerview() {
        recyclerView = (RecyclerView) findViewById(R.id.list_car);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
    }
    public void initCarsList(List<CarModel> carList1) {
        tvEmptyCarList.setText("");
        textView.setVisibility(View.VISIBLE);
        CarsListAdapter adapter = new CarsListAdapter(this, carList1);
        recyclerView.setAdapter(adapter);

        if (carList.isEmpty()) {
            textView.setVisibility(View.GONE);
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
                startActivityForResult(new Intent(v.getContext(), CarNewActivity.class), requestCodeCarNew);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case requestCodeCarNew :
                if (resultCode==RESULT_OK) {
                    finish();
                    startActivity(getIntent());
                }
                else {
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        return super.onOptionsItemSelected(item);

    }

}
