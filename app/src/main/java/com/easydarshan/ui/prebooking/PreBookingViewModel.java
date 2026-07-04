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
    private MutableLiveData<List<String>> devoteeNames = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<String> tempReferenceId = new MutableLiveData<>();
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
    private boolean isBookingInProgress = false;

    public PreBookingViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }

    public void setTempleId(Long templeId) { this.templeId = templeId; }
    public void setDarshanTypeId(Long darshanTypeId) { this.darshanTypeId = darshanTypeId; }

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
    public LiveData<List<String>> getDevoteeNames() { return devoteeNames; }
    public LiveData<String> getTempReferenceId() { return tempReferenceId; }
    public LiveData<List<String>> getAvailableDates() { return availableDates; }
    public LiveData<List<TimeSlot>> getTimeSlots() { return timeSlots; }
    public LiveData<CreateBookingResponse> getBookingCreated() { return bookingCreated; }
    public LiveData<PaymentOrderResponse> getPaymentOrderCreated() { return paymentOrderCreated; }

    public String getBookingId() { return bookingId; }

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
        generateTempReferenceId();
        currentStep.setValue(4);
    }

    private void generateTempReferenceId() {
        String datePart = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String randomPart = String.format("%04d", (int) (Math.random() * 10000));
        tempReferenceId.setValue("ED" + datePart + randomPart);
    }

    public void setDevoteeNames(List<String> names) {
        devoteeNames.setValue(names);
    }

    public void createBookingAndPayment(String paymentMethod) {
        if (isBookingInProgress) return;
        isBookingInProgress = true;
        isLoading.setValue(true);

        List<String> names = devoteeNames.getValue();
        List<CreateBookingRequest.MemberRequest> members = new ArrayList<>();
        if (names != null) {
            for (String name : names) {
                members.add(new CreateBookingRequest.MemberRequest(name));
            }
        }
        if (members.isEmpty()) {
            int total = getTotalDevotees();
            for (int i = 0; i < total; i++) {
                members.add(new CreateBookingRequest.MemberRequest("Devotee " + (i + 1)));
            }
        }

        Long slotIdLong = null;
        if (!isLiveQueue && selectedSlot.getValue() != null) {
            String slotIdStr = selectedSlot.getValue().getSlotId();
            if (slotIdStr != null) {
                try { slotIdLong = Long.parseLong(slotIdStr); } catch (NumberFormatException ignored) {}
            }
        }

        CreateBookingRequest request = new CreateBookingRequest(
                templeId, darshanTypeId, slotIdLong, selectedDate.getValue(), members);

        repository.createBooking(request, new Callback<CreateBookingResponse>() {
            @Override
            public void onResponse(Call<CreateBookingResponse> call, Response<CreateBookingResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getBookingReference() != null) {
                    bookingId = response.body().getBookingReference();
                    createPaymentOrder(paymentMethod);
                } else {
                    isBookingInProgress = false;
                    isLoading.postValue(false);
                    errorMessage.postValue("Booking failed. Please try again.");
                }
            }
            @Override
            public void onFailure(Call<CreateBookingResponse> call, Throwable t) {
                isBookingInProgress = false;
                isLoading.postValue(false);
                errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
            }
        });
    }

    private void createPaymentOrder(String paymentMethod) {
        // Guard: bookingId must be set from createBooking response
        if (bookingId == null || bookingId.isEmpty()) {
            errorMessage.postValue("Booking ID missing. Please try again.");
            isBookingInProgress = false;
            isLoading.postValue(false);
            return;
        }

        int unitPrice = isLiveQueue ?
                (selectedDarshanType != null && selectedDarshanType.toLowerCase().contains("paid") ? 500 : 0) :
                (selectedSlot.getValue() != null ? selectedSlot.getValue().getPrice() : 0);
        int totalDevotees = getTotalDevotees();
        java.math.BigDecimal darshanAmount = java.math.BigDecimal.valueOf((long) unitPrice * totalDevotees);
        java.math.BigDecimal platformFee = java.math.BigDecimal.valueOf(20);

        com.easydarshan.data.session.SessionManager sm =
                com.easydarshan.data.session.SessionManager.getInstance(getApplication());
        Long userId = null;
        try {
            String uid = sm.getCurrentUser() != null ? sm.getCurrentUser().getId() : null;
            if (uid != null) userId = Long.parseLong(uid);
        } catch (NumberFormatException ignored) {}

        PaymentOrderRequest request = new PaymentOrderRequest(
                bookingId, darshanAmount, platformFee, userId, templeId);

        repository.createPaymentOrder(request, new Callback<PaymentOrderResponse>() {
            @Override
            public void onResponse(Call<PaymentOrderResponse> call, Response<PaymentOrderResponse> response) {
                isBookingInProgress = false;
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    paymentOrderCreated.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to create payment order. Please try again.");
                }
            }
            @Override
            public void onFailure(Call<PaymentOrderResponse> call, Throwable t) {
                isBookingInProgress = false;
                isLoading.postValue(false);
                errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
            }
        });
    }

    public void confirmBooking() {
        String idToUse = (bookingId != null) ? bookingId : tempReferenceId.getValue();
        CreateBookingResponse response = new CreateBookingResponse();
        response.setBookingId(idToUse);
        response.setStatus("CONFIRMED");
        bookingCreated.postValue(response);
    }

    public boolean isLiveQueue() { return isLiveQueue; }
}
