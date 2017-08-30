package com.usupov.autopark.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import productcard.ru.R;
import com.usupov.autopark.activity.PartNewActivity;
import com.usupov.autopark.model.UserPartModel;

import java.util.List;


public class PartFoundAdapter extends RecyclerView.Adapter<PartFoundAdapter.MyViewHolder> {

    Context context;
    List<UserPartModel> partList;
    boolean activeSelect;
    private int cntChecked;

    public PartFoundAdapter(Context context, List<UserPartModel> partList, boolean activeSelect) {
        this.context = context;
        this.partList = partList;
        this.activeSelect = activeSelect;
        this.cntChecked = 0;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        UserPartModel part = partList.get(position);

        String text = part.getParentCatName()+" | "+part.getCategoryName()+"\n"+part.getArticle()+",  "+part.getTitle()+"\n";
        if (part.getParentCatName()==null) {
            text = part.getTitle();
            holder.checkBox.setVisibility(View.GONE);
        }
        String note = part.getNote();
        if (note==null)
            note = "";
        int ind = note.indexOf("Данные модели:");
        if (ind >= 0) {
            note = note.substring(ind);
            text += note + "\n";
        }
        holder.partName.setText(text);

        if (activeSelect) {
            if (cntChecked == 0) {
                ((PartNewActivity) context).findViewById(PartNewActivity.textNextId).setVisibility(View.INVISIBLE);
            }
            else {
                ((PartNewActivity) context).findViewById(PartNewActivity.textNextId).setVisibility(View.VISIBLE);
            }
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activeSelect) {
                    holder.checkBox.setChecked(true);
                    return;
                }
                UserPartModel part = partList.get(position);
                if (holder.checkBox.isChecked()) {
                    ((PartNewActivity)context).selectedPartsMap.put(part.getId()+" "+part.getCarId(), part);
                    cntChecked ++;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.checkBox.setButtonTintList(ContextCompat.getColorStateList(context, R.color.colorPrimary));
                    }
                }
                else {
                    ((PartNewActivity)context).selectedPartsMap.remove(part.getId()+" "+part.getCarId());
                    cntChecked --;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.checkBox.setButtonTintList(ContextCompat.getColorStateList(context, R.color.colorGray));
                    }
                }
                if (cntChecked==0) {
                    ((PartNewActivity) context).findViewById(PartNewActivity.textNextId).setVisibility(View.INVISIBLE);
                }
                else {
                    ((PartNewActivity) context).findViewById(PartNewActivity.textNextId).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return partList.size();
    }

    @Override
    public PartFoundAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parts, parent, false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView partName;
        public AppCompatCheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            partName = (TextView)itemView.findViewById(R.id.textPart);
            checkBox = (AppCompatCheckBox) itemView.findViewById(R.id.chBoxPart);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkBox.setButtonTintList(ContextCompat.getColorStateList(context, R.color.colorGray));
            }
            if (!activeSelect) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    checkBox.setButtonTintList(ContextCompat.getColorStateList(context, R.color.colorPrimary));
                }
                checkBox.setChecked(true);
            }
        }
    }
}
