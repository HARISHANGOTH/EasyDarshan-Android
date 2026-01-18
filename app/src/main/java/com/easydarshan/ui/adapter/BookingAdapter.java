package com.easydarshan.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.easydarshan.R;
import com.easydarshan.data.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    
    private List<Booking> bookings;
    private OnBookingClickListener listener;
    
    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }
    
    public BookingAdapter(List<Booking> bookings, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_card, parent, false);
        return new BookingViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bookingTempleName.setText(booking.getTemple());
        holder.bookingLocation.setText(booking.getLocation());
        holder.bookingDate.setText(booking.getDate());
        holder.bookingTime.setText(booking.getTime());
        holder.bookingStatus.setText(getStatusText(booking.getStatus()));
        holder.bookingId.setText(booking.getId());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookingClick(booking);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return bookings != null ? bookings.size() : 0;
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "upcoming": return "Upcoming";
            case "waiting": return "In Queue";
            case "ready": return "Ready";
            case "completed": return "Completed";
            case "cancelled": return "Cancelled";
            default: return status;
        }
    }
    
    public void updateList(List<Booking> newBookings) {
        BookingDiffCallback diffCallback = new BookingDiffCallback(this.bookings, newBookings);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        
        this.bookings = newBookings;
        diffResult.dispatchUpdatesTo(this);
    }
    
    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingTempleName;
        TextView bookingLocation;
        TextView bookingDate;
        TextView bookingTime;
        TextView bookingStatus;
        TextView bookingId;
        
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingTempleName = itemView.findViewById(R.id.bookingTempleName);
            bookingLocation = itemView.findViewById(R.id.bookingLocation);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingTime = itemView.findViewById(R.id.bookingTime);
            bookingStatus = itemView.findViewById(R.id.bookingStatus);
            bookingId = itemView.findViewById(R.id.bookingId);
        }
    }

    private static class BookingDiffCallback extends DiffUtil.Callback {
        private final List<Booking> oldList;
        private final List<Booking> newList;

        public BookingDiffCallback(List<Booking> oldList, List<Booking> newList) {
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
            return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
