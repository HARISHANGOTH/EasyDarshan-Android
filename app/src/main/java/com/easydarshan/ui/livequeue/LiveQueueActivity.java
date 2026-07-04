package com.easydarshan.ui.livequeue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.easydarshan.data.model.LiveQueuePositionResponse;
import com.easydarshan.databinding.ActivityLiveQueueBinding;
import com.easydarshan.ui.bookings.BookingDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LiveQueueActivity extends AppCompatActivity {

    private ActivityLiveQueueBinding binding;
    private LiveQueueViewModel viewModel;
    private String bookingId;
    private boolean isAtEntryPoint = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveQueueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookingId = getIntent().getStringExtra("booking_id");
        String templeName = getIntent().getStringExtra("temple_name");
        String date = getIntent().getStringExtra("booking_date");
        int devotees = getIntent().getIntExtra("devotees", 1);

        if (templeName != null) binding.tvTempleName.setText(templeName);
        
        binding.tvBookingDate.setText(date != null ? date : new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(new Date()));
        binding.tvDevotees.setText(String.format(Locale.getDefault(), "%d %s", devotees, devotees > 1 ? "Devotees" : "Devotee"));
        if (bookingId != null) binding.tvBookingId.setText(String.format("Booking ID: %s", bookingId));

        viewModel = new ViewModelProvider(this).get(LiveQueueViewModel.class);
        
        setupObservers();
        setupListeners();
        
        if (bookingId != null) viewModel.startPolling(bookingId);
    }

    private void setupObservers() {
        viewModel.getQueueStatus().observe(this, status -> {
            if (status != null) updateUI(status);
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnViewTicket.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingDetailsActivity.class);
            intent.putExtra("booking_id", bookingId);
            startActivity(intent);
        });

        binding.btnRefresh.setOnClickListener(v -> {
            v.setEnabled(false);
            viewModel.fetchStatus();
            v.postDelayed(() -> v.setEnabled(true), 2000);
        });
    }

    private void updateUI(LiveQueuePositionResponse status) {
        int position = status.getPosition();
        binding.tvPeopleAhead.setText(String.valueOf(position));
        
        int waitTime = position * 2;
        if (waitTime >= 60) {
            binding.tvWaitTime.setText(String.format(Locale.getDefault(), "%dh %dm", waitTime / 60, waitTime % 60));
        } else {
            binding.tvWaitTime.setText(String.format(Locale.getDefault(), "%dm", waitTime));
        }

        binding.tvLastUpdated.setText(String.format("Last Updated: %s", new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date())));

        if (position <= 1 && !isAtEntryPoint && position > 0) {
            isAtEntryPoint = true;
            Toast.makeText(this, "It's almost your turn! Please reach the entry point.", Toast.LENGTH_LONG).show();
        } else if (position == 0) {
            Toast.makeText(this, "Welcome to the Temple!", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
