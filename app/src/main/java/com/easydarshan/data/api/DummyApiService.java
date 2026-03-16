package com.easydarshan.data.api;

import android.os.Handler;
import android.os.Looper;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.Booking;
import com.easydarshan.data.model.Notification;
import com.easydarshan.data.model.OtpRequest;
import com.easydarshan.data.model.OtpVerifyRequest;
import com.easydarshan.data.model.Temple;
import com.easydarshan.data.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DummyApiService {
    
    private static final String BASE_URL = "https://api.easydarshan.com/";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    public void sendOtp(OtpRequest request, Callback<ApiResponse<String>> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            ApiResponse<String> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("OTP sent successfully");
            response.setData("123456");
            
            Response<ApiResponse<String>> retrofitResponse = Response.success(response);
            handler.post(() -> callback.onResponse(null, retrofitResponse));
        });
    }
    
    public void verifyOtp(OtpVerifyRequest request, Callback<ApiResponse<User>> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            User user = new User();
            user.setName("User Name");
            user.setPhone(request.getMobile());
            user.setCity("City");
            user.setTotalVisits(12);
            
            ApiResponse<User> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("OTP verified successfully");
            response.setData(user);
            
            Response<ApiResponse<User>> retrofitResponse = Response.success(response);
            handler.post(() -> callback.onResponse(null, retrofitResponse));
        });
    }
    
    public void getTemples(String search, Callback<ApiResponse<List<Temple>>> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            List<Temple> temples = new ArrayList<>();
            temples.add(new Temple(1L, "Sri Venkateswara Temple", "Tirupati, Andhra Pradesh", "2.3 km", "high", "High Queue", ""));
            temples.add(new Temple(2L, "Golden Temple", "Amritsar, Punjab", "5.1 km", "medium", "Medium Queue", ""));
            temples.add(new Temple(3L, "Meenakshi Temple", "Madurai, Tamil Nadu", "8.7 km", "low", "Low Queue", ""));
            temples.add(new Temple(4L, "Kashi Vishwanath Temple", "Varanasi, Uttar Pradesh", "1.2 km", "medium", "Medium Queue", ""));
            
            ApiResponse<List<Temple>> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Temples fetched successfully");
            response.setData(temples);
            
            Response<ApiResponse<List<Temple>>> retrofitResponse = Response.success(response);
            handler.post(() -> callback.onResponse(null, retrofitResponse));
        });
    }
    
    public void getTempleDetails(int id, Callback<ApiResponse<Temple>> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            Temple temple = new Temple();
            temple.setId((long) id);
            temple.setName("Sri Venkateswara Temple");
            temple.setLocation("Tirupati, Andhra Pradesh");
            temple.setDistance("2.3 km");
            temple.setOpeningTime("6:00 AM");
            temple.setClosingTime("9:00 PM");
            temple.setDescription("One of the most revered pilgrimage sites, this ancient temple attracts millions of devotees every year. Experience divine blessings and spiritual peace in the sacred atmosphere.");
            
            ApiResponse<Temple> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Temple details fetched successfully");
            response.setData(temple);
            
            Response<ApiResponse<Temple>> retrofitResponse = Response.success(response);
            handler.post(() -> callback.onResponse(null, retrofitResponse));
        });
    }
    
    public void getBookings(String status, Callback<ApiResponse<List<Booking>>> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            List<Booking> bookings = new ArrayList<>();
            
            if ("upcoming".equals(status)) {
                Booking booking = new Booking();
                booking.setId("TDB001");
                booking.setTemple("Sri Venkateswara Temple");
                booking.setLocation("Tirupati, Andhra Pradesh");
                booking.setDate("Jan 15, 2025");
                booking.setTime("8:00 - 9:00 AM");
                booking.setStatus("upcoming");
                booking.setType("Pre-Booked");
                booking.setDevotees(2);
                bookings.add(booking);
            } else if ("active".equals(status)) {
                Booking booking = new Booking();
                booking.setId("TDQ002");
                booking.setTemple("Golden Temple");
                booking.setLocation("Amritsar, Punjab");
                booking.setDate("Today");
                booking.setTime("Live Queue");
                booking.setStatus("waiting");
                booking.setType("Live Queue");
                booking.setPosition(12);
                bookings.add(booking);
            } else if ("completed".equals(status)) {
                Booking booking = new Booking();
                booking.setId("TDB003");
                booking.setTemple("Meenakshi Temple");
                booking.setLocation("Madurai, Tamil Nadu");
                booking.setDate("Dec 28, 2024");
                booking.setTime("6:00 - 7:00 AM");
                booking.setStatus("completed");
                booking.setType("Pre-Booked");
                booking.setDevotees(3);
                bookings.add(booking);
            }
            
            ApiResponse<List<Booking>> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Bookings fetched successfully");
            response.setData(bookings);
            
            Response<ApiResponse<List<Booking>>> retrofitResponse = Response.success(response);
            handler.post(() -> callback.onResponse(null, retrofitResponse));
        });
    }
    
    public void getBookingDetails(String id, Callback<ApiResponse<Booking>> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            Booking booking = new Booking();
            booking.setId(id);
            booking.setTemple("Sri Venkateswara Temple");
            booking.setLocation("Tirupati, Andhra Pradesh");
            booking.setDate("Jan 15, 2025");
            booking.setTime("8:00 - 9:00 AM");
            booking.setStatus("upcoming");
            booking.setType("Pre-Booked");
            booking.setDevotees(2);
            booking.setAmount(620.0);
            booking.setPaymentMethod("UPI");
            
            ApiResponse<Booking> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Booking details fetched successfully");
            response.setData(booking);
            
            Response<ApiResponse<Booking>> retrofitResponse = Response.success(response);
            handler.post(() -> callback.onResponse(null, retrofitResponse));
        });
    }
    
    public void createBooking(Booking booking, Callback<ApiResponse<Booking>> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            booking.setId("TDB" + System.currentTimeMillis());
            booking.setStatus("upcoming");
            
            ApiResponse<Booking> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Booking created successfully");
            response.setData(booking);
            
            Response<ApiResponse<Booking>> retrofitResponse = Response.success(response);
            handler.post(() -> callback.onResponse(null, retrofitResponse));
        });
    }
    
    public void getNotifications(Callback<ApiResponse<List<Notification>>> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            List<Notification> notifications = new ArrayList<>();
            
            Notification n1 = new Notification();
            n1.setId(1);
            n1.setType("reminder");
            n1.setTitle("Booking Reminder");
            n1.setMessage("Your darshan at Sri Venkateswara Temple is tomorrow at 8:00 AM");
            n1.setTime("2 hours ago");
            n1.setIcon("🔔");
            n1.setRead(false);
            notifications.add(n1);
            
            Notification n2 = new Notification();
            n2.setId(2);
            n2.setType("success");
            n2.setTitle("Booking Confirmed");
            n2.setMessage("Your darshan booking has been confirmed. Booking ID: TDB001");
            n2.setTime("1 day ago");
            n2.setIcon("✅");
            n2.setRead(false);
            notifications.add(n2);
            
            Notification n3 = new Notification();
            n3.setId(3);
            n3.setType("info");
            n3.setTitle("Special Darshan Available");
            n3.setMessage("Maha Shivaratri special darshan slots are now open for booking");
            n3.setTime("2 days ago");
            n3.setIcon("ℹ️");
            n3.setRead(true);
            notifications.add(n3);
            
            ApiResponse<List<Notification>> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Notifications fetched successfully");
            response.setData(notifications);
            
            Response<ApiResponse<List<Notification>>> retrofitResponse = Response.success(response);
            handler.post(() -> callback.onResponse(null, retrofitResponse));
        });
    }
    
    public void markNotificationsRead(Callback<ApiResponse<Void>> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Notifications marked as read");
            
            Response<ApiResponse<Void>> retrofitResponse = Response.success(response);
            handler.post(() -> callback.onResponse(null, retrofitResponse));
        });
    }
    
    public void getUserProfile(Callback<ApiResponse<User>> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            User user = new User();
            user.setName("User Name");
            user.setPhone("+91 9876543210");
            user.setCity("City");
            user.setTotalVisits(12);
            
            ApiResponse<User> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Profile fetched successfully");
            response.setData(user);
            
            Response<ApiResponse<User>> retrofitResponse = Response.success(response);
            handler.post(() -> callback.onResponse(null, retrofitResponse));
        });
    }
    
    public void updateUserProfile(User user, Callback<ApiResponse<User>> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            ApiResponse<User> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Profile updated successfully");
            response.setData(user);
            
            Response<ApiResponse<User>> retrofitResponse = Response.success(response);
            handler.post(() -> callback.onResponse(null, retrofitResponse));
        });
    }
}
