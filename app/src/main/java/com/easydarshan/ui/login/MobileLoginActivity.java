package com.easydarshan.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.easydarshan.databinding.ActivityMobileLoginBinding;
import com.easydarshan.data.session.SessionManager;
import com.easydarshan.ui.otp.OtpVerificationActivity;
import com.google.android.material.snackbar.Snackbar;

public class MobileLoginActivity extends AppCompatActivity {
    
    private ActivityMobileLoginBinding binding;
    private MobileLoginViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMobileLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize SessionManager with context
        SessionManager.getInstance(this);
        
        viewModel = new ViewModelProvider(this).get(MobileLoginViewModel.class);
        
        setupObservers();
        setupListeners();
    }
    
    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.getOtpButton.setEnabled(!isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
            }
        });
        
        viewModel.getNavigateToOtp().observe(this, mobile -> {
            if (mobile != null) {
                Intent intent = new Intent(this, OtpVerificationActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
                viewModel.onNavigationComplete();
                finish();
            }
        });
    }
    
    private void setupListeners() {
        binding.mobileNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.getOtpButton.setEnabled(s.length() == 10);
                if (s.length() < 10) {
                    viewModel.clearErrorMessage();
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        binding.getOtpButton.setOnClickListener(v -> {
            hideKeyboard();
            String mobile = binding.mobileNumberInput.getText().toString().trim();
            viewModel.sendOtp(mobile);
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
