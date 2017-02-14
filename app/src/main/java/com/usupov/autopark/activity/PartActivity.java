package com.usupov.autopark.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.adapter.CarsListAdapter;
import com.usupov.autopark.model.CarCategories;

import org.w3c.dom.Text;

public class PartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setActionBar(toolbar);

        String carName = "Ford Focus III, 2012";
        TextView carNameText = (TextView) findViewById(R.id.part_car_name);
        carNameText.setText(carName);

        String []names = {"Внутренняя отделка", "Двигатель", "Кузов", "Подвеска амортизации", "Подвеска оси/система подвески колес", "Подготовка топливной системы", "Ременный привод", "Рулевое управление", "Система зажигания, накаливания", "Система охлаждения"};

        LinearLayout linLayout = (LinearLayout)findViewById(R.id.layout_parts);
        LayoutInflater lytinflater = getLayoutInflater();
        CarCategories one = new CarCategories("");

        for (int i = 0; i < names.length; i++) {
            View item = lytinflater.inflate(R.layout.item_part_car, linLayout, false);
            TextView partnName = (TextView) item.findViewById(R.id.part_name);
            partnName.setText(names[i]);
            ImageView partImage = (ImageView) item.findViewById(R.id.part_image);
            partImage.setImageResource(R.drawable.ic_action_do_photo);

            ImageView arrowImage = (ImageView) item.findViewById(R.id.arrow);
            arrowImage.setImageResource(R.drawable.ic_action_arrow_not_opened);
            linLayout.addView(item);

        }
    }

}
