package com.easydarshan.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easydarshan.R;
import com.easydarshan.data.model.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    public interface OnSlotClickListener {
        void onSlotClick(TimeSlot slot);
    }

    private List<TimeSlot> slots = new ArrayList<>();
    private final OnSlotClickListener listener;
    private TimeSlot selectedSlot;

    public TimeSlotAdapter(List<TimeSlot> slots, OnSlotClickListener listener) {
        if (slots != null) {
            this.slots = slots;
        }
        this.listener = listener;
    }

    public void updateSlots(List<TimeSlot> newSlots) {
        this.slots = newSlots != null ? newSlots : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setSelectedSlot(TimeSlot slot) {
        this.selectedSlot = slot;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot slot = slots.get(position);
        holder.slotTime.setText(slot.getTime());
        holder.slotCapacity.setText(slot.getCapacity());
        holder.slotPrice.setText("₹" + slot.getPrice());

        boolean isAvailable = slot.isAvailable();
        holder.itemView.setEnabled(isAvailable);

        boolean isSelected = selectedSlot != null && selectedSlot.getSlotId() != null
                && selectedSlot.getSlotId().equals(slot.getSlotId());
        holder.itemView.setSelected(isSelected);

        holder.itemView.setOnClickListener(v -> {
            if (isAvailable && listener != null) {
                listener.onSlotClick(slot);
                setSelectedSlot(slot);
            }
        });
    }

    @Override
    public int getItemCount() {
        return slots != null ? slots.size() : 0;
    }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView slotTime;
        TextView slotCapacity;
        TextView slotPrice;

        TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            slotTime = itemView.findViewById(R.id.slotTime);
            slotCapacity = itemView.findViewById(R.id.slotCapacity);
            slotPrice = itemView.findViewById(R.id.slotPrice);
        }
    }
}





