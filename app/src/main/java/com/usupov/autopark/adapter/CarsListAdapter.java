package com.usupov.autopark.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.usupov.autopark.R;
import com.usupov.autopark.activity.MainActivity;
import com.usupov.autopark.model.CarModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class CarsListAdapter extends RecyclerView.Adapter<CarsListAdapter.MyViewHolder> {

    private List<CarModel> carList;
    private Context context;

    public CarsListAdapter(Context context, List<CarModel> list) {
        this.context = context;
        this.carList = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView fullName, description;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            fullName = (TextView) view.findViewById(R.id.item_car_full_name);
            description = (TextView) view.findViewById(R.id.item_car_description);
            thumbnail = (ImageView) view.findViewById(R.id.item_car_thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }
    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }
    };

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.menu_car_main);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.btnCareDelete:
                                //
                                return true;
                            case R.id.btnCarEdit:
                                //
                                return true;

                            default:
                                return false;
                        }
                    }
                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
                //
            }
        });
        popupMenu.show();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        CarModel carListItem = carList.get(position);
        holder.fullName.setText(carListItem.getFullName());
        holder.description.setText(carListItem.getDescription());

        // loading album cover using Glide library
        Glide.with(context).load(carListItem.getImageUrl()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(viewClickListener);
        /*holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

}
