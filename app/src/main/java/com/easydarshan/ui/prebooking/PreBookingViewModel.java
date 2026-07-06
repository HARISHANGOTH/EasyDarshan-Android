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
import com.easydarshan.data.model.Temple;
import com.easydarshan.data.model.TimeSlot;
import com.easydarshan.data.model.Visitor;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.utils.ErrorHandler;

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
    private MutableLiveData<Temple> temple = new MutableLiveData<>();
    private MutableLiveData<String> selectedDate = new MutableLiveData<>();
    private MutableLiveData<TimeSlot> selectedSlot = new MutableLiveData<>();
    private MutableLiveData<List<Visitor>> visitors = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<String> tempReferenceId = new MutableLiveData<>();
    private MutableLiveData<List<String>> availableDates = new MutableLiveData<>();
    private MutableLiveData<List<TimeSlot>> timeSlots = new MutableLiveData<>();
    private MutableLiveData<CreateBookingResponse> bookingCreated = new MutableLiveData<>();
    private MutableLiveData<PaymentOrderResponse> paymentOrderCreated = new MutableLiveData<>();

    private Long templeId;
    private Long darshanTypeId;
    private String selectedDarshanType;
    private String bookingId;
    private String orderReference;
    private boolean isLiveQueue = false;
    private boolean isBookingInProgress = false;

    public PreBookingViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
        
        // Initialize with one visitor
        List<Visitor> list = new ArrayList<>();
        list.add(new Visitor(true));
        visitors.setValue(list);
    }

    public void setTempleId(Long templeId) { 
        this.templeId = templeId;
        loadTempleDetails();
    }

    private void loadTempleDetails() {
        if (templeId == null) return;
        repository.getTempleDetails(templeId, new Callback<com.easydarshan.data.model.TempleDetailResponse>() {
            @Override
            public void onResponse(Call<com.easydarshan.data.model.TempleDetailResponse> call, Response<com.easydarshan.data.model.TempleDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    temple.postValue(response.body().getData().getTemple());
                }
            }
            @Override
            public void onFailure(Call<com.easydarshan.data.model.TempleDetailResponse> call, Throwable t) {}
        });
    }

    public void setDarshanTypeId(Long darshanTypeId) { this.darshanTypeId = darshanTypeId; }

    public Long getDarshanTypeId() { return darshanTypeId; }

    public void setSelectedDate(String date) { selectedDate.setValue(date); }

    public void setSelectedDarshanType(String darshanType) {
        this.selectedDarshanType = darshanType;
    }

    public String getSelectedDarshanType() {
        return selectedDarshanType;
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Integer> getCurrentStep() { return currentStep; }
    public void setCurrentStep(int step) { currentStep.setValue(step); }
    public LiveData<Temple> getTemple() { return temple; }
    public LiveData<String> getSelectedDate() { return selectedDate; }
    public LiveData<TimeSlot> getSelectedSlot() { return selectedSlot; }
    public LiveData<List<Visitor>> getVisitors() { return visitors; }
    public LiveData<String> getTempReferenceId() { return tempReferenceId; }
    public LiveData<List<String>> getAvailableDates() { return availableDates; }
    public LiveData<List<TimeSlot>> getTimeSlots() { return timeSlots; }
    public LiveData<CreateBookingResponse> getBookingCreated() { return bookingCreated; }
    public LiveData<PaymentOrderResponse> getPaymentOrderCreated() { return paymentOrderCreated; }

    public String getBookingId() { return bookingId; }
    public String getOrderReference() { return orderReference; }

    public void addVisitor() {
        List<Visitor> list = visitors.getValue();
        if (list == null) list = new ArrayList<>();
        if (list.size() < 10) {
            list.add(new Visitor(true));
            visitors.setValue(list);
        }
    }

    public void removeVisitor(int index) {
        List<Visitor> list = visitors.getValue();
        if (list != null && list.size() > 1) {
            list.remove(index);
            visitors.setValue(list);
        }
    }

    public void setVisitorCount(int count) {
        List<Visitor> list = visitors.getValue();
        if (list == null) list = new ArrayList<>();
        
        if (count > list.size()) {
            while (list.size() < count) list.add(new Visitor(true));
        } else if (count < list.size() && count >= 1) {
            while (list.size() > count) list.remove(list.size() - 1);
        }
        visitors.setValue(list);
    }

    public int getTotalDevotees() {
        return visitors.getValue() != null ? visitors.getValue().size() : 0;
    }

    public void continueToPayment() {
        generateTempReferenceId();
        currentStep.setValue(3);
    }

    private void generateTempReferenceId() {
        String datePart = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String randomPart = String.format("%04d", (int) (Math.random() * 10000));
        tempReferenceId.setValue("ED" + datePart + randomPart);
    }

    public void createBookingAndPayment(String paymentMethod) {
        if (isBookingInProgress) return;
        isBookingInProgress = true;
        isLoading.setValue(true);

        List<Visitor> visitorList = visitors.getValue();
        List<CreateBookingRequest.MemberRequest> members = new ArrayList<>();
        if (visitorList != null) {
            for (Visitor v : visitorList) {
                members.add(new CreateBookingRequest.MemberRequest(v.getFullName()));
            }
        }

        String date = selectedDate.getValue();
        if (date == null) date = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());

        CreateBookingRequest request = new CreateBookingRequest(
                templeId, darshanTypeId, null, date, members);

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
        if (bookingId == null || bookingId.isEmpty()) {
            errorMessage.postValue("Booking ID missing. Please try again.");
            isBookingInProgress = false;
            isLoading.postValue(false);
            return;
        }

        int unitPrice = (selectedDarshanType != null && selectedDarshanType.toLowerCase().contains("paid")) ? 500 : 300;
        int totalDevotees = getTotalDevotees();
        java.math.BigDecimal darshanAmount = java.math.BigDecimal.valueOf((long) unitPrice * totalDevotees);
        java.math.BigDecimal platformFee = java.math.BigDecimal.valueOf(20);

        PaymentOrderRequest request = new PaymentOrderRequest(bookingId, darshanAmount, platformFee, templeId);

        repository.createPaymentOrder(request, new Callback<PaymentOrderResponse>() {
            @Override
            public void onResponse(Call<PaymentOrderResponse> call, Response<PaymentOrderResponse> response) {
                isBookingInProgress = false;
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    orderReference = response.body().getOrderReference();
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
}