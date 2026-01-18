package com.easydarshan.data.repository;

import com.easydarshan.data.api.ApiService;
import com.easydarshan.data.api.DummyApiService;
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
    private DummyApiService dummyApiService;
    
    private AppRepository() {
        dummyApiService = new DummyApiService();
    }
    
    public static synchronized AppRepository getInstance() {
        if (instance == null) {
            instance = new AppRepository();
        }
        return instance;
    }
    
    public void sendOtp(OtpRequest request, Callback<ApiResponse<String>> callback) {
        dummyApiService.sendOtp(request, callback);
    }
    
    public void verifyOtp(OtpVerifyRequest request, Callback<ApiResponse<User>> callback) {
        dummyApiService.verifyOtp(request, callback);
    }
    
    public void getTemples(String search, Callback<ApiResponse<List<Temple>>> callback) {
        dummyApiService.getTemples(search, callback);
    }
    
    public void getTempleDetails(int id, Callback<ApiResponse<Temple>> callback) {
        dummyApiService.getTempleDetails(id, callback);
    }
    
    public void getBookings(String status, Callback<ApiResponse<List<Booking>>> callback) {
        dummyApiService.getBookings(status, callback);
    }
    
    public void getBookingDetails(String id, Callback<ApiResponse<Booking>> callback) {
        dummyApiService.getBookingDetails(id, callback);
    }
    
    public void createBooking(Booking booking, Callback<ApiResponse<Booking>> callback) {
        dummyApiService.createBooking(booking, callback);
    }
    
    public void getNotifications(Callback<ApiResponse<List<Notification>>> callback) {
        dummyApiService.getNotifications(callback);
    }
    
    public void markNotificationsRead(Callback<ApiResponse<Void>> callback) {
        dummyApiService.markNotificationsRead(callback);
    }
    
    public void getUserProfile(Callback<ApiResponse<User>> callback) {
        dummyApiService.getUserProfile(callback);
    }
    
    public void updateUserProfile(User user, Callback<ApiResponse<User>> callback) {
        dummyApiService.updateUserProfile(user, callback);
    }
}

