package com.example.hc_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hc_app.DetailExerActivity;
import com.example.hc_app.Models.Exercise;
import com.example.hc_app.R;

import java.util.List;

public class ListExerAdapter extends RecyclerView.Adapter<ListExerAdapter.DetailExerViewHolder> {
    private Context context;
    private List<Exercise> data;

    public ListExerAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Exercise> x) {
        this.data = x;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailExerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new DetailExerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailExerViewHolder holder, int position) {
        Exercise item = data.get(position);
        if (item == null) return;
        holder.exer_name.setText(item.getExcer_name() + " (BMI: " + item.getBmi_from() + " - " + item.getBmi_to() + ")");
        holder.exer_desc.setText((item.getDescription() != null && !item.getDescription().isEmpty()) ? item.getDescription() : "Chưa có mô tả");
        holder.item.setOnClickListener(v -> {
            Intent i = new Intent(context, DetailExerActivity.class);
            i.putExtra("data", item);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        if (data != null) return data.size();
        return 0;
    }

    public class DetailExerViewHolder extends RecyclerView.ViewHolder {
        TextView exer_name, exer_desc;
        LinearLayout item;

        public DetailExerViewHolder(@NonNull View itemView) {
            super(itemView);
            exer_name   = itemView.findViewById(R.id.ex_name);
            exer_desc   = itemView.findViewById(R.id.ex_desc);
            item        = itemView.findViewById(R.id.exer_item);
        }
    }
}
