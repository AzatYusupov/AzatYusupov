package com.usupov.autopark.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import productcard.ru.R;
import com.usupov.autopark.model.AlbumModel;

import java.io.File;
import java.util.List;

public class GalleryAdapter extends BaseAdapter {

    private Context mContext;
    private List<AlbumModel> albumList;
    private int cntSelected;

    public GalleryAdapter(Context mContext, List<AlbumModel> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AlbumModel album = albumList.get(position);
        if (convertView==null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gallery_card, parent, false);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
        Picasso.with(mContext).load(new File(album.getImageUri())).resize(0, 180).into(imageView);

        final TextView circleNumber = (TextView) convertView.findViewById(R.id.circleNumber);

        if (album.isSelected()) {
            circleNumber.setBackground(mContext.getResources().getDrawable(R.drawable.circle_background_selected));
            circleNumber.setText(album.getNumber()+"");
        }
        else {
            circleNumber.setBackground(mContext.getResources().getDrawable(R.drawable.circle_background));
            circleNumber.setText("");
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                album.click();
                if (album.isSelected()) {
                    circleNumber.setBackground(mContext.getResources().getDrawable(R.drawable.circle_background_selected));
                    cntSelected++;
                    album.setNumber(cntSelected);
                    circleNumber.setText(cntSelected+"");
                }
                else {
                    circleNumber.setText("");
                    circleNumber.setBackground(mContext.getResources().getDrawable(R.drawable.circle_background));
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
        return convertView;
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        return inSampleSize;
    }
}
