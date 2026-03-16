package com.easydarshan.ui.bookings;

import android.app.Application;

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
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Booking>> bookings = new MutableLiveData<>();
    private MutableLiveData<Booking> selectedBooking = new MutableLiveData<>();
    private MutableLiveData<String> currentTab = new MutableLiveData<>("upcoming");
    
    public MyBookingsViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<List<Booking>> getBookings() {
        return bookings;
    }
    
    public LiveData<Booking> getSelectedBooking() {
        return selectedBooking;
    }
    
    public LiveData<String> getCurrentTab() {
        return currentTab;
    }
    
    public void setCurrentTab(String tab) {
        currentTab.setValue(tab);
        loadBookings(tab);
    }
    
    public void loadBookings(String status) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        repository.getBookings(status, new Callback<BookingListResponse>() {
            @Override
            public void onResponse(Call<BookingListResponse> call, Response<BookingListResponse> response) {
                isLoading.postValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Booking> bookingList = response.body().getBookings();
                    bookings.postValue(bookingList != null ? bookingList : new ArrayList<>());
                } else {
                    String errorMsg = ErrorHandler.getErrorMessage(response.code(), null);
                    errorMessage.postValue(errorMsg);
                    bookings.postValue(new ArrayList<>());
                }
            }
            
            @Override
            public void onFailure(Call<BookingListResponse> call, Throwable t) {
                isLoading.postValue(false);
                String errorMsg = ErrorHandler.getErrorMessage(t, getApplication());
                errorMessage.postValue(errorMsg);
                bookings.postValue(new ArrayList<>());
            }
        });
    }
    
    public void onBookingSelected(Booking booking) {
        selectedBooking.setValue(booking);
    }
}
