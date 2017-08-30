package com.usupov.autopark.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import productcard.ru.R;
import com.usupov.autopark.model.AlbumModel;

import java.io.File;
import java.util.List;


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private List<AlbumModel> albumList;
    private int cntSelected;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
//        public View homeView;
        public TextView circleNumber;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
//            homeView = view.findViewById(R.id.home_view);
            circleNumber = (TextView) view.findViewById(R.id.circleNumber);
        }
    }


    public AlbumsAdapter(Context mContext, List<AlbumModel> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
        cntSelected = 0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        final AlbumModel album = albumList.get(position);


        // loading album cover using Glide library
//        Picasso.with(mContext).load(new File(album.getImageUri())).
//                fit().centerCrop().into(holder.thumbnail);
        Glide.with(mContext).load(new File(album.getImageUri())).asBitmap().thumbnail(0.5f).override(160, 160).centerCrop().
                diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.thumbnail);

//        if (album.isSelected()) {
//
//            holder.circleNumber.setText(album.getNumber()+"");
//            holder.circleNumber.setBackground(mContext.getResources().getDrawable(R.drawable.circle_background_selected));
//        }
//        else {
//
//            holder.circleNumber.setText("");
//            holder.circleNumber.setBackground(mContext.getResources().getDrawable(R.drawable.circle_background));
//        }

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                album.click();
                System.out.println("456456456456456");
                System.out.println(album.getImageUri());
                if (album.isSelected()) {
                    holder.circleNumber.setBackground(mContext.getResources().getDrawable(R.drawable.circle_background_selected));

                    cntSelected++;
                    album.setNumber(cntSelected);
                    holder.circleNumber.setText(cntSelected+"");
                }
                else {
                    holder.circleNumber.setText("");
                    holder.circleNumber.setBackground(mContext.getResources().getDrawable(R.drawable.circle_background));
                    cntSelected--;
                    int removedNumber = album.getNumber();
                    album.setNumber(0);
                    for (AlbumModel item : albumList) {
                        if (item.getNumber() > removedNumber) {
                            item.decNumber();
                        }
                    }
                }
                ((TextView)((Activity)mContext).findViewById(R.id.readyImagesCount)).setText(cntSelected+"");
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
