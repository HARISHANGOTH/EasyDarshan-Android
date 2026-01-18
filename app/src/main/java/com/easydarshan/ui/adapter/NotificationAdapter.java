package com.easydarshan.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.easydarshan.R;
import com.easydarshan.data.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    
    private List<Notification> notifications;
    
    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }
    
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.notificationTitle.setText(notification.getTitle());
        holder.notificationMessage.setText(notification.getMessage());
        holder.notificationTime.setText(notification.getTime());
        holder.notificationIcon.setText(notification.getIcon());
        
        if (notification.isRead()) {
            holder.unreadIndicator.setVisibility(View.GONE);
        } else {
            holder.unreadIndicator.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }
    
    public void updateList(List<Notification> newNotifications) {
        NotificationDiffCallback diffCallback = new NotificationDiffCallback(this.notifications, newNotifications);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        
        this.notifications = newNotifications;
        diffResult.dispatchUpdatesTo(this);
    }
    
    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTitle;
        TextView notificationMessage;
        TextView notificationTime;
        TextView notificationIcon;
        View unreadIndicator;
        
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            notificationTime = itemView.findViewById(R.id.notificationTime);
            notificationIcon = itemView.findViewById(R.id.notificationIcon);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
        }
    }

    private static class NotificationDiffCallback extends DiffUtil.Callback {
        private final List<Notification> oldList;
        private final List<Notification> newList;

        public NotificationDiffCallback(List<Notification> oldList, List<Notification> newList) {
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
