package com.usupov.autopark.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
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
import android.view.ViewManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.adapter.CarsListAdapter;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CarModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

//    @Override
//    protected void onResume() {
//        Toast.makeText(MainActivity.this, "Resumed", Toast.LENGTH_LONG).show();
//    }
    public static TextView textView;
    public static View emptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        initCarsList(true);
        initFabNewCar();
    }
    private void initTryAgain() {
        Button btnTryAgain =(Button)findViewById(R.id.btn_try_again);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCarsList(false);
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
    }
    /**
     * Initial car list
     */
    private static LayoutInflater inflate;
    private static LinearLayout parentLayout;
    public void initCarsList(boolean firstTime) {
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
            return;
        }
        else{
            findViewById(R.id.fab_new_car).setVisibility(View.VISIBLE);
            if (!firstTime) {

                findViewById(R.id.btn_try_again).setVisibility(View.GONE);
                findViewById(R.id.text_again).setVisibility(View.GONE);
                params.gravity = Gravity.TOP;
                parentLayout.setLayoutParams(params);
            }
        }

        List<CarModel> carList = new ArrayList<>();

        RecyclerView recyclerView = null;
        inflate = getLayoutInflater();
        parentLayout = (LinearLayout) findViewById(R.id.layout_car_list);

        textView = (TextView) findViewById(R.id.textView);
        textView.setVisibility(View.VISIBLE);

        HttpHandler handler = new HttpHandler();
//        String url = Config.getMetaData(this, Config.apiUrlCars);
        String url = Config.getUrlCars();
        String jsonStr = handler.ReadHttpResponse(url);
//        String jsonStr = "[{id : \"10\", imageUri : \"https://i.otto.de/i/otto/5431264/rc-auto-jamara-lamborghini-murcielago-lp-670-4-orange.jpg?$formatz$\", name : \"Name\", description : \"Desc\"}]";
        if (jsonStr == null) {
            Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }
        JSONArray carsArray = null;
        try {
            carsArray = new JSONArray(jsonStr);
            recyclerView = (RecyclerView) findViewById(R.id.list_car);
            for (int i = 0; i < carsArray.length(); i++) {
                carList.add(new CarModel(carsArray.getJSONObject(i).getInt("id"), carsArray.getJSONObject(i).getString("imageUri"), carsArray.getJSONObject(i).getString("name"), carsArray.getJSONObject(i).getString("description")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (carList.isEmpty()) {
            emptyListCars();
            return;
        }

        CarsListAdapter adapter = new CarsListAdapter(this, carList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    public static void emptyListCars() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parentLayout.setLayoutParams(params);
        params.gravity = Gravity.CENTER;
        emptyView = inflate.inflate(R.layout.empty_car_list, parentLayout, true);
        textView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void initFabNewCar() {

        FloatingActionButton fabNewCar = (FloatingActionButton) findViewById(R.id.fab_new_car);
        fabNewCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), CarNewActivity.class));
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;

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
