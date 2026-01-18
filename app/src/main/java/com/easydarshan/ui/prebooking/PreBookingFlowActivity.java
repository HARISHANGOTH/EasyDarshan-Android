package com.easydarshan.ui.prebooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityPreBookingFlowBinding;
import com.easydarshan.data.model.Booking;
import com.easydarshan.ui.bookings.MyBookingsActivity;

public class PreBookingFlowActivity extends AppCompatActivity {
    
    private ActivityPreBookingFlowBinding binding;
    private PreBookingViewModel viewModel;
    private int templeId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreBookingFlowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        templeId = getIntent().getIntExtra("temple_id", 1);
        
        viewModel = new ViewModelProvider(this).get(PreBookingViewModel.class);
        viewModel.setTempleId(templeId);
        
        setupObservers();
        setupListeners();
    }
    
    private void setupObservers() {
        viewModel.getCurrentStep().observe(this, step -> {
            updateStepVisibility(step);
        });
        
        viewModel.getDevoteeCount().observe(this, count -> {
            if (count != null) {
                TextView devoteeCountView = findViewById(R.id.devoteeCount);
                if (devoteeCountView != null) {
                    devoteeCountView.setText(String.valueOf(count));
                }
            }
        });
        
        viewModel.getDevoteeName().observe(this, name -> {
            if (name != null) {
                View continueBtn = findViewById(R.id.continueToPaymentButton);
                if (continueBtn != null) {
                    continueBtn.setEnabled(!name.isEmpty());
                }
            }
        });
        
        viewModel.getSelectedSlot().observe(this, slot -> {
            if (slot != null) {
                int count = viewModel.getDevoteeCount().getValue() != null ? viewModel.getDevoteeCount().getValue() : 1;
                int total = slot.getPrice() * count + 20;
                TextView totalAmountView = findViewById(R.id.totalAmount);
                if (totalAmountView != null) {
                    totalAmountView.setText("₹" + total);
                }
                android.widget.Button confirmBtn = findViewById(R.id.confirmPayButton);
                if (confirmBtn != null) {
                    confirmBtn.setText("Confirm & Pay ₹" + total);
                }
            }
        });
        
        viewModel.getBookingCreated().observe(this, booking -> {
            if (booking != null) {
                Intent intent = new Intent(this, MyBookingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateStepVisibility(int step) {
        View step1 = findViewById(R.id.step1Content);
        View step2 = findViewById(R.id.step2Content);
        View step3 = findViewById(R.id.step3Content);
        View step4 = findViewById(R.id.step4Content);
        
        if (step1 != null) step1.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        if (step2 != null) step2.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        if (step3 != null) step3.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        if (step4 != null) step4.setVisibility(step == 4 ? View.VISIBLE : View.GONE);
    }
    
    private void setupListeners() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        findViewById(R.id.incrementButton).setOnClickListener(v -> viewModel.incrementDevoteeCount());
        findViewById(R.id.decrementButton).setOnClickListener(v -> viewModel.decrementDevoteeCount());
        
        android.widget.EditText devoteeNameInput = findViewById(R.id.devoteeNameInput);
        if (devoteeNameInput != null) {
            devoteeNameInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    viewModel.setDevoteeName(s.toString());
                }
                
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
        
        findViewById(R.id.continueToPaymentButton).setOnClickListener(v -> viewModel.continueToPayment());
        findViewById(R.id.confirmPayButton).setOnClickListener(v -> viewModel.confirmBooking());
    }
}

