package com.easydarshan.ui.notifications;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityNotificationsBinding;
import com.easydarshan.ui.BaseActivity;
import com.easydarshan.ui.adapter.NotificationAdapter;

public class NotificationsActivity extends BaseActivity {
    
    private ActivityNotificationsBinding binding;
    private NotificationsViewModel viewModel;
    private NotificationAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        
        setupRecyclerView();
        setupObservers();
        setupListeners();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadNotifications();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_notifications;
    }
    
    private void setupRecyclerView() {
        adapter = new NotificationAdapter(null);
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.notificationsRecyclerView.setAdapter(adapter);
    }
    
    private void setupObservers() {
        viewModel.getNotifications().observe(this, notifications -> {
            if (notifications != null && !notifications.isEmpty()) {
                adapter.updateList(notifications);
                binding.emptyState.setVisibility(View.GONE);
                binding.notificationsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.notificationsRecyclerView.setVisibility(View.GONE);
            }
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupListeners() {
        binding.markAllReadButton.setOnClickListener(v -> viewModel.markAllAsRead());
    }
}
