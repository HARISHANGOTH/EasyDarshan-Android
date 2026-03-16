package com.easydarshan.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityProfileBinding;
import com.easydarshan.ui.BaseActivity;
import com.easydarshan.ui.profileupdate.ProfileUpdateActivity;

public class ProfileActivity extends BaseActivity {
    
    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this, 
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ProfileViewModel.class);
        
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
                if (user.getName() != null) {
                    binding.userName.setText(user.getName());
                }
                if (user.getPhone() != null) {
                    binding.userPhone.setText(user.getPhone());
                }
                // Load profile photo if available
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    // Use Glide to load image
                    com.bumptech.glide.Glide.with(this)
                            .load(user.getAvatar())
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .into(binding.profilePhoto);
                }
            }
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupListeners() {
        // Click on profile photo opens update profile
        binding.profilePhotoContainer.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileUpdateActivity.class);
            startActivity(intent);
        });
        
        // Logout button
        binding.logoutButton.setOnClickListener(v -> {
            viewModel.logout();
            Intent intent = new Intent(this, com.easydarshan.ui.login.MobileLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finishAffinity(); // Clear all activities
        });
    }
}
