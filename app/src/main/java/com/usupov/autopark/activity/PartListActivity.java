package com.usupov.autopark.activity;

import android.content.Intent;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.usupov.autopark.R;
import com.usupov.autopark.adapter.UserPartListAdapter;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.jsonHelper.Car;
import com.usupov.autopark.jsonHelper.Part;
import com.usupov.autopark.model.CarModel;
import com.usupov.autopark.model.UserPartModel;

import java.util.List;

public class PartListActivity extends AppCompatActivity {

    private RecyclerView rvUserPartList;
    private List<UserPartModel> userPartList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_list);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ifCarListEmpty();

        initToolbar();
        initFabUserPart();
        initEmptyView();
        initRecyclerView();
        initUserPartList();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_part_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            HttpHandler.removeAutToken(getApplicationContext());
            startActivity(new Intent(PartListActivity.this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void initEmptyView() {

    }
    private void initUserPartList() {
        userPartList = Part.getUserPartList(this);
        UserPartListAdapter adapter = new UserPartListAdapter(this, userPartList);
        rvUserPartList.setAdapter(adapter);
    }
    private void initRecyclerView() {
        rvUserPartList  = (RecyclerView) findViewById(R.id.list_user_part);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rvUserPartList.setLayoutManager(mLayoutManager);
        rvUserPartList.setItemAnimator(new DefaultItemAnimator());
        rvUserPartList.setNestedScrollingEnabled(false);
    }
    private void initFabUserPart() {
        FloatingActionButton fabUserPart = (FloatingActionButton) findViewById(R.id.fab_new_user_part);
        fabUserPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<CarModel> carList = Car.getCarList(getApplicationContext());
                if (carList==null || carList.size() != 1) {
                    startActivity(new Intent(PartListActivity.this, CarListActivity.class));
                    finish();
                }
                else {
                    Intent intent = new Intent(PartListActivity.this, PartActivity.class);
                    CarModel car = carList.get(0);
                    intent.putExtra("carName", car.getFullName());
                    intent.putExtra("carId", car.getId());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private  void ifCarListEmpty() {
        List<CarModel> carList = Car.getCarList(getApplicationContext());
        Part.getUserPartList(this);
        if (carList == null || carList.isEmpty()) {
            startActivity(new Intent(PartListActivity.this, CarListActivity.class));
            finish();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_part_list);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Лента");
    }
}
