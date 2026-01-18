package com.easydarshan.ui.bookings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.Booking;
import com.easydarshan.data.repository.AppRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingsViewModel extends ViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Booking>> bookings = new MutableLiveData<>();
    private MutableLiveData<Booking> selectedBooking = new MutableLiveData<>();
    private MutableLiveData<String> currentTab = new MutableLiveData<>("upcoming");
    
    public MyBookingsViewModel() {
        repository = AppRepository.getInstance();
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
        isLoading.setValue(true);
        repository.getBookings(status, new Callback<ApiResponse<List<Booking>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Booking>>> call, Response<ApiResponse<List<Booking>>> response) {
                isLoading.postValue(false);
                if (response.body() != null && response.body().isSuccess()) {
                    bookings.postValue(response.body().getData());
                } else {
                    errorMessage.postValue("Failed to load bookings");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Booking>>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error. Please check your connection.");
            }
        });
    }
    
    public void onBookingSelected(Booking booking) {
        selectedBooking.setValue(booking);
    }
}
