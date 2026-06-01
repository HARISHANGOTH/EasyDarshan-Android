package com.easydarshan.ui.profileupdate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.easydarshan.R;
import com.easydarshan.databinding.ActivityProfileUpdateBinding;
import com.easydarshan.data.model.User;
import com.easydarshan.ui.profile.ProfileViewModel;

public class ProfileUpdateActivity extends AppCompatActivity {
    
    private ActivityProfileUpdateBinding binding;
    private ProfileViewModel viewModel;
    private static final int PICK_IMAGE = 100;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        
        setupObservers();
        setupListeners();
        
        viewModel.loadUserProfile();
    }
    
    private void setupObservers() {
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                if (user.getName() != null) binding.nameInput.setText(user.getName());
                if (user.getPhone() != null) binding.phoneInput.setText(user.getPhone());
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

        viewModel.getProfileUpdated().observe(this, updated -> {
            if (updated) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                viewModel.onUpdateHandled();
                finish();
            }
        });
    }
    
    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> finish());
        
        binding.profilePhotoContainer.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });
        
        binding.saveButton.setOnClickListener(v -> {
            User user = new User();
            user.setName(binding.nameInput.getText().toString().trim());
            user.setPhone(binding.phoneInput.getText().toString().trim());
            viewModel.updateUserProfile(user);
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                binding.profilePhoto.setImageURI(imageUri);
            }
        }
    }
}
