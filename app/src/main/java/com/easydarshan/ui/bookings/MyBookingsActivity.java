package com.easydarshan.ui.bookings;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityMyBookingsBinding;
import com.easydarshan.ui.BaseActivity;
import com.easydarshan.ui.adapter.BookingAdapter;

public class MyBookingsActivity extends BaseActivity {
    
    private ActivityMyBookingsBinding binding;
    private MyBookingsViewModel viewModel;
    private BookingAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this).get(MyBookingsViewModel.class);
        
        setupRecyclerView();
        setupObservers();
        setupListeners();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadBookings("upcoming");
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_bookings;
    }
    
    private void setupRecyclerView() {
        adapter = new BookingAdapter(null, booking -> {
            // Navigate to booking details
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
        
        viewModel.getCurrentTab().observe(this, this::updateTabSelection);
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateTabSelection(String currentTab) {
        binding.tabUpcoming.setSelected("upcoming".equals(currentTab));
        binding.tabActive.setSelected("active".equals(currentTab));
        binding.tabCompleted.setSelected("completed".equals(currentTab));
        binding.tabCancelled.setSelected("cancelled".equals(currentTab));
    }
    
    private void setupListeners() {
        binding.tabUpcoming.setOnClickListener(v -> viewModel.setCurrentTab("upcoming"));
        binding.tabActive.setOnClickListener(v -> viewModel.setCurrentTab("active"));
        binding.tabCompleted.setOnClickListener(v -> viewModel.setCurrentTab("completed"));
        binding.tabCancelled.setOnClickListener(v -> viewModel.setCurrentTab("cancelled"));
    }
}
