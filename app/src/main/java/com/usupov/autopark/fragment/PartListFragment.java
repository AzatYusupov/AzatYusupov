package com.usupov.autopark.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.usupov.autopark.R;
import com.usupov.autopark.activity.CarListActivity;
import com.usupov.autopark.activity.PartActivity;
import com.usupov.autopark.adapter.UserPartListAdapter;
import com.usupov.autopark.json.Car;
import com.usupov.autopark.json.Part;
import com.usupov.autopark.model.CarModel;
import com.usupov.autopark.model.UserPartModel;

import java.util.List;


public class PartListFragment extends Fragment {

    private RecyclerView rvUserPartList;
    private List<UserPartModel> userPartList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_part_list, null, false);
        DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawerLayout.addView(contentView, 0);

        initFabUserPart();

        ifCarListEmpty();

        initEmptyView();
        initRecyclerView();
        initUserPartList();

    }

    private void initEmptyView() {

    }
    private void initUserPartList() {
        userPartList = Part.getUserPartList(getActivity().getApplicationContext());
        UserPartListAdapter adapter = new UserPartListAdapter(getActivity(), userPartList);
        rvUserPartList.setAdapter(adapter);
    }
    private void initRecyclerView() {
        rvUserPartList  = (RecyclerView) getActivity().findViewById(R.id.list_user_part);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rvUserPartList.setLayoutManager(mLayoutManager);
        rvUserPartList.setItemAnimator(new DefaultItemAnimator());
        rvUserPartList.setNestedScrollingEnabled(false);
    }
    private void initFabUserPart() {
        FloatingActionButton fabUserPart = (FloatingActionButton) getActivity().findViewById(R.id.fab_new_user_part);
        fabUserPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<CarModel> carList = Car.getCarList(getActivity().getApplicationContext());
                if (carList==null || carList.size() != 1) {
                    startActivity(new Intent(getActivity(), CarListActivity.class));
                    getActivity().finish();
                }
                else {
                    Intent intent = new Intent(getActivity(), PartActivity.class);
                    CarModel car = carList.get(0);
                    intent.putExtra("carName", car.getFullName());
                    intent.putExtra("carId", car.getId());
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }

    private  void ifCarListEmpty() {
        List<CarModel> carList = Car.getCarList(getActivity().getApplicationContext());
        Part.getUserPartList(getActivity());
        if (carList == null || carList.isEmpty()) {
//            startActivity(new Intent(PartListActivity.this, CarListActivity.class));
//            finish();
        }
    }

}
