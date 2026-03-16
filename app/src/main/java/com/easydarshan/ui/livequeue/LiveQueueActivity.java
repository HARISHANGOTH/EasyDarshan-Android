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

        if (templeName != null) {
            binding.tvTempleName.setText(templeName);
        }

        setupListeners();
        startQueuePolling();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnViewQr.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingDetailsActivity.class);
            intent.putExtra("booking_id", bookingId);
            startActivity(intent);
        });
    }

    private void startQueuePolling() {
        statusPoller = new Runnable() {
            @Override
            public void run() {
                fetchQueueStatus();
                // Poll every 5 seconds
                handler.postDelayed(this, 5000);
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
        binding.tvQueuePosition.setText("#" + position);
        
        // Dynamic wait time calculation (e.g., 3 mins per person)
        int waitTimeMinutes = position * 3;
        binding.tvWaitTime.setText(waitTimeMinutes + " mins");

        if (position <= 1 && !isAtEntryPoint) {
            showEntryPointAlert();
        } else if (position == 0) {
            // position 0 means scanned/completed
            Toast.makeText(this, "Welcome to the Temple!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void showEntryPointAlert() {
        isAtEntryPoint = true;
        binding.cvEntryPointAlert.setVisibility(View.VISIBLE);
        binding.tvStatusHeader.setText("You are next!");
        binding.tvWaitTime.setText("Direct Entry");
        binding.tvInstruction.setText("Please show your QR code to the temple team now.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && statusPoller != null) {
            handler.removeCallbacks(statusPoller);
        }
    }
}
