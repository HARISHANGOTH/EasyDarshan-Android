package com.easydarshan.data.api;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.AvailableDatesResponse;
import com.easydarshan.data.model.Booking;
import com.easydarshan.data.model.BookingListResponse;
import com.easydarshan.data.model.CreateBookingRequest;
import com.easydarshan.data.model.CreateBookingResponse;
import com.easydarshan.data.model.LiveQueueActivateRequest;
import com.easydarshan.data.model.LiveQueueActivateResponse;
import com.easydarshan.data.model.LiveQueuePositionResponse;
import com.easydarshan.data.model.Notification;
import com.easydarshan.data.model.OtpReponse;
import com.easydarshan.data.model.OtpRequest;
import com.easydarshan.data.model.OtpVerifyRequest;
import com.easydarshan.data.model.PaymentOrderRequest;
import com.easydarshan.data.model.PaymentOrderResponse;
import com.easydarshan.data.model.PaymentVerificationRequest;
import com.easydarshan.data.model.PaymentVerificationResponse;
import com.easydarshan.data.model.SingleBookingResponse;
import com.easydarshan.data.model.SlotsResponse;
import com.easydarshan.data.model.Temple;
import com.easydarshan.data.model.TempleListResponse;
import com.easydarshan.data.model.User;
import com.easydarshan.data.model.VerifyOtpResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // Authentication endpoints (no auth required)
    @POST("/api/v1/auth/register")
    Call<OtpReponse> sendOtp(@Body OtpRequest request);
    
    @POST("api/v1/auth/verify-otp")
    Call<VerifyOtpResponse> verifyOtp(@Body OtpVerifyRequest request);
    
    // Temple endpoints (no auth required)
    @GET("api/v1/temples")
    Call<TempleListResponse> getTemples(@Query("search") String search);

//    curl --location 'https://deflected-emotion-deviant.ngrok-free.dev/api/v1/temples/search?temple=tirupati'
    
    @GET("api/v1/temples/featured")
    Call<TempleListResponse> getFeaturedTemples();
    
    @GET("api/v1/temples/{id}")
    Call<Temple> getTempleDetails(@Path("id") Long id);
    
    @GET("api/v1/temples/{id}/darshans")
    Call<ApiResponse<List<com.easydarshan.data.model.DarshanType>>> getTempleDarshans(@Path("id") Long id);
    
    // Booking flow endpoints (auth required)
    @GET("api/v1/darshan/available-dates")
    Call<AvailableDatesResponse> getAvailableDates(
            @Query("templeId") Long templeId,
            @Query("darshanTypeId") Long darshanTypeId);
    
    @GET("api/v1/darshan/slots")
    Call<SlotsResponse> getSlots(
            @Query("date") String date,
            @Query("templeId") Long templeId,
            @Query("darshanTypeId") Long darshanTypeId);
    
    @POST("api/v1/darshan/slot-lock")
    Call<com.easydarshan.data.model.SlotLockResponse> lockSlot(@Body com.easydarshan.data.model.SlotLockRequest request);
    
    @DELETE("api/v1/darshan/slot-lock/{lockId}")
    Call<ApiResponse<Void>> releaseSlot(@Path("lockId") String lockId);
    
    // Booking endpoints (auth required)
    @GET("api/v1/bookings")
    Call<BookingListResponse> getBookings(@Query("status") String status);
    
    @GET("api/v1/bookings/{id}")
    Call<SingleBookingResponse> getBookingDetails(@Path("id") String id);
    
    @POST("api/v1/bookings")
    Call<CreateBookingResponse> createBooking(@Body CreateBookingRequest request);
    
    @DELETE("api/v1/bookings/{id}")
    Call<ApiResponse<Void>> cancelBooking(@Path("id") String id);
    
    @GET("api/v1/bookings/{id}/ticket")
    Call<SingleBookingResponse> getBookingTicket(@Path("id") String id);
    
    // Payment endpoints (auth required)
    @GET("api/v1/payments/calculate/{bookingId}")
    Call<ApiResponse<com.easydarshan.data.model.PaymentCalculationResponse>> calculatePayment(@Path("bookingId") String bookingId);
    
    @POST("api/v1/payments/order")
    Call<PaymentOrderResponse> createPaymentOrder(@Body PaymentOrderRequest request);
    
    @POST("api/v1/payments/verify")
    Call<PaymentVerificationResponse> verifyPayment(@Body PaymentVerificationRequest request);
    
    // Live Queue endpoints (auth required)
    @POST("api/v1/live-queue/draft")
    Call<ApiResponse<com.easydarshan.data.model.LiveQueueDraftResponse>> createLiveQueueDraft(@Body com.easydarshan.data.model.LiveQueueDraftRequest request);
    
    @POST("api/v1/live-queue/activate")
    Call<LiveQueueActivateResponse> activateLiveQueue(@Body LiveQueueActivateRequest request);
    
    @GET("api/v1/live-queue/{queueId}/status")
    Call<LiveQueuePositionResponse> getQueuePosition(@Path("queueId") String queueId);
    
    // Notification endpoints (auth required)
    @GET("api/v1/notifications")
    Call<ApiResponse<List<Notification>>> getNotifications();
    
    @PATCH("api/v1/notifications/{id}/read")
    Call<ApiResponse<Void>> markNotificationRead(@Path("id") Long id);
    
    @POST("api/v1/notifications/register-device")
    Call<ApiResponse<Void>> registerDevice(@Body com.easydarshan.data.model.DeviceRegistrationRequest request);
    
    // User profile endpoints (auth required)
    @GET("api/v1/users/profile")
    Call<ApiResponse<User>> getUserProfile();
    
    @PUT("api/v1/users/profile")
    Call<ApiResponse<User>> updateUserProfile(@Body User user);
    
    @PUT("api/v1/users/password")
    Call<ApiResponse<Void>> updatePassword(@Body com.easydarshan.data.model.UpdatePasswordRequest request);
    
    @POST("api/v1/users/avatar")
    Call<ApiResponse<String>> uploadAvatar(@Body com.easydarshan.data.model.UploadAvatarRequest request);
}
