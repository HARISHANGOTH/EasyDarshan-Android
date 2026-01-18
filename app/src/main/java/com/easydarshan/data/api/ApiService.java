package com.easydarshan.data.api;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.Booking;
import com.easydarshan.data.model.Notification;
import com.easydarshan.data.model.OtpRequest;
import com.easydarshan.data.model.OtpVerifyRequest;
import com.easydarshan.data.model.Temple;
import com.easydarshan.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // Authentication endpoints (no auth required)
    @POST("api/v1/auth/send-otp")
    Call<ApiResponse<String>> sendOtp(@Body OtpRequest request);
    
    @POST("api/v1/auth/verify-otp")
    Call<com.easydarshan.data.model.VerifyOtpResponse> verifyOtp(@Body OtpVerifyRequest request);
    
    // Temple endpoints (no auth required)
    @GET("api/v1/temples")
    Call<ApiResponse<List<Temple>>> getTemples(@Query("search") String search);
    
    @GET("api/v1/temples/featured")
    Call<ApiResponse<List<Temple>>> getFeaturedTemples();
    
    @GET("api/v1/temples/{id}")
    Call<ApiResponse<Temple>> getTempleDetails(@Path("id") int id);
    
    // Booking endpoints (auth required)
    @GET("api/v1/bookings")
    Call<ApiResponse<List<Booking>>> getBookings(@Query("status") String status);
    
    @GET("api/v1/bookings/{id}")
    Call<ApiResponse<Booking>> getBookingDetails(@Path("id") String id);
    
    @POST("api/v1/bookings")
    Call<ApiResponse<Booking>> createBooking(@Body Booking booking);
    
    @DELETE("api/v1/bookings/{id}")
    Call<ApiResponse<Void>> cancelBooking(@Path("id") String id);
    
    // Notification endpoints (auth required)
    @GET("api/v1/notifications")
    Call<ApiResponse<List<Notification>>> getNotifications();
    
    @PATCH("api/v1/notifications/{id}/read")
    Call<ApiResponse<Void>> markNotificationRead(@Path("id") Long id);
    
    // User profile endpoints (auth required)
    @GET("api/v1/users/profile")
    Call<ApiResponse<User>> getUserProfile();
    
    @PUT("api/v1/users/profile")
    Call<ApiResponse<User>> updateUserProfile(@Body User user);
}

