package com.usupov.autopark.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.usupov.autopark.R;
import com.usupov.autopark.model.CarBrand;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class SelectBrand extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_brand);

        final List<CarBrand> brandList = new ArrayList<>();

        brandList.add(new CarBrand(1, "Acura", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/Acura-Logo.jpg"));
        brandList.add(new CarBrand(2, "BMW", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/bmw-logo.jpg"));
        brandList.add(new CarBrand(3, "Ferrari", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/ferrari-logo.jpg"));
        brandList.add(new CarBrand(4, "Ford", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/ford-logo.jpg"));
        brandList.add(new CarBrand(5, "Hyundai", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/hyundai-logo.jpg"));
        brandList.add(new CarBrand(6, "Jeep", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/Jeep-Logo-300x120.jpg"));
        brandList.add(new CarBrand(7, "Toyota", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/toyota-logo1.jpg"));
        brandList.add(new CarBrand(8, "ИЖ", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT2FipULUBwKcWWpEZG8WRDkc1kjWU05DTTL1QtSFLASKjG3C3Y"));

        LinearLayout linearLayoutBrandList = (LinearLayout) findViewById(R.id.brandList);
        LayoutInflater inflater = getLayoutInflater();

        for (int i = 0; i < brandList.size(); i++) {
            final CarBrand brandItem = brandList.get(i);
            View brandView = inflater.inflate(R.layout.item_brand, linearLayoutBrandList, false);
            ImageView brandImage = (ImageView) brandView.findViewById(R.id.brandImage);

            Glide.with(this).load(brandItem.getImageUrl()).into(brandImage);

            TextView brandName = (TextView) brandView.findViewById(R.id.brandName);
            brandName.setText(brandItem.getName());

            brandView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("brandId", brandItem.getId());
                    intent.putExtra("brandName", brandItem.getName());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            linearLayoutBrandList.addView(brandView);
        }
    }
}
