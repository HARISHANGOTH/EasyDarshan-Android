package com.easydarshan.ui.prebooking;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.AvailableDatesResponse;
import com.easydarshan.data.model.CreateBookingRequest;
import com.easydarshan.data.model.CreateBookingResponse;
import com.easydarshan.data.model.PaymentOrderRequest;
import com.easydarshan.data.model.PaymentOrderResponse;
import com.easydarshan.data.model.SlotsResponse;
import com.easydarshan.data.model.TimeSlot;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreBookingViewModel extends AndroidViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Integer> currentStep = new MutableLiveData<>(1);
    private MutableLiveData<String> selectedDate = new MutableLiveData<>();
    private MutableLiveData<TimeSlot> selectedSlot = new MutableLiveData<>();
    private MutableLiveData<Integer> adultCount = new MutableLiveData<>(1);
    private MutableLiveData<Integer> childrenCount = new MutableLiveData<>(0);
    private MutableLiveData<List<String>> availableDates = new MutableLiveData<>();
    private MutableLiveData<List<TimeSlot>> timeSlots = new MutableLiveData<>();
    private MutableLiveData<CreateBookingResponse> bookingCreated = new MutableLiveData<>();
    private MutableLiveData<PaymentOrderResponse> paymentOrderCreated = new MutableLiveData<>();
    
    private Long templeId;
    private Long darshanTypeId;
    private String selectedDarshanType;
    private String bookingId;
    private String lockId;
    private boolean isLiveQueue = false;
    
    public PreBookingViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
    
    public void setTempleId(Long templeId) {
        this.templeId = templeId;
    }
    
    public void setDarshanTypeId(Long darshanTypeId) {
        this.darshanTypeId = darshanTypeId;
    }
    
    public void setSelectedDarshanType(String darshanType) {
        this.selectedDarshanType = darshanType;
        if (darshanType != null && darshanType.toLowerCase().contains("live")) {
            this.isLiveQueue = true;
            this.selectedDate.setValue(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            this.currentStep.setValue(3);
        }
    }
    
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Integer> getCurrentStep() { return currentStep; }
    public void setCurrentStep(int step) { currentStep.setValue(step); }
    public LiveData<String> getSelectedDate() { return selectedDate; }
    public LiveData<TimeSlot> getSelectedSlot() { return selectedSlot; }
    public LiveData<Integer> getAdultCount() { return adultCount; }
    public LiveData<Integer> getChildrenCount() { return childrenCount; }
    public LiveData<List<String>> getAvailableDates() { return availableDates; }
    public LiveData<List<TimeSlot>> getTimeSlots() { return timeSlots; }
    public LiveData<CreateBookingResponse> getBookingCreated() { return bookingCreated; }
    public LiveData<PaymentOrderResponse> getPaymentOrderCreated() { return paymentOrderCreated; }
    
    public void incrementAdults() {
        int current = adultCount.getValue() != null ? adultCount.getValue() : 1;
        if (current < 10) adultCount.setValue(current + 1);
    }
    
    public void decrementAdults() {
        int current = adultCount.getValue() != null ? adultCount.getValue() : 1;
        if (current > 1) adultCount.setValue(current - 1);
    }

    public void incrementChildren() {
        int current = childrenCount.getValue() != null ? childrenCount.getValue() : 0;
        if (current < 10) childrenCount.setValue(current + 1);
    }
    
    public void decrementChildren() {
        int current = childrenCount.getValue() != null ? childrenCount.getValue() : 0;
        if (current > 0) childrenCount.setValue(current - 1);
    }

    public int getTotalDevotees() {
        return (adultCount.getValue() != null ? adultCount.getValue() : 0) + 
               (childrenCount.getValue() != null ? childrenCount.getValue() : 0);
    }
    
    public void loadAvailableDates() {
        if (isLiveQueue) return;
        isLoading.setValue(true);
        repository.getAvailableDates(templeId, darshanTypeId, new Callback<AvailableDatesResponse>() {
            @Override
            public void onResponse(Call<AvailableDatesResponse> call, Response<AvailableDatesResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    availableDates.postValue(response.body().getAvailableDates());
                } else {
                    errorMessage.postValue("Failed to load dates");
                }
            }
            @Override
            public void onFailure(Call<AvailableDatesResponse> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
            }
        });
    }
    
    public void selectDate(String date) {
        selectedDate.setValue(date);
        loadTimeSlots(date);
        currentStep.setValue(2);
    }

    private void loadTimeSlots(String date) {
        isLoading.setValue(true);
        repository.getSlots(date, templeId, darshanTypeId, new Callback<SlotsResponse>() {
            @Override
            public void onResponse(Call<SlotsResponse> call, Response<SlotsResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<TimeSlot> slots = new ArrayList<>();
                    for (SlotsResponse.SlotInfo info : response.body().getSlots()) {
                        TimeSlot s = new TimeSlot();
                        s.setTime(info.getTime());
                        s.setPrice(info.getPrice().intValue());
                        s.setAvailable("AVAILABLE".equals(info.getStatus()));
                        s.setSlotId(info.getSlotId());
                        slots.add(s);
                    }
                    timeSlots.postValue(slots);
                }
            }
            @Override
            public void onFailure(Call<SlotsResponse> call, Throwable t) {
                isLoading.postValue(false);
            }
        });
    }
    
    public void selectSlot(TimeSlot slot) {
        if (slot.isAvailable()) {
            selectedSlot.setValue(slot);
            currentStep.setValue(3);
        }
    }
    
    public void continueToPayment() {
        currentStep.setValue(4);
    }
    
    public void createBookingAndPayment(String paymentMethod) {
        isLoading.setValue(true);
        int totalDevotees = getTotalDevotees();
        int unitPrice = isLiveQueue ? 
            (selectedDarshanType.toLowerCase().contains("paid") ? 500 : 0) : 
            (selectedSlot.getValue() != null ? selectedSlot.getValue().getPrice() : 0);
        
        // Platform fee is always 20
        double totalAmount = (unitPrice * totalDevotees) + 20;
        
        CreateBookingRequest request = new CreateBookingRequest(
                templeId,
                selectedDate.getValue(),
                isLiveQueue ? "LIVE_QUEUE" : selectedSlot.getValue().getSlotId(),
                selectedDarshanType,
                totalDevotees,
                paymentMethod,
                totalAmount
        );
        
        repository.createBooking(request, new Callback<CreateBookingResponse>() {
            @Override
            public void onResponse(Call<CreateBookingResponse> call, Response<CreateBookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookingId = response.body().getBookingId();
                    createPaymentOrder(paymentMethod);
                } else {
                    isLoading.postValue(false);
                    errorMessage.postValue("Booking failed");
                }
            }
            @Override
            public void onFailure(Call<CreateBookingResponse> call, Throwable t) {
                isLoading.postValue(false);
            }
        });
    }
    
    private void createPaymentOrder(String paymentMethod) {
        PaymentOrderRequest request = new PaymentOrderRequest(bookingId, paymentMethod);
        repository.createPaymentOrder(request, new Callback<PaymentOrderResponse>() {
            @Override
            public void onResponse(Call<PaymentOrderResponse> call, Response<PaymentOrderResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    paymentOrderCreated.postValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<PaymentOrderResponse> call, Throwable t) {
                isLoading.postValue(false);
            }
        });
    }
    
    public void confirmBooking() {
        if (bookingId != null) {
            CreateBookingResponse response = new CreateBookingResponse();
            response.setBookingId(bookingId);
            response.setStatus("CONFIRMED");
            bookingCreated.postValue(response);
        }
    }

    public boolean isLiveQueue() { return isLiveQueue; }
}
