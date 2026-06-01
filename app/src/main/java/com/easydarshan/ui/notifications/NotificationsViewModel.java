package com.easydarshan.ui.notifications;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.Notification;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsViewModel extends AndroidViewModel {
    
    private final AppRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Notification>> notifications = new MutableLiveData<>(new ArrayList<>());
    
    public NotificationsViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
    
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<Notification>> getNotifications() { return notifications; }
    
    public void loadNotifications() {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        repository.getNotifications(new Callback<ApiResponse<List<Notification>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Notification>>> call, @NonNull Response<ApiResponse<List<Notification>>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<Notification> list = response.body().getData();
                        notifications.postValue(list != null ? list : new ArrayList<>());
                    } else {
                        errorMessage.postValue(response.body().getMessage() != null ? response.body().getMessage() : "Failed to load notifications");
                    }
                } else {
                    errorMessage.postValue(ErrorHandler.getErrorMessage(response.code(), null));
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Notification>>> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
            }
        });
    }
    
    public void markAllAsRead() {
        List<Notification> current = notifications.getValue();
        if (current == null || current.isEmpty()) return;
        
        List<Notification> unread = new ArrayList<>();
        for (Notification n : current) {
            if (!n.isRead()) unread.add(n);
        }
        
        if (unread.isEmpty()) return;
        
        final int[] completed = {0};
        for (Notification n : unread) {
            repository.markNotificationRead((long) n.getId(), new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                    if (++completed[0] == unread.size()) loadNotifications();
                }
                
                @Override
                public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                    if (++completed[0] == unread.size()) loadNotifications();
                }
            });
        }
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }
}
