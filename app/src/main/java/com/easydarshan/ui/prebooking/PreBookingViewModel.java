package com.easydarshan.ui.prebooking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.Booking;
import com.easydarshan.data.model.TimeSlot;
import com.easydarshan.data.repository.AppRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreBookingViewModel extends ViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Integer> currentStep = new MutableLiveData<>(1);
    private MutableLiveData<Date> selectedDate = new MutableLiveData<>();
    private MutableLiveData<TimeSlot> selectedSlot = new MutableLiveData<>();
    private MutableLiveData<Integer> devoteeCount = new MutableLiveData<>(1);
    private MutableLiveData<String> devoteeName = new MutableLiveData<>("");
    private MutableLiveData<List<Date>> availableDates = new MutableLiveData<>();
    private MutableLiveData<List<TimeSlot>> timeSlots = new MutableLiveData<>();
    private MutableLiveData<Booking> bookingCreated = new MutableLiveData<>();
    
    private int templeId;
    
    public PreBookingViewModel() {
        repository = AppRepository.getInstance();
        loadAvailableDates();
        loadTimeSlots();
    }
    
    public void setTempleId(int templeId) {
        this.templeId = templeId;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Integer> getCurrentStep() {
        return currentStep;
    }
    
    public LiveData<Date> getSelectedDate() {
        return selectedDate;
    }
    
    public LiveData<TimeSlot> getSelectedSlot() {
        return selectedSlot;
    }
    
    public LiveData<Integer> getDevoteeCount() {
        return devoteeCount;
    }
    
    public LiveData<String> getDevoteeName() {
        return devoteeName;
    }
    
    public LiveData<List<Date>> getAvailableDates() {
        return availableDates;
    }
    
    public LiveData<List<TimeSlot>> getTimeSlots() {
        return timeSlots;
    }
    
    public LiveData<Booking> getBookingCreated() {
        return bookingCreated;
    }
    
    private void loadAvailableDates() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 1; i <= 7; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dates.add(calendar.getTime());
        }
        availableDates.setValue(dates);
    }
    
    private void loadTimeSlots() {
        List<TimeSlot> slots = new ArrayList<>();
        
        TimeSlot slot1 = new TimeSlot();
        slot1.setTime("6:00 - 7:00 AM");
        slot1.setCapacity("Available");
        slot1.setPrice(300);
        slot1.setAvailable(true);
        slots.add(slot1);
        
        TimeSlot slot2 = new TimeSlot();
        slot2.setTime("7:00 - 8:00 AM");
        slot2.setCapacity("Available");
        slot2.setPrice(300);
        slot2.setAvailable(true);
        slots.add(slot2);
        
        TimeSlot slot3 = new TimeSlot();
        slot3.setTime("8:00 - 9:00 AM");
        slot3.setCapacity("Few left");
        slot3.setPrice(300);
        slot3.setAvailable(true);
        slots.add(slot3);
        
        TimeSlot slot4 = new TimeSlot();
        slot4.setTime("9:00 - 10:00 AM");
        slot4.setCapacity("Full");
        slot4.setPrice(300);
        slot4.setAvailable(false);
        slots.add(slot4);
        
        timeSlots.setValue(slots);
    }
    
    public void selectDate(Date date) {
        selectedDate.setValue(date);
        currentStep.setValue(2);
    }
    
    public void selectSlot(TimeSlot slot) {
        if (slot.isAvailable()) {
            selectedSlot.setValue(slot);
            currentStep.setValue(3);
        }
    }
    
    public void incrementDevoteeCount() {
        int current = devoteeCount.getValue() != null ? devoteeCount.getValue() : 1;
        if (current < 10) {
            devoteeCount.setValue(current + 1);
        }
    }
    
    public void decrementDevoteeCount() {
        int current = devoteeCount.getValue() != null ? devoteeCount.getValue() : 1;
        if (current > 1) {
            devoteeCount.setValue(current - 1);
        }
    }
    
    public void setDevoteeName(String name) {
        devoteeName.setValue(name);
    }
    
    public void continueToPayment() {
        if (devoteeName.getValue() != null && !devoteeName.getValue().isEmpty()) {
            currentStep.setValue(4);
        }
    }
    
    public void confirmBooking() {
        isLoading.setValue(true);
        
        Booking booking = new Booking();
        booking.setTemple("Temple Name");
        booking.setDate(selectedDate.getValue() != null ? selectedDate.getValue().toString() : "");
        booking.setTime(selectedSlot.getValue() != null ? selectedSlot.getValue().getTime() : "");
        booking.setDevotees(devoteeCount.getValue());
        booking.setType("Pre-Booked");
        booking.setAmount((double) (selectedSlot.getValue() != null ? selectedSlot.getValue().getPrice() * (devoteeCount.getValue() != null ? devoteeCount.getValue() : 1) + 20 : 0));
        
        repository.createBooking(booking, new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                isLoading.postValue(false);
                if (response.body() != null && response.body().isSuccess()) {
                    bookingCreated.postValue(response.body().getData());
                } else {
                    errorMessage.postValue("Failed to create booking");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error. Please check your connection.");
            }
        });
    }
}

