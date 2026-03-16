package com.easydarshan.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.easydarshan.R;
import com.easydarshan.data.model.Temple;

import java.util.List;

public class TempleAdapter extends RecyclerView.Adapter<TempleAdapter.TempleViewHolder> {
    
    private List<Temple> temples;
    private OnTempleClickListener listener;
    
    public interface OnTempleClickListener {
        void onTempleClick(Temple temple);
    }
    
    public TempleAdapter(List<Temple> temples, OnTempleClickListener listener) {
        this.temples = temples;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TempleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_temple_card, parent, false);
        return new TempleViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TempleViewHolder holder, int position) {
        Temple temple = temples.get(position);
        holder.templeName.setText(temple.getName());
        holder.templeLocation.setText(temple.getLocation());
        holder.templeDistance.setText(temple.getDistance());
        holder.queueStatusBadge.setText(temple.getQueueText());

        Glide.with(holder.itemView.getContext())
                .load(temple.getImage())
                .placeholder(R.drawable.ic_temple_placeholder)
                .into(holder.templeImage);
        
        View.OnClickListener clickListener = v -> {
            if (listener != null) {
                listener.onTempleClick(temple);
            }
        };

        holder.itemView.setOnClickListener(clickListener);
        if (holder.bookDarshanButton != null) {
            holder.bookDarshanButton.setOnClickListener(clickListener);
        }
    }
    
    @Override
    public int getItemCount() {
        return temples != null ? temples.size() : 0;
    }
    
    public void updateList(List<Temple> newTemples) {
        TempleDiffCallback diffCallback = new TempleDiffCallback(this.temples, newTemples);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        
        this.temples = newTemples;
        diffResult.dispatchUpdatesTo(this);
    }
    
    static class TempleViewHolder extends RecyclerView.ViewHolder {
        ImageView templeImage;
        TextView templeName;
        TextView templeLocation;
        TextView templeDistance;
        TextView queueStatusBadge;
        View bookDarshanButton;
        
        public TempleViewHolder(@NonNull View itemView) {
            super(itemView);
            templeImage = itemView.findViewById(R.id.templeImage);
            templeName = itemView.findViewById(R.id.templeName);
            templeLocation = itemView.findViewById(R.id.templeLocation);
            templeDistance = itemView.findViewById(R.id.templeDistance);
            queueStatusBadge = itemView.findViewById(R.id.queueStatusBadge);
            bookDarshanButton = itemView.findViewById(R.id.bookDarshanButton);
        }
    }

    private static class TempleDiffCallback extends DiffUtil.Callback {
        private final List<Temple> oldList;
        private final List<Temple> newList;

        public TempleDiffCallback(List<Temple> oldList, List<Temple> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
