package com.usupov.autopark.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.usupov.autopark.model.CatalogYear;

import product.card.R;

import java.util.List;


public class ApplicableAdapter extends RecyclerView.Adapter<ApplicableAdapter.MyViewHolder> {
    List<CatalogYear> applyList;
    Context context;

    public ApplicableAdapter(Context context, List<CatalogYear> applyList) {
        this.context = context;
        this.applyList = applyList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).
                inflate(R.layout.row_apply_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CatalogYear year = applyList.get(position);
        holder.brandName.setText(year.getBrandName());
        holder.modelName.setText(year.getModelName());
        holder.yearName.setText(year.getName());
    }

    @Override
    public int getItemCount() {
        return applyList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView brandName, modelName, yearName;

        public MyViewHolder(View itemView) {
            super(itemView);
            brandName = (TextView) itemView.findViewById(R.id.apply_brand);
            modelName = (TextView) itemView.findViewById(R.id.apply_model);
            yearName = (TextView) itemView.findViewById(R.id.apply_year);
        }
    }

}
