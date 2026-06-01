package com.easydarshan.ui.bookings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.Booking;
import com.easydarshan.data.model.SingleBookingResponse;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailsViewModel extends AndroidViewModel {
    
    private final AppRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Booking> booking = new MutableLiveData<>();
    
    public BookingDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
    
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Booking> getBooking() { return booking; }
    
    public void loadBookingDetails(String bookingId) {
        if (bookingId == null || bookingId.isEmpty()) {
            errorMessage.setValue("Invalid booking ID");
            return;
        }
        
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        repository.getBookingDetails(bookingId, new Callback<SingleBookingResponse>() {
            @Override
            public void onResponse(@NonNull Call<SingleBookingResponse> call, @NonNull Response<SingleBookingResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    booking.postValue(response.body().getBooking());
                } else {
                    errorMessage.postValue(ErrorHandler.getErrorMessage(response.code(), null));
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<SingleBookingResponse> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
            }
        });
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }
}
