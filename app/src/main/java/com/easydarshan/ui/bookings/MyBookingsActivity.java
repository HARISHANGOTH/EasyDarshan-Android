package com.easydarshan.ui.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityMyBookingsBinding;
import com.easydarshan.ui.BaseActivity;
import com.easydarshan.ui.adapter.BookingAdapter;
import com.easydarshan.ui.paymenthistory.PaymentHistoryActivity;
import com.google.android.material.tabs.TabLayout;

public class MyBookingsActivity extends BaseActivity {
    
    private ActivityMyBookingsBinding binding;
    private MyBookingsViewModel viewModel;
    private BookingAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this, 
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(MyBookingsViewModel.class);
        
        setupRecyclerView();
        setupTabs();
        setupObservers();
        setupListeners();
        setupBottomNavigation();
        
        // Initial load with original "active" status for compatibility
        viewModel.loadBookings("active");
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCurrentTab();
    }

    private void refreshCurrentTab() {
        int selectedTab = binding.tabLayout.getSelectedTabPosition();
        if (selectedTab == 0) {
            viewModel.loadBookings("active");
        } else {
            viewModel.loadBookings("completed");
        }
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_bookings;
    }
    
    private void setupRecyclerView() {
        adapter = new BookingAdapter(null, booking -> {
            Intent intent = new Intent(this, BookingDetailsActivity.class);
            intent.putExtra("booking_id", booking.getId());
            startActivity(intent);
        });
        
        binding.bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.bookingsRecyclerView.setAdapter(adapter);
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    binding.emptyStateTitle.setText("No Active Bookings");
                    viewModel.loadBookings("active");
                } else {
                    binding.emptyStateTitle.setText("No Past Bookings");
                    viewModel.loadBookings("completed");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                refreshCurrentTab();
            }
        });
    }
    
    private void setupObservers() {
        viewModel.getBookings().observe(this, bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                adapter.updateList(bookings);
                binding.emptyState.setVisibility(View.GONE);
                binding.bookingsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                adapter.updateList(null);
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.bookingsRecyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupListeners() {
        if (binding.viewPreviousVisitsLink != null) {
            binding.viewPreviousVisitsLink.setOnClickListener(v -> {
                binding.tabLayout.getTabAt(1).select();
            });
        }
    }
}
