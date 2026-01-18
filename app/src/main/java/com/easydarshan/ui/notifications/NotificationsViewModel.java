package com.easydarshan.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.Notification;
import com.easydarshan.data.repository.AppRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsViewModel extends ViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();
    
    public NotificationsViewModel() {
        repository = AppRepository.getInstance();
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }
    
    public void loadNotifications() {
        isLoading.setValue(true);
        repository.getNotifications(new Callback<ApiResponse<List<Notification>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Notification>>> call, Response<ApiResponse<List<Notification>>> response) {
                isLoading.postValue(false);
                if (response.body() != null && response.body().isSuccess()) {
                    notifications.postValue(response.body().getData());
                } else {
                    errorMessage.postValue("Failed to load notifications");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Notification>>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error. Please check your connection.");
            }
        });
    }
    
    public void markAllAsRead() {
        List<Notification> currentNotifications = notifications.getValue();
        if (currentNotifications == null || currentNotifications.isEmpty()) {
            return;
        }
        
        // Mark each unread notification as read
        List<Notification> unreadNotifications = new java.util.ArrayList<>();
        for (Notification notification : currentNotifications) {
            if (!notification.isRead()) {
                unreadNotifications.add(notification);
            }
        }
        
        if (unreadNotifications.isEmpty()) {
            // All notifications already read
            return;
        }
        
        final int[] completedCount = {0};
        final int totalCount = unreadNotifications.size();
        
        for (Notification notification : unreadNotifications) {
            repository.markNotificationRead((long) notification.getId(), new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    completedCount[0]++;
                    // Reload notifications after all are marked
                    if (completedCount[0] == totalCount) {
                        loadNotifications();
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    completedCount[0]++;
                    if (completedCount[0] == totalCount) {
                        loadNotifications();
                    } else {
                        errorMessage.postValue("Failed to mark some notifications as read");
                    }
                }
            });
        }
    }
}

