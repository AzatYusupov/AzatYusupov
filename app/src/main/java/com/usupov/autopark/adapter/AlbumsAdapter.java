package com.usupov.autopark.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.usupov.autopark.R;

import java.io.File;
import java.util.List;


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public View homeView;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            homeView = view.findViewById(R.id.home_view);
        }
    }


    public AlbumsAdapter(Context mContext, List<String> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final String album = albumList.get(position);

        // loading album cover using Glide library
        Glide.with(mContext).load(album).into(holder.thumbnail);

        holder.homeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.fromFile(new File(album)));
                ((Activity)mContext).setResult(Activity.RESULT_OK, intent);
                ((Activity)mContext).finish();
            }
        });

    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
