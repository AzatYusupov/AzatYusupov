package com.usupov.autopark.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.usupov.autopark.R;
import com.usupov.autopark.activity.PartNewActivity;
import com.usupov.autopark.model.UserPartModel;

import java.util.List;


public class PartFoundAdapter extends RecyclerView.Adapter<PartFoundAdapter.MyViewHolder> {

    Context context;
    List<UserPartModel> partList;
    boolean activeSelect;

    public PartFoundAdapter(Context context, List<UserPartModel> partList, boolean activeSelect) {
        this.context = context;
        this.partList = partList;
        this.activeSelect = activeSelect;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        UserPartModel part = partList.get(position);

        String text = part.getParentCatName()+" | "+part.getCategoryName()+"\n"+part.getArticle()+",  "+part.getTitle()+"\n";
        String note = part.getNote();
        int ind = note.indexOf("Данные модели:");
        if (ind >= 0) {
            note = note.substring(ind);
            text += note + "\n";
        }
        holder.partName.setText(text);

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
                }
                else {
                    ((PartNewActivity)context).selectedPartsMap.remove(part.getId()+" "+part.getCarId());
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
        public CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            partName = (TextView)itemView.findViewById(R.id.textPart);
            checkBox = (CheckBox) itemView.findViewById(R.id.chBoxPart);
            if (!activeSelect) {
                checkBox.setChecked(true);
            }
        }
    }
}
