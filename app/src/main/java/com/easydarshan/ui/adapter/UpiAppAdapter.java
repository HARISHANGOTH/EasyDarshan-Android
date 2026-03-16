package com.easydarshan.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easydarshan.R;
import com.easydarshan.data.model.UpiApp;

import java.util.List;

public class UpiAppAdapter extends RecyclerView.Adapter<UpiAppAdapter.ViewHolder> {

    private final List<UpiApp> upiApps;
    private final OnAppClickListener listener;

    public interface OnAppClickListener {
        void onAppClick(UpiApp app);
    }

    public UpiAppAdapter(List<UpiApp> upiApps, OnAppClickListener listener) {
        this.upiApps = upiApps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upi_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UpiApp app = upiApps.get(position);
        holder.tvName.setText(app.getName());
        holder.ivLogo.setImageDrawable(app.getIcon());
        holder.itemView.setOnClickListener(v -> listener.onAppClick(app));
    }

    @Override
    public int getItemCount() {
        return upiApps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.ivUpiAppLogo);
            tvName = itemView.findViewById(R.id.tvUpiAppName);
        }
    }
}
