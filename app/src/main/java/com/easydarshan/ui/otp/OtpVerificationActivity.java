package com.easydarshan.ui.otp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityOtpVerificationBinding;
import com.easydarshan.data.model.User;
import com.easydarshan.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class OtpVerificationActivity extends AppCompatActivity {
    
    private ActivityOtpVerificationBinding binding;
    private OtpVerificationViewModel viewModel;
    private List<EditText> otpInputs;
    private String mobile;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        mobile = getIntent().getStringExtra("mobile");
        if (mobile == null) {
            finish();
            return;
        }
        
        viewModel = new ViewModelProvider(this).get(OtpVerificationViewModel.class);
        viewModel.setMobile(mobile);
        
        binding.subtitleText.setText("Enter the 6-digit code sent to\n+91 " + mobile);
        
        setupOtpInputs();
        setupObservers();
        setupListeners();
        
        viewModel.startTimer();
    }
    
    private void setupOtpInputs() {
        otpInputs = new ArrayList<>();
        otpInputs.add(binding.otpInput1);
        otpInputs.add(binding.otpInput2);
        otpInputs.add(binding.otpInput3);
        otpInputs.add(binding.otpInput4);
        otpInputs.add(binding.otpInput5);
        otpInputs.add(binding.otpInput6);
        
        for (int i = 0; i < otpInputs.size(); i++) {
            final int index = i;
            EditText input = otpInputs.get(i);
            
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.size() - 1) {
                        otpInputs.get(index + 1).requestFocus();
                    }
                    checkOtpComplete();
                }
                
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
    
    private void checkOtpComplete() {
        StringBuilder otp = new StringBuilder();
        for (EditText input : otpInputs) {
            otp.append(input.getText().toString());
        }
        
        if (otp.length() == 6) {
            binding.verifyButton.setEnabled(true);
        } else {
            binding.verifyButton.setEnabled(false);
        }
    }
    
    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.verifyButton.setEnabled(!isLoading && isOtpComplete());
            binding.verifyButton.setText(isLoading ? "Verifying..." : "Verify & Continue");
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
        
        viewModel.getNavigateToHome().observe(this, user -> {
            if (user != null) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        viewModel.getTimer().observe(this, seconds -> {
            if (seconds > 0) {
                binding.resendTimer.setText("Resend OTP in " + seconds + "s");
                binding.resendTimer.setVisibility(View.VISIBLE);
                binding.resendButton.setVisibility(View.GONE);
            } else {
                binding.resendTimer.setVisibility(View.GONE);
                binding.resendButton.setVisibility(View.VISIBLE);
            }
        });
    }
    
    private boolean isOtpComplete() {
        for (EditText input : otpInputs) {
            if (input.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    private void setupListeners() {
        binding.resendButton.setOnClickListener(v -> {
            viewModel.resendOtp();
            for (EditText input : otpInputs) {
                input.setText("");
            }
            otpInputs.get(0).requestFocus();
        });

        binding.verifyButton.setOnClickListener(v -> {
            StringBuilder otp = new StringBuilder();
            for (EditText input : otpInputs) {
                otp.append(input.getText().toString());
            }
            viewModel.verifyOtp(otp.toString());
        });
    }
}
