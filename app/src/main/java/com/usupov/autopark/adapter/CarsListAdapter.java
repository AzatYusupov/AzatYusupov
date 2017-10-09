package com.usupov.autopark.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import product.card.R;
import com.usupov.autopark.activity.CarFoundActivity;
import com.usupov.autopark.activity.CarListActivity;
import com.usupov.autopark.activity.LoginActivity;
import com.usupov.autopark.activity.PartNewActivity;
import com.usupov.autopark.config.CarRestURIConstants;
import com.usupov.autopark.http.Headers;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CarModel;

import org.apache.http.HttpStatus;

import java.util.List;
import java.util.Map;

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
        public ProgressBar progress;

        public MyViewHolder(View view) {
            super(view);
            fullName = (TextView) view.findViewById(R.id.item_car_full_name);
            description = (TextView) view.findViewById(R.id.item_car_description);
            thumbnail = (ImageView) view.findViewById(R.id.item_car_thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            homeView = view.findViewById(R.id.home_view);
            progress = (ProgressBar)view.findViewById(R.id.progress_car);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void showPopupMenu(View v, final int position) {
        final HttpHandler handler = new HttpHandler();
        final String urlCarDelete = CarRestURIConstants.DELETE;

        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.menu_car_main);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.btnCareDelete:
                                new AlertDialog.Builder(context)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Удалить автомобиль")
                                        .setMessage("Вместе с удалением автомобиля из гаража будут удалены все запчасти," +
                                                " добавленные к нему. Вы уверены, что хотите удалить автомобиль "
                                                + carList.get(position).getFullName() + "?")
                                        .setPositiveButton("Да", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final int carId = carList.get(position).getId();
                                                final String carName = carList.get(position).getFullName();

                                                String url = String.format(urlCarDelete, carId);

                                                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                carList.remove(position);
                                                                for (int i = 0; i < CarListActivity.carList.size(); i++) {
                                                                    if (CarListActivity.carList.get(i).getId()==carId) {
                                                                        CarListActivity.carList.remove(i);
                                                                        break;
                                                                    }
                                                                }
                                                                notifyItemRemoved(position);
                                                                notifyDataSetChanged();
                                                                Toast.makeText(context, carName + " " + context.getString(R.string.car_success_deleted), Toast.LENGTH_LONG).show();

                                                                if (carList.isEmpty()) {
                                                                    CarListActivity.tryEmpty();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                if (error.networkResponse != null && (error.networkResponse.statusCode== HttpStatus.SC_UNAUTHORIZED || error.networkResponse.statusCode==HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
                                                                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                                                                            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                                                                    Intent intent = new Intent(context, LoginActivity.class);
                                                                    intent.putExtra("unauthorized", true);
                                                                    context.startActivity(intent, bundle);
                                                                    ((Activity)context).finishAffinity();
                                                                }
                                                                else
                                                                    Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                ) {
                                                    @Override
                                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                                        return Headers.headerMap(context.getApplicationContext());
                                                    }
                                                };
                                                RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                                                requestQueue.add(stringRequest);
                                            }

                                        })
                                        .setNegativeButton("Нет", null)
                                        .show();
                                return true;
                            case R.id.btnCarEdit:
                                Gson g = new Gson();
                                Intent intent = new Intent(context, CarFoundActivity.class);
                                intent.putExtra("car", g.toJson(carList.get(position), CarModel.class));
                                intent.putExtra("update", true);
                                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                                context.startActivity(intent, bundle);
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
        holder.progress.setProgress(carListItem.getPercent());

        Glide.with(context).load(Headers.getUrlWithHeaders(carListItem.getImageUrl(), context.getApplicationContext()))
                .signature(new StringSignature(carListItem.getId() + " " + carListItem.getLastUpdateTime()))
                .into(holder.thumbnail);

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
                Intent intent = new Intent(context, PartNewActivity.class);
                intent.putExtra("carId", carId);
                intent.putExtra("carName", carName);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                context.startActivity(intent, bundle);
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
