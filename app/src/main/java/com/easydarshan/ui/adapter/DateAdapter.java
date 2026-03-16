package com.easydarshan.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easydarshan.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    public interface OnDateClickListener {
        void onDateClick(String date);
    }

    private List<String> dates = new ArrayList<>();
    private final OnDateClickListener listener;
    private String selectedDate;

    private final SimpleDateFormat serverFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat dayFormat =
            new SimpleDateFormat("EEE", Locale.getDefault());
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("d", Locale.getDefault());
    private final SimpleDateFormat monthFormat =
            new SimpleDateFormat("MMM", Locale.getDefault());

    public DateAdapter(List<String> dates, OnDateClickListener listener) {
        if (dates != null) {
            this.dates = dates;
        }
        this.listener = listener;
    }

    public void updateDates(List<String> newDates) {
        this.dates = newDates != null ? newDates : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        String rawDate = dates.get(position);
        try {
            Date parsed = serverFormat.parse(rawDate);
            if (parsed != null) {
                holder.dayText.setText(dayFormat.format(parsed));
                holder.dateText.setText(dateFormat.format(parsed));
                holder.monthText.setText(monthFormat.format(parsed));
            } else {
                holder.dayText.setText("");
                holder.dateText.setText(rawDate);
                holder.monthText.setText("");
            }
        } catch (ParseException e) {
            holder.dayText.setText("");
            holder.dateText.setText(rawDate);
            holder.monthText.setText("");
        }

        // Simple selected state styling (bold text)
        boolean isSelected = selectedDate != null && selectedDate.equals(rawDate);
        holder.itemView.setSelected(isSelected);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateClick(rawDate);
            }
            setSelectedDate(rawDate);
        });
    }

    @Override
    public int getItemCount() {
        return dates != null ? dates.size() : 0;
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        TextView dateText;
        TextView monthText;

        DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            dateText = itemView.findViewById(R.id.dateText);
            monthText = itemView.findViewById(R.id.monthText);
        }
    }
}





