package com.easydarshan.data.repository;

import com.easydarshan.data.api.ApiService;
import com.easydarshan.data.api.RetrofitClient;
import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.Booking;
import com.easydarshan.data.model.Notification;
import com.easydarshan.data.model.OtpRequest;
import com.easydarshan.data.model.OtpVerifyRequest;
import com.easydarshan.data.model.Temple;
import com.easydarshan.data.model.User;

import java.util.List;

import retrofit2.Callback;

public class AppRepository {
    
    private static AppRepository instance;
    private ApiService apiService;
    
    private AppRepository() {
        apiService = RetrofitClient.getInstance().getApiService();
    }
    
    public static synchronized AppRepository getInstance() {
        if (instance == null) {
            instance = new AppRepository();
        }
        return instance;
    }
    
    public void sendOtp(OtpRequest request, Callback<ApiResponse<String>> callback) {
        apiService.sendOtp(request).enqueue(callback);
    }
    
    public void verifyOtp(OtpVerifyRequest request, Callback<com.easydarshan.data.model.VerifyOtpResponse> callback) {
        apiService.verifyOtp(request).enqueue(callback);
    }
    
    public void getTemples(String search, Callback<ApiResponse<List<Temple>>> callback) {
        apiService.getTemples(search).enqueue(callback);
    }
    
    public void getTempleDetails(int id, Callback<ApiResponse<Temple>> callback) {
        apiService.getTempleDetails(id).enqueue(callback);
    }
    
    public void getBookings(String status, Callback<ApiResponse<List<Booking>>> callback) {
        apiService.getBookings(status).enqueue(callback);
    }
    
    public void getBookingDetails(String id, Callback<ApiResponse<Booking>> callback) {
        apiService.getBookingDetails(id).enqueue(callback);
    }
    
    public void createBooking(Booking booking, Callback<ApiResponse<Booking>> callback) {
        apiService.createBooking(booking).enqueue(callback);
    }
    
    public void cancelBooking(String id, Callback<ApiResponse<Void>> callback) {
        apiService.cancelBooking(id).enqueue(callback);
    }
    
    public void getNotifications(Callback<ApiResponse<List<Notification>>> callback) {
        apiService.getNotifications().enqueue(callback);
    }
    
    public void markNotificationRead(Long id, Callback<ApiResponse<Void>> callback) {
        apiService.markNotificationRead(id).enqueue(callback);
    }
    
    public void getUserProfile(Callback<ApiResponse<User>> callback) {
        apiService.getUserProfile().enqueue(callback);
    }
    
    public void updateUserProfile(User user, Callback<ApiResponse<User>> callback) {
        apiService.updateUserProfile(user).enqueue(callback);
    }
}

