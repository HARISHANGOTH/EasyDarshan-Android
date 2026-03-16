package com.easydarshan.data.repository;

import android.content.Context;

import com.easydarshan.data.api.ApiService;
import com.easydarshan.data.api.RetrofitClient;
import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.AvailableDatesResponse;
import com.easydarshan.data.model.Booking;
import com.easydarshan.data.model.BookingListResponse;
import com.easydarshan.data.model.CreateBookingRequest;
import com.easydarshan.data.model.CreateBookingResponse;
import com.easydarshan.data.model.DeviceRegistrationRequest;
import com.easydarshan.data.model.LiveQueueActivateRequest;
import com.easydarshan.data.model.LiveQueueActivateResponse;
import com.easydarshan.data.model.LiveQueueDraftRequest;
import com.easydarshan.data.model.LiveQueuePositionResponse;
import com.easydarshan.data.model.Notification;
import com.easydarshan.data.model.OtpRequest;
import com.easydarshan.data.model.OtpVerifyRequest;
import com.easydarshan.data.model.PaymentOrderRequest;
import com.easydarshan.data.model.PaymentOrderResponse;
import com.easydarshan.data.model.PaymentVerificationRequest;
import com.easydarshan.data.model.PaymentVerificationResponse;
import com.easydarshan.data.model.SingleBookingResponse;
import com.easydarshan.data.model.SlotLockRequest;
import com.easydarshan.data.model.SlotsResponse;
import com.easydarshan.data.model.Temple;
import com.easydarshan.data.model.TempleListResponse;
import com.easydarshan.data.model.UpdatePasswordRequest;
import com.easydarshan.data.model.UploadAvatarRequest;
import com.easydarshan.data.model.User;
import com.easydarshan.data.model.VerifyOtpResponse;

import java.util.List;

import retrofit2.Callback;

public class AppRepository {
    
    private static AppRepository instance;
    private ApiService apiService;
    private Context context;
    
    private AppRepository(Context context) {
        this.context = context;
        apiService = RetrofitClient.getInstance(context).getApiService();
    }
    
    public static synchronized AppRepository getInstance(Context context) {
        if (instance == null || instance.context != context) {
            instance = new AppRepository(context);
        }
        return instance;
    }
    
    // Authentication
    public void sendOtp(OtpRequest request, Callback<ApiResponse<String>> callback) {
        apiService.sendOtp(request).enqueue(callback);
    }
    
    public void verifyOtp(OtpVerifyRequest request, Callback<VerifyOtpResponse> callback) {
        apiService.verifyOtp(request).enqueue(callback);
    }
    
    // Temples
    public void getTemples(String search, Callback<TempleListResponse> callback) {
        apiService.getTemples(search).enqueue(callback);
    }
    
    public void getFeaturedTemples(Callback<TempleListResponse> callback) {
        apiService.getFeaturedTemples().enqueue(callback);
    }
    
    public void getTempleDetails(Long id, Callback<Temple> callback) {
        apiService.getTempleDetails(id).enqueue(callback);
    }
    
    public void getTempleDarshans(Long id, Callback<ApiResponse<List<com.easydarshan.data.model.DarshanType>>> callback) {
        apiService.getTempleDarshans(id).enqueue(callback);
    }
    
    // Booking Flow
    public void getAvailableDates(Long templeId, Long darshanTypeId, Callback<AvailableDatesResponse> callback) {
        apiService.getAvailableDates(templeId, darshanTypeId).enqueue(callback);
    }
    
    public void getSlots(String date, Long templeId, Long darshanTypeId, Callback<SlotsResponse> callback) {
        apiService.getSlots(date, templeId, darshanTypeId).enqueue(callback);
    }
    
    public void lockSlot(SlotLockRequest request, Callback<com.easydarshan.data.model.SlotLockResponse> callback) {
        apiService.lockSlot(request).enqueue(callback);
    }
    
    public void releaseSlot(String lockId, Callback<ApiResponse<Void>> callback) {
        apiService.releaseSlot(lockId).enqueue(callback);
    }
    
    // Bookings
    public void getBookings(String status, Callback<BookingListResponse> callback) {
        apiService.getBookings(status).enqueue(callback);
    }
    
    public void getBookingDetails(String id, Callback<SingleBookingResponse> callback) {
        apiService.getBookingDetails(id).enqueue(callback);
    }
    
    public void createBooking(CreateBookingRequest request, Callback<CreateBookingResponse> callback) {
        apiService.createBooking(request).enqueue(callback);
    }
    
    public void cancelBooking(String id, Callback<ApiResponse<Void>> callback) {
        apiService.cancelBooking(id).enqueue(callback);
    }
    
    public void getBookingTicket(String id, Callback<SingleBookingResponse> callback) {
        apiService.getBookingTicket(id).enqueue(callback);
    }
    
    // Payment
    public void calculatePayment(String bookingId, Callback<ApiResponse<com.easydarshan.data.model.PaymentCalculationResponse>> callback) {
        apiService.calculatePayment(bookingId).enqueue(callback);
    }
    
    public void createPaymentOrder(PaymentOrderRequest request, Callback<PaymentOrderResponse> callback) {
        apiService.createPaymentOrder(request).enqueue(callback);
    }
    
    public void verifyPayment(PaymentVerificationRequest request, Callback<PaymentVerificationResponse> callback) {
        apiService.verifyPayment(request).enqueue(callback);
    }
    
    // Live Queue
    public void createLiveQueueDraft(LiveQueueDraftRequest request, Callback<ApiResponse<com.easydarshan.data.model.LiveQueueDraftResponse>> callback) {
        apiService.createLiveQueueDraft(request).enqueue(callback);
    }
    
    public void activateLiveQueue(LiveQueueActivateRequest request, Callback<LiveQueueActivateResponse> callback) {
        apiService.activateLiveQueue(request).enqueue(callback);
    }
    
    public void getQueuePosition(String queueId, Callback<LiveQueuePositionResponse> callback) {
        apiService.getQueuePosition(queueId).enqueue(callback);
    }
    
    // Notifications
    public void getNotifications(Callback<ApiResponse<List<Notification>>> callback) {
        apiService.getNotifications().enqueue(callback);
    }
    
    public void markNotificationRead(Long id, Callback<ApiResponse<Void>> callback) {
        apiService.markNotificationRead(id).enqueue(callback);
    }
    
    public void registerDevice(DeviceRegistrationRequest request, Callback<ApiResponse<Void>> callback) {
        apiService.registerDevice(request).enqueue(callback);
    }
    
    // User Profile
    public void getUserProfile(Callback<ApiResponse<User>> callback) {
        apiService.getUserProfile().enqueue(callback);
    }
    
    public void updateUserProfile(User user, Callback<ApiResponse<User>> callback) {
        apiService.updateUserProfile(user).enqueue(callback);
    }
    
    public void updatePassword(UpdatePasswordRequest request, Callback<ApiResponse<Void>> callback) {
        apiService.updatePassword(request).enqueue(callback);
    }
    
    public void uploadAvatar(UpdatePasswordRequest request, Callback<ApiResponse<String>> callback) {
        apiService.uploadAvatar(new UploadAvatarRequest(request.getNewPassword())).enqueue(callback);
    }
}
