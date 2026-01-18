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
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    @POST("auth/send-otp")
    Call<ApiResponse<String>> sendOtp(@Body OtpRequest request);
    
    @POST("auth/verify-otp")
    Call<ApiResponse<User>> verifyOtp(@Body OtpVerifyRequest request);
    
    @GET("temples")
    Call<ApiResponse<List<Temple>>> getTemples(@Query("search") String search);
    
    @GET("temples/{id}")
    Call<ApiResponse<Temple>> getTempleDetails(@Path("id") int id);
    
    @GET("bookings")
    Call<ApiResponse<List<Booking>>> getBookings(@Query("status") String status);
    
    @GET("bookings/{id}")
    Call<ApiResponse<Booking>> getBookingDetails(@Path("id") String id);
    
    @POST("bookings")
    Call<ApiResponse<Booking>> createBooking(@Body Booking booking);
    
    @GET("notifications")
    Call<ApiResponse<List<Notification>>> getNotifications();
    
    @POST("notifications/mark-read")
    Call<ApiResponse<Void>> markNotificationsRead();
    
    @GET("user/profile")
    Call<ApiResponse<User>> getUserProfile();
    
    @POST("user/profile")
    Call<ApiResponse<User>> updateUserProfile(@Body User user);
}

