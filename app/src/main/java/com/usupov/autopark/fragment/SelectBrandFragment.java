package com.usupov.autopark.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.usupov.autopark.R;
import com.usupov.autopark.model.CatalogBrand;

import java.util.ArrayList;
import java.util.List;

public class SelectBrandFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_brand, container, true);
        int height = getActivity().getWindow().getAttributes().height * 9 / 10;
        height = 500;
        view.setMinimumHeight(height);
        final List<CatalogBrand> brandList = new ArrayList<>();

        brandList.add(new CatalogBrand(1, "Acura", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/Acura-Logo.jpg"));
        brandList.add(new CatalogBrand(2, "BMW", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/bmw-logo.jpg"));
        brandList.add(new CatalogBrand(3, "Ferrari", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/ferrari-logo.jpg"));
        brandList.add(new CatalogBrand(4, "Ford", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/ford-logo.jpg"));
        brandList.add(new CatalogBrand(5, "Hyundai", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/hyundai-logo.jpg"));
        brandList.add(new CatalogBrand(6, "Jeep", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/Jeep-Logo-300x120.jpg"));
        brandList.add(new CatalogBrand(7, "Toyota", "https://8096-presscdn-0-43-pagely.netdna-ssl.com/wp-content/uploads/2014/10/toyota-logo1.jpg"));
        brandList.add(new CatalogBrand(8, "ИЖ", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT2FipULUBwKcWWpEZG8WRDkc1kjWU05DTTL1QtSFLASKjG3C3Y"));


        LinearLayout linearLayoutBrandList = (LinearLayout) view.findViewById(R.id.brandList);

        for (int i = 0; i < brandList.size(); i++) {
            final CatalogBrand brandItem = brandList.get(i);
            View brandView = inflater.inflate(R.layout.item_brand, linearLayoutBrandList, false);
            ImageView brandImage = (ImageView) brandView.findViewById(R.id.brandImage);

            Glide.with(this).load(brandItem.getImageUrl()).into(brandImage);

            TextView brandName = (TextView) brandView.findViewById(R.id.brandName);
            brandName.setText(brandItem.getName());

//            brandView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.putExtra("brandId", brandItem.getId());
//                    intent.putExtra("brandName", brandItem.getName());
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            });
            linearLayoutBrandList.addView(brandView);
        }
        return view;

    }
}
