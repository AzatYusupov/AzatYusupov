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
import android.view.View;

import com.usupov.autopark.R;
import com.usupov.autopark.adapter.UserPartListAdapter;
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

                List<CarModel> carList = Car.getCarList(getBaseContext());
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
        List<CarModel> carList = Car.getCarList(getBaseContext());
        Part.getUserPartList(this);
        if (carList == null || carList.isEmpty()) {
            startActivity(new Intent(PartListActivity.this, CarListActivity.class));
            finish();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_part_list);
        toolbar.setTitle("Лента");
    }
}
