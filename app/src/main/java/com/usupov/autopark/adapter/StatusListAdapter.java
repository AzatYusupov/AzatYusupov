package com.usupov.autopark.adapter;


import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.usupov.autopark.R;
import com.usupov.autopark.model.StatusModel;

import java.util.List;

public class StatusListAdapter implements ListAdapter {

    List<StatusModel> statusList;
    Context context;

    public StatusListAdapter(Context context, List<StatusModel> statusList) {
        this.context = context;
        this.statusList = statusList;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return statusList.size();
    }

    @Override
    public Object getItem(int position) {
        return statusList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

        if (convertView==null)
            convertView = inflater.inflate(R.layout.item_status, null);
        TextView numberStatus = (TextView) convertView.findViewById(R.id.numberStatus);
        TextView titleStatus = (TextView) convertView.findViewById(R.id.titleStatus);
        TextView descriptionStatus = (TextView) convertView.findViewById(R.id.descriptionStatus);
        ImageView imageStatus = (ImageView) convertView.findViewById(R.id.imageStatus);

        StatusModel itemStatus = statusList.get(position);
        numberStatus.setText(itemStatus.getNumber());
        imageStatus.setImageDrawable(context.getResources().getDrawable(itemStatus.getImage()));
        titleStatus.setText(itemStatus.getTitle());
        descriptionStatus.setText(itemStatus.getDescription());
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return statusList.isEmpty();
    }
}
