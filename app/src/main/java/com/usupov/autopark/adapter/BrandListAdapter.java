package com.usupov.autopark.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.usupov.autopark.R;
import com.usupov.autopark.model.CatalogBrand;

import java.util.List;

/**
 * Created by Azat on 18.04.2017.
 */

public class BrandListAdapter extends ArrayAdapter<CatalogBrand> {
    private Context contex;
    public BrandListAdapter(Context context, int resource, List<CatalogBrand> brandList) {
        super(context, resource, brandList);
        this.contex = context;
    }
    private class ViewHolder {
        ImageView brandImage;
        TextView brandName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CatalogBrand rowItem = getItem(position);

       // LayoutInflater inflater = (LayoutInflater) contex.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView==null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand, parent, false);
            holder = new ViewHolder();
            holder.brandImage = (ImageView) convertView.findViewById(R.id.brandImage);
            holder.brandName = (TextView) convertView.findViewById(R.id.brandName);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();
        holder.brandName.setText(rowItem.getName());
        Glide.with(contex).load(rowItem.getImageUrl()).into(holder.brandImage);

        return convertView;
    }
}
