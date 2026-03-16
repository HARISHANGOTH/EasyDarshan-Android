package com.easydarshan.ui.paymenthistory;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityPaymentHistoryBinding;
import com.easydarshan.ui.adapter.BookingAdapter;
import com.easydarshan.ui.bookings.MyBookingsViewModel;

public class PaymentHistoryActivity extends AppCompatActivity {
    
    private ActivityPaymentHistoryBinding binding;
    private MyBookingsViewModel viewModel;
    private BookingAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this, 
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(MyBookingsViewModel.class);
        
        setupRecyclerView();
        setupObservers();
        setupListeners();
        
        // Load completed bookings (payment history)
        viewModel.loadBookings("completed");
    }
    
    private void setupRecyclerView() {
        adapter = new BookingAdapter(null, booking -> {
            // Show booking details
        });
        
        binding.bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.bookingsRecyclerView.setAdapter(adapter);
    }
    
    private void setupObservers() {
        viewModel.getBookings().observe(this, bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                adapter.updateList(bookings);
                binding.emptyState.setVisibility(View.GONE);
                binding.bookingsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.bookingsRecyclerView.setVisibility(View.GONE);
            }
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> finish());
    }
}





