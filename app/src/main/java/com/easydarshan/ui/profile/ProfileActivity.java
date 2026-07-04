package com.easydarshan.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.easydarshan.R;
import com.easydarshan.databinding.ActivityProfileBinding;
import com.easydarshan.ui.BaseActivity;
import com.easydarshan.ui.login.MobileLoginActivity;
import com.easydarshan.ui.profileupdate.ProfileUpdateActivity;

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
                if (user.getName() != null) binding.userName.setText(user.getName());
                if (user.getPhone() != null) binding.userPhone.setText(user.getPhone());
                
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    Glide.with(this)
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
                viewModel.clearErrorMessage();
            }
        });
    }
    
    private void setupListeners() {
        binding.profilePhotoContainer.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileUpdateActivity.class);
            startActivity(intent);
        });
        
        binding.logoutButton.setOnClickListener(v -> {
            viewModel.logout();
            Intent intent = new Intent(this, MobileLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
