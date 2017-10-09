package com.usupov.autopark.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import product.card.R;

import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.usupov.autopark.activity.ApplicableListActivity;
import com.usupov.autopark.activity.CarNewActivity;
import com.usupov.autopark.activity.LoginActivity;
import com.usupov.autopark.activity.PartFoundActivity;
import com.usupov.autopark.activity.PartListActivity;
import com.usupov.autopark.config.PartRestURIConstants;
import com.usupov.autopark.http.Headers;
import com.usupov.autopark.model.CarModel;
import com.usupov.autopark.model.UserPartModel;

import org.apache.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class UserPartListAdapter extends RecyclerView.Adapter<UserPartListAdapter.MyViewHolder> {

    private List<UserPartModel> userPartList;
    private Context context;

    public UserPartListAdapter(Context context, List<UserPartModel> userPartList) {
        this.context = context;
        this.userPartList = userPartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_part, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final UserPartModel userPart = userPartList.get(position);
        String carName = userPart.getModelName()+" "+userPart.getYearName();
        if (!carName.startsWith(userPart.getBrandName()))
            carName = userPart.getBrandName() + carName;
        holder.carName.setText(carName);

        holder.partName.setText(userPart.getPartName());
        if (userPart.getPartName()==null || userPart.getPartName().isEmpty())
            holder.partName.setText(userPart.getCategoryName());

        holder.applicable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                Intent intent = new Intent(context, ApplicableListActivity.class);
                intent.putExtra("partId", userPart.getPartId());
                context.startActivity(intent, bundle);
            }
        });
        holder.article.setText(userPart.getArticle());
        holder.applicable.setText(carName);
        holder.comment.setText(userPart.getComment());
        holder.place.setText(userPart.getStore());
        holder.status.setText(userPart.getStatus());

        holder.overFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, position);
            }
        });

        if (userPart.getPrice() != 0) {
            holder.price.setText(userPart.getPrice() + "");
            holder.imageRouble.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(Headers.getUrlWithHeaders(userPart.getImageUrl(), context.getApplicationContext()))
                .signature(new StringSignature(userPart.getId() + " " + userPart.getLastUpdateTime()))
                .into(holder.imageView);
    }

    private void showPopupMenu(View v, final int position) {

        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.menu_car_main);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.btnCareDelete : {
                        new AlertDialog.Builder(context)
                                .setTitle("Вы точно хотите удалить запчасть?")
                                .setNegativeButton("Нет", null)
                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final long partId = userPartList.get(position).getId();
                                        String url = String.format(PartRestURIConstants.DELETE, partId);
                                        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        userPartList.remove(position);
                                                        for (int i = 0; i < PartListActivity.userPartList.size(); i++) {
                                                            if (PartListActivity.userPartList.get(i).getId()==partId) {
                                                                PartListActivity.userPartList.remove(i);
                                                                break;
                                                            }
                                                        }
                                                        notifyItemRemoved(position);
                                                        notifyDataSetChanged();
                                                        Toast.makeText(context, context.getString(R.string.part_success_deleted), Toast.LENGTH_LONG).show();
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
                                                        }
                                                        else {
                                                            Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                                                        }
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
                                .show();
                        break;
                    }
                    case R.id.btnCarEdit : {

                        Gson g = new Gson();
                        Intent intent = new Intent(context, PartFoundActivity.class);
                        UserPartModel part = userPartList.get(position);


                        intent.putExtra("isUpdate", true);
                        intent.putExtra("parts", g.toJson(Arrays.asList(part), new TypeToken<List<UserPartModel>>(){}.getType()));
                        intent.putExtra("car_full_name", CarModel.getFullName(part.getBrandName(), part.getModelName(), part.getYearName()));
                        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                        context.startActivity(intent, bundle);
                        break;
                    }

                }
                return false;
            }
        });

        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return userPartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView carName, partName, applicable, article, comment, place, status, price;
        public View homeView;
        public ImageView imageView, overFlow, imageRouble;

        public MyViewHolder(View itemView) {
            super(itemView);
            carName = (TextView) itemView.findViewById(R.id.item_user_part_car_name);
            partName = (TextView) itemView.findViewById(R.id.item_user_part_part_name);
            applicable = (TextView) itemView.findViewById(R.id.applicable);
            article = (TextView) itemView.findViewById(R.id.item_user_part_article);
            comment = (TextView) itemView.findViewById(R.id.item_user_part_comment);
            place = (TextView) itemView.findViewById(R.id.item_user_part_place);
            homeView = itemView.findViewById(R.id.item_user_part_home_view);
            imageView = (ImageView) itemView.findViewById(R.id.item_user_part_image);
            status = (TextView) itemView.findViewById(R.id.item_user_part_status);
            price = (TextView) itemView.findViewById(R.id.item_user_part_price);
            overFlow = (ImageView) itemView.findViewById(R.id.overflow);
            imageRouble = (ImageView) itemView.findViewById(R.id.image_rouble);
        }
    }

}
