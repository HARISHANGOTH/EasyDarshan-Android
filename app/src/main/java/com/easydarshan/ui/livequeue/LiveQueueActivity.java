package com.easydarshan.ui.livequeue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easydarshan.data.api.ApiService;
import com.easydarshan.data.api.RetrofitClient;
import com.easydarshan.data.model.LiveQueuePositionResponse;
import com.easydarshan.databinding.ActivityLiveQueueBinding;
import com.easydarshan.ui.bookings.BookingDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveQueueActivity extends AppCompatActivity {

    private ActivityLiveQueueBinding binding;
    private String bookingId;
    private String templeName;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable statusPoller;
    private boolean isAtEntryPoint = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveQueueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookingId = getIntent().getStringExtra("booking_id");
        templeName = getIntent().getStringExtra("temple_name");
        String date = getIntent().getStringExtra("booking_date");
        int devotees = getIntent().getIntExtra("devotees", 1);

        if (templeName != null) {
            binding.tvTempleName.setText(templeName);
        }
        
        if (date != null) {
            binding.tvBookingDate.setText(date);
        } else {
            binding.tvBookingDate.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(new Date()));
        }
        
        binding.tvDevotees.setText(devotees + (devotees > 1 ? " Devotees" : " Devotee"));
        
        if (bookingId != null) {
            binding.tvBookingId.setText("Booking ID: " + bookingId);
        }

        setupListeners();
        startQueuePolling();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnViewTicket.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingDetailsActivity.class);
            intent.putExtra("booking_id", bookingId);
            startActivity(intent);
        });

        binding.btnRefresh.setOnClickListener(v -> {
            binding.btnRefresh.setEnabled(false);
            fetchQueueStatus();
            handler.postDelayed(() -> binding.btnRefresh.setEnabled(true), 2000);
        });
    }

    private void startQueuePolling() {
        statusPoller = new Runnable() {
            @Override
            public void run() {
                fetchQueueStatus();
                // Poll every 10 seconds
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(statusPoller);
    }

    private void fetchQueueStatus() {
        if (bookingId == null) return;

        ApiService apiService = RetrofitClient.getInstance(this).getApiService();
        apiService.getQueuePosition(bookingId).enqueue(new Callback<LiveQueuePositionResponse>() {
            @Override
            public void onResponse(Call<LiveQueuePositionResponse> call, Response<LiveQueuePositionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                }
            }

            @Override
            public void onFailure(Call<LiveQueuePositionResponse> call, Throwable t) {
                // Silently handle failure for polling
            }
        });
    }

    private void updateUI(LiveQueuePositionResponse status) {
        int position = status.getPosition();
        binding.tvPeopleAhead.setText(String.valueOf(position));
        
        // Dynamic wait time calculation (e.g., 2 mins per person)
        int waitTimeMinutes = position * 2;
        if (waitTimeMinutes >= 60) {
            int hours = waitTimeMinutes / 60;
            int mins = waitTimeMinutes % 60;
            binding.tvWaitTime.setText(hours + "h " + mins + "m");
        } else {
            binding.tvWaitTime.setText(waitTimeMinutes + "m");
        }

        String currentTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
        binding.tvLastUpdated.setText("Last Updated: " + currentTime);

        if (position <= 1 && !isAtEntryPoint) {
            isAtEntryPoint = true;
            Toast.makeText(this, "It's almost your turn! Please reach the entry point.", Toast.LENGTH_LONG).show();
        } else if (position == 0) {
            // position 0 means scanned/completed
            Toast.makeText(this, "Welcome to the Temple!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && statusPoller != null) {
            handler.removeCallbacks(statusPoller);
        }
    }
}
