package com.usupov.autopark.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.usupov.autopark.R;
import com.usupov.autopark.model.UserPartModel;

import java.util.List;

/**
 * Created by Azat on 06.07.2017.
 */

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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserPartModel userPart = userPartList.get(position);
        String carName = userPart.getModelName()+" "+userPart.getYearName();
        if (!carName.startsWith(userPart.getBrandName()))
            carName = userPart.getBrandName() + carName;
        holder.carName.setText(carName);

        holder.partName.setText(userPart.getPartName());
        if (userPart.getPartName()==null || userPart.getPartName().isEmpty())
            holder.partName.setText(userPart.getCategoryName());

        holder.suitFor.setText("");
        holder.article.setText(userPart.getArticle());
        holder.comment.setText(userPart.getComment());
        holder.place.setText(userPart.getStore());

        Glide.with(context).load(userPart.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return userPartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView carName, partName, suitFor, article, comment, place;
        public View homeView;
        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            carName = (TextView) itemView.findViewById(R.id.item_user_part_car_name);
            partName = (TextView) itemView.findViewById(R.id.item_user_part_part_name);
            suitFor = (TextView) itemView.findViewById(R.id.item_user_part_suit);
            article = (TextView) itemView.findViewById(R.id.item_user_part_article);
            comment = (TextView) itemView.findViewById(R.id.item_user_part_comment);
            place = (TextView) itemView.findViewById(R.id.item_user_part_place);
            homeView = itemView.findViewById(R.id.item_user_part_home_view);
            imageView = (ImageView) itemView.findViewById(R.id.item_user_part_image);

        }
    }

}
