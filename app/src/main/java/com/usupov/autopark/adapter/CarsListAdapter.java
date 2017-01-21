package com.usupov.autopark.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.usupov.autopark.R;
import com.usupov.autopark.model.CarsListModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class CarsListAdapter extends RecyclerView.Adapter<CarsListAdapter.MyViewHolder> {

    private List<CarsListModel> carList;
    private Context context;

    public CarsListAdapter(Context context, List<CarsListModel> list) {
        this.context = context;
        this.carList = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView fullName, description;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            fullName = (TextView) view.findViewById(R.id.item_car_full_name);
            description = (TextView) view.findViewById(R.id.item_car_description);
            thumbnail = (ImageView) view.findViewById(R.id.item_car_thumbnail);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        CarsListModel carListItem = carList.get(position);
        holder.fullName.setText(carListItem.getFullName());
        holder.description.setText(carListItem.getDescription());

        //(new DownloadImage(holder.thumbnail)).execute(carListItem.getImageUrl());

        // loading album cover using Glide library
        Glide.with(context).load(carListItem.getImageUrl()).into(holder.thumbnail);

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

    private class DownloadImage extends AsyncTask<String, Integer, Drawable> {

        private final ImageView imageView;

        public DownloadImage(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Drawable doInBackground(String... arg0) {
            return downloadImage(arg0[0]);
        }

        protected void onPostExecute(Drawable image) {
            //System.err.println("Errrrrr");
            this.imageView.setImageDrawable(image);
        }

        private Drawable downloadImage(String imageUrl) {

            //Prepare to download image
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            //BufferedInputStream buf;
            try {

                url = new URL(imageUrl);
                in = url.openStream();

                /*
                 * THIS IS NOT NEEDED
                 *
                 * YOU TRY TO CREATE AN ACTUAL IMAGE HERE, BY WRITING
                 * TO A NEW FILE
                 * YOU ONLY NEED TO READ THE INPUTSTREAM
                 * AND CONVERT THAT TO A BITMAP*/
                /*out = new BufferedOutputStream(new FileOutputStream("testImage.jpg"));
                int i;

                 while ((i = in.read()) != -1) {
                     out.write(i);
                 }
                 out.close();
                 in.close();*/


                // Read the inputstream
                buf = new BufferedInputStream(in);

                // Convert the BufferedInputStream to a Bitmap
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                if (in != null)
                    in.close();

                if (buf != null)
                    buf.close();

                return new BitmapDrawable(bMap);

            } catch (Exception e) {
                //Log.e("Error reading file", e.toString());
            }

            return null;
        }

    }

}
