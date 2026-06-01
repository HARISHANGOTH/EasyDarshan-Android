package com.easydarshan.ui.bookings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.Booking;
import com.easydarshan.data.model.BookingListResponse;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingsViewModel extends AndroidViewModel {
    
    private final AppRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Booking>> bookings = new MutableLiveData<>(new ArrayList<>());
    
    public MyBookingsViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
    
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<Booking>> getBookings() { return bookings; }
    
    public void loadBookings(String status) {
        // Load from cache first
        repository.getCachedBookings(status, cached -> {
            if (cached != null && !cached.isEmpty()) {
                bookings.postValue(cached);
            }
        });

        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            if (bookings.getValue().isEmpty()) {
                errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            }
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        repository.getBookings(status, new Callback<BookingListResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookingListResponse> call, @NonNull Response<BookingListResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Booking> list = response.body().getBookings();
                    bookings.postValue(list != null ? list : new ArrayList<>());
                } else if (bookings.getValue().isEmpty()) {
                    errorMessage.postValue(ErrorHandler.getErrorMessage(response.code(), null));
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<BookingListResponse> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                if (bookings.getValue().isEmpty()) {
                    errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
                }
            }
        });
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }
}
