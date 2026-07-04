package com.easydarshan.ui.livequeue;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.LiveQueuePositionResponse;
import com.easydarshan.data.repository.AppRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveQueueViewModel extends AndroidViewModel {
    
    private final AppRepository repository;
    private final MutableLiveData<LiveQueuePositionResponse> queueStatus = new MutableLiveData<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable poller;
    private String bookingId;
    
    public LiveQueueViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
    
    public LiveData<LiveQueuePositionResponse> getQueueStatus() { return queueStatus; }
    
    public void startPolling(String bookingId) {
        this.bookingId = bookingId;
        if (poller != null) handler.removeCallbacks(poller);
        poller = new Runnable() {
            @Override
            public void run() {
                fetchStatus();
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(poller);
    }
    
    public void fetchStatus() {
        if (bookingId == null) return;
        repository.getQueuePosition(bookingId, new Callback<LiveQueuePositionResponse>() {
            @Override
            public void onResponse(@NonNull Call<LiveQueuePositionResponse> call, @NonNull Response<LiveQueuePositionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    queueStatus.postValue(response.body());
                }
            }
            @Override
            public void onFailure(@NonNull Call<LiveQueuePositionResponse> call, @NonNull Throwable t) {}
        });
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        if (poller != null) handler.removeCallbacks(poller);
    }
}
