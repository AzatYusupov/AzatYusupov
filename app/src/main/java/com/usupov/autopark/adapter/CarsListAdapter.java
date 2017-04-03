package com.usupov.autopark.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DecorContentParent;
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
import com.usupov.autopark.activity.PartActivity;
import com.usupov.autopark.activity.PartsInActivity;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CarModel;

import java.util.List;
import java.util.logging.Handler;

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
        public View homeView;

        public MyViewHolder(View view) {
            super(view);
            fullName = (TextView) view.findViewById(R.id.item_car_full_name);
            description = (TextView) view.findViewById(R.id.item_car_description);
            thumbnail = (ImageView) view.findViewById(R.id.item_car_thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            homeView = view.findViewById(R.id.home_view);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void showPopupMenu(View v, final int position) {
        final HttpHandler handler = new HttpHandler();
        final String urlCarDelete = Config.getUrlCarDelete();

        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.menu_car_main);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.btnCareDelete:
                                System.out.println("Deleted car id = "+carList.get(position).getId());
                                boolean result = handler.deleteQuery(urlCarDelete+carList.get(position).getId());
                                String message = carList.get(position).getFullName()+" ";
                                if (result) {
                                    carList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                    message += context.getString(R.string.car_success_deleted);
                                    System.out.println("Removed id : "+position);
                                    if (carList.isEmpty()) {
                                       MainActivity.emptyListCars();
                                    }
                                }
                                else
                                    message += context.getString(R.string.car_not_deleted);
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        CarModel carListItem = carList.get(position);
        holder.fullName.setText(carListItem.getFullName());
        holder.description.setText(carListItem.getDescription());

        // loading album cover using Glide library
        Glide.with(context).load(carListItem.getImageUrl()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                showPopupMenu(v, position);
            }
        });
        holder.homeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int carId = carList.get(position).getId();
                String carName = carList.get(position).getFullName();
                Intent intent = new Intent(context, PartActivity.class);
                intent.putExtra("carId", carId);
                intent.putExtra("carName", carName);
                context.startActivity(intent);
                
            }
        });
        /*holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });*/

    }

    @Override
    public int getItemCount() { return carList.size(); }

}
