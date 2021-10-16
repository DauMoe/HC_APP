package com.example.hc_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.hc_app.DetailExActivity;
import com.example.hc_app.DetailExerActivity;
import com.example.hc_app.Models.Exercise;
import com.example.hc_app.R;

import org.json.JSONObject;

import java.util.List;

public class ListExerAdapter extends RecyclerView.Adapter<ListExerAdapter.DetailExerViewHolder> {
    private Context context;
    private Activity activity;
    private List<Exercise> data;
    boolean isGroup;

    public ListExerAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setData(List<Exercise> x, boolean group) {
        this.data = x;
        this.isGroup = group;
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
        if (isGroup) {
            holder.exer_thum.setVisibility(View.VISIBLE);
            Glide.with(context).load(item.getThumBase64())
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .error(R.drawable.ic_android_black_24dp)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(holder.exer_thum);
        } else {
            holder.exer_thum.setVisibility(View.GONE);
        }
        holder.exer_name.setText(item.getExcer_name() + " (BMI: " + item.getBmi_from() + " - " + item.getBmi_to() + ")");
        holder.exer_desc.setText((item.getDescription() != null && !item.getDescription().isEmpty()) ? item.getDescription() : "Chưa có mô tả");
        holder.item.setOnClickListener(v -> {
            Intent i;
            if (!isGroup) {
                i = new Intent(activity, DetailExerActivity.class);
            } else {
                i = new Intent(activity, DetailExActivity.class);
            }
            Log.e("ADAPTER_GRID", String.valueOf(item.getExcerID()));
            i.putExtra("data", item.getExcerID());
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
        ImageView exer_thum;

        public DetailExerViewHolder(@NonNull View itemView) {
            super(itemView);
            exer_name   = itemView.findViewById(R.id.ex_name);
            exer_thum   = itemView.findViewById(R.id.ex_thum);
            exer_desc   = itemView.findViewById(R.id.ex_desc);
            item        = itemView.findViewById(R.id.exer_item);
        }
    }
}
