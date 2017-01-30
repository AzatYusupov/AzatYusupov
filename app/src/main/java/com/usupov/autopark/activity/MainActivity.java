package com.usupov.autopark.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.usupov.autopark.R;
import com.usupov.autopark.adapter.CarsListAdapter;
import com.usupov.autopark.model.CarModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        initCarsList();

        initFabNewCar();

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
    private void initCarsList() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_car);

        List<CarModel> carList = new ArrayList<>();
        carList.add(new CarModel(
                "http://img.autobytel.com/car-reviews/autobytel/11694-good-looking-sports-cars/2016-Ford-Mustang-GT-burnout-red-tire-smoke.jpg",
                "Ford Focus III, 2012",
                "1.6 AMT (125 л.с) бензин, передний привод, хэтчбек 5 дв."
        ));
        carList.add(new CarModel(
                "http://img.autobytel.com/car-reviews/autobytel/11694-good-looking-sports-cars/2016-Ford-Mustang-GT-burnout-red-tire-smoke.jpg",
                "Hyundai Coupe II (GK)",
                "2.0 AT (150 л.с) бензин, задний привод, хэтчбек 5 дв."
        ));
        carList.add(new CarModel(
                "http://img.autobytel.com/car-reviews/autobytel/11694-good-looking-sports-cars/2016-Ford-Mustang-GT-burnout-red-tire-smoke.jpg",
                "ИЖ 2717",
                "1.6 МТ (73 л.с.) бензин, задний привод, фургон"
        ));
        carList.add(new CarModel(
                "http://img.autobytel.com/car-reviews/autobytel/11694-good-looking-sports-cars/2016-Ford-Mustang-GT-burnout-red-tire-smoke.jpg",
                "Mitsubishi Outlander III",
                "2.0 AT (146 л.с) бензин, передний привод, внедорожник 5 дв."
        ));
        carList.add(new CarModel(
                "http://img.autobytel.com/car-reviews/autobytel/11694-good-looking-sports-cars/2016-Ford-Mustang-GT-burnout-red-tire-smoke.jpg",
                "Ford Focus III, 2012",
                "1.6 AMT (125 л.с) бензин, передний привод, хэтчбек 5 дв."
        ));
        carList.add(new CarModel(
                "http://img.autobytel.com/car-reviews/autobytel/11694-good-looking-sports-cars/2016-Ford-Mustang-GT-burnout-red-tire-smoke.jpg",
                "Hyundai Coupe II (GK)",
                "2.0 AT (150 л.с) бензин, задний привод, хэтчбек 5 дв."
        ));
        carList.add(new CarModel(
                "http://img.autobytel.com/car-reviews/autobytel/11694-good-looking-sports-cars/2016-Ford-Mustang-GT-burnout-red-tire-smoke.jpg",
                "ИЖ 2717",
                "1.6 МТ (73 л.с.) бензин, задний привод, фургон"
        ));
        carList.add(new CarModel(
                "http://img.autobytel.com/car-reviews/autobytel/11694-good-looking-sports-cars/2016-Ford-Mustang-GT-burnout-red-tire-smoke.jpg",
                "Mitsubishi Outlander III",
                "2.0 AT (146 л.с) бензин, передний привод, внедорожник 5 дв."
        ));

        CarsListAdapter adapter = new CarsListAdapter(this, carList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.setNestedScrollingEnabled(false);

    }

    private void initFabNewCar() {

        FloatingActionButton fabNewCar = (FloatingActionButton) findViewById(R.id.fab_new_car);
        fabNewCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), CarNewActivity.class));
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
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);

    }

}
