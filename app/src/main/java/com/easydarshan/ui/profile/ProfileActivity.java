package com.easydarshan.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityProfileBinding;
import com.easydarshan.ui.BaseActivity;

public class ProfileActivity extends BaseActivity {
    
    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        
        setupObservers();
        setupListeners();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadUserProfile();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_profile;
    }
    
    private void setupObservers() {
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                binding.userName.setText(user.getName());
                binding.userPhone.setText(user.getPhone());
                binding.userLocation.setText(user.getCity());
                binding.totalVisits.setText(String.valueOf(user.getTotalVisits()));
            }
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupListeners() {
        binding.logoutButton.setOnClickListener(v -> {
            // Handle logout
            startActivity(new Intent(this, com.easydarshan.ui.login.MobileLoginActivity.class));
            finish();
        });
    }
}
