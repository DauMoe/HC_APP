package com.example.hc_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hc_app.Models.Historys;
import com.example.hc_app.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private Context context;
    private List<Historys> data;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat format_hour = new SimpleDateFormat("kk:mm");

    public HistoryAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Historys> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new HistoryAdapter.HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Historys item = data.get(position);
        if (item == null) return;
        holder.txt_bold.setText("Nhóm bài tập: " + item.getGr_name());
        holder.txt_regular.setText("Bài tập: " + item.getExcer_name() + "\n" + formatter.format(item.getDatestamp()) + " (" + format_hour.format(item.getStarttime()) + " - " + format_hour.format(item.getEndtime()) + ")");
    }

    @Override
    public int getItemCount() {
        if (data != null) return data.size();
        return 0;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txt_bold, txt_regular;
        LinearLayout item;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_bold        = itemView.findViewById(R.id.ex_name);
            txt_regular     = itemView.findViewById(R.id.ex_desc);
            item            = itemView.findViewById(R.id.exer_item);
        }
    }
}
