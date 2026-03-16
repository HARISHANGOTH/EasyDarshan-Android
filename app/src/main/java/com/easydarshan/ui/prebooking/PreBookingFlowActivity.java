package com.easydarshan.ui.prebooking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityPreBookingFlowBinding;
import com.easydarshan.data.model.Booking;
import com.easydarshan.data.model.PaymentOrderResponse;
import com.easydarshan.data.model.PaymentVerificationRequest;
import com.easydarshan.data.model.PaymentVerificationResponse;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.payment.RazorpayPaymentHelper;
import com.easydarshan.ui.adapter.DateAdapter;
import com.easydarshan.ui.adapter.TimeSlotAdapter;
import com.easydarshan.ui.bookings.MyBookingsActivity;
import com.easydarshan.ui.livequeue.LiveQueueActivity;
import com.easydarshan.ui.payment.PaymentBottomSheet;
import com.razorpay.PaymentResultListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreBookingFlowActivity extends AppCompatActivity implements PaymentResultListener, PaymentBottomSheet.PaymentCallback {
    
    private ActivityPreBookingFlowBinding binding;
    private PreBookingViewModel viewModel;
    private Long templeId;
    private String templeName;
    private RazorpayPaymentHelper paymentHelper;
    private AppRepository repository;
    private DateAdapter dateAdapter;
    private TimeSlotAdapter timeSlotAdapter;
    private Handler redirectHandler = new Handler(Looper.getMainLooper());
    private Runnable redirectRunnable;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreBookingFlowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Handle temple info from intent
        templeId = getIntent().getLongExtra("temple_id", 1L);
        templeName = getIntent().getStringExtra("temple_name");
        if (templeName != null) {
            binding.templeName.setText(templeName);
        }
        
        Long darshanTypeId = getIntent().getLongExtra("darshan_type_id", 1L);
        String darshanType = getIntent().getStringExtra("darshan_type");
        
        viewModel = new ViewModelProvider(this, 
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(PreBookingViewModel.class);
        viewModel.setTempleId(templeId);
        viewModel.setDarshanTypeId(darshanTypeId);
        viewModel.setSelectedDarshanType(darshanType);
        
        repository = AppRepository.getInstance(getApplication());
        paymentHelper = new RazorpayPaymentHelper(this, new RazorpayPaymentHelper.PaymentCallback() {
            @Override
            public void onPaymentSuccess(String paymentId, String orderId) {
                verifyPayment(paymentId, orderId);
            }
            
            @Override
            public void onPaymentError(int code, String response) {
                viewModel.setCurrentStep(6); // Step 6: Failed
            }
        });
        
        setupRecyclerViews();
        if (!viewModel.isLiveQueue()) {
            viewModel.loadAvailableDates();
        }
        
        setupObservers();
        setupListeners();
    }
    
    private void setupRecyclerViews() {
        binding.datesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dateAdapter = new DateAdapter(new java.util.ArrayList<>(), date -> viewModel.selectDate(date));
        binding.datesRecyclerView.setAdapter(dateAdapter);

        binding.slotsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        timeSlotAdapter = new TimeSlotAdapter(new java.util.ArrayList<>(), slot -> viewModel.selectSlot(slot));
        binding.slotsRecyclerView.setAdapter(timeSlotAdapter);
    }
    
    private void setupObservers() {
        viewModel.getCurrentStep().observe(this, step -> {
            updateStepVisibility(step);
            if (step == 4) {
                populatePaymentSummary();
            } else if (step == 5) {
                populateSuccessSummary();
                startAutoRedirect();
            }
        });
        
        viewModel.getAdultCount().observe(this, count -> {
            if (count != null) {
                binding.adultCount.setText(String.valueOf(count));
            }
        });

        viewModel.getChildrenCount().observe(this, count -> {
            if (count != null) {
                binding.childrenCount.setText(String.valueOf(count));
            }
        });
        
        viewModel.getAvailableDates().observe(this, dates -> {
            if (dates != null && dateAdapter != null) {
                dateAdapter.updateDates(dates);
            }
        });

        viewModel.getTimeSlots().observe(this, slots -> {
            if (slots != null && timeSlotAdapter != null) {
                timeSlotAdapter.updateSlots(slots);
            }
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
        
        viewModel.getPaymentOrderCreated().observe(this, paymentOrder -> {
            if (paymentOrder != null && paymentOrder.isSuccess()) {
                startRazorpayPayment(paymentOrder);
            }
        });
        
        viewModel.getBookingCreated().observe(this, booking -> {
            if (booking != null) {
                viewModel.setCurrentStep(5);
            }
        });
    }

    private void populatePaymentSummary() {
        binding.summaryTempleName.setText(binding.templeName.getText());
        
        String dateStr = viewModel.getSelectedDate().getValue();
        if (dateStr != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
                java.util.Date date = inputFormat.parse(dateStr);
                binding.summaryDate.setText(outputFormat.format(date));
            } catch (Exception e) {
                binding.summaryDate.setText(dateStr);
            }
        }

        if (viewModel.isLiveQueue()) {
            binding.summaryTime.setText("Live Queue Entry");
        } else if (viewModel.getSelectedSlot().getValue() != null) {
            binding.summaryTime.setText(viewModel.getSelectedSlot().getValue().getTime());
        }

        int count = viewModel.getTotalDevotees();
        binding.summaryDevotees.setText(count + (count > 1 ? " Devotees" : " Devotee"));

        int unitPrice = 0;
        if (viewModel.isLiveQueue()) {
            String type = getIntent().getStringExtra("darshan_type");
            if (type != null && type.toLowerCase().contains("paid")) {
                unitPrice = 500;
            }
        } else if (viewModel.getSelectedSlot().getValue() != null) {
            unitPrice = viewModel.getSelectedSlot().getValue().getPrice();
        }

        int darshanTotal = unitPrice * count;
        // Fix: Platform fee is always 20
        int platformFee = 20;
        int total = darshanTotal + platformFee;

        binding.darshanFeeLabel.setText("Darshan Fee (" + count + " Members)");
        binding.darshanFee.setText("₹" + darshanTotal);
        binding.totalAmount.setText("₹" + total);
        binding.confirmPayButton.setText("Confirm & Pay ₹" + total);
        
        if (total == 0) {
            binding.confirmPayButton.setText("Confirm Booking");
        }
    }

    private void populateSuccessSummary() {
        binding.successTempleName.setText(binding.templeName.getText());
        binding.successPersons.setText(String.valueOf(viewModel.getTotalDevotees()));
        binding.successBookingId.setText("BK" + System.currentTimeMillis() / 100000);
        binding.successToken.setText("#" + (100 + (int)(Math.random() * 900)));
        binding.successEntryTime.setText("11:30 AM"); 
    }

    private void startAutoRedirect() {
        redirectRunnable = () -> navigateToLiveQueue();
        redirectHandler.postDelayed(redirectRunnable, 3000);
    }

    private void navigateToLiveQueue() {
        if (redirectRunnable != null) {
            redirectHandler.removeCallbacks(redirectRunnable);
            redirectRunnable = null;
        }
        Intent intent = new Intent(this, LiveQueueActivity.class);
        intent.putExtra("booking_id", binding.successBookingId.getText().toString());
        intent.putExtra("temple_name", binding.successTempleName.getText().toString());
        startActivity(intent);
        finish();
    }
    
    private void updateStepVisibility(int step) {
        binding.stepContentContainer.setVisibility(View.VISIBLE);
        binding.step1Content.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        binding.step2Content.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        binding.step3Content.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        binding.step4Content.setVisibility(step == 4 ? View.VISIBLE : View.GONE);
        binding.successContent.setVisibility(step == 5 ? View.VISIBLE : View.GONE);
        binding.failedContent.setVisibility(step == 6 ? View.VISIBLE : View.GONE);
        
        binding.continueToPaymentButton.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        binding.confirmPayButton.setVisibility(step == 4 ? View.VISIBLE : View.GONE);
        
        if (step >= 5) {
            binding.headerLayout.setVisibility(View.GONE);
            binding.bottomActionArea.setVisibility(View.GONE);
        } else {
            binding.headerLayout.setVisibility(View.VISIBLE);
            binding.bottomActionArea.setVisibility(View.VISIBLE);
        }
    }
    
    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> {
            Integer currentStep = viewModel.getCurrentStep().getValue();
            if (currentStep != null && currentStep > 1 && !viewModel.isLiveQueue()) {
                viewModel.setCurrentStep(currentStep - 1);
            } else {
                finish();
            }
        });
        
        binding.incrementAdults.setOnClickListener(v -> viewModel.incrementAdults());
        binding.decrementAdults.setOnClickListener(v -> viewModel.decrementAdults());
        binding.incrementChildren.setOnClickListener(v -> viewModel.incrementChildren());
        binding.decrementChildren.setOnClickListener(v -> viewModel.decrementChildren());
        
        binding.continueToPaymentButton.setOnClickListener(v -> viewModel.continueToPayment());
        
        binding.confirmPayButton.setOnClickListener(v -> {
            int total = calculateTotalAmount();
            // Free Queue also needs to pay Platform Fee (20)
            if (total > 0) {
                showPaymentBottomSheet(String.valueOf(total));
            } else {
                viewModel.createBookingAndPayment("FREE");
            }
        });

        binding.goToLiveQueueButton.setOnClickListener(v -> navigateToLiveQueue());
        
        binding.retryPaymentButton.setOnClickListener(v -> viewModel.setCurrentStep(4));
        binding.cancelBookingButton.setOnClickListener(v -> finish());
    }

    private int calculateTotalAmount() {
        int count = viewModel.getTotalDevotees();
        int unitPrice = 0;
        if (viewModel.isLiveQueue()) {
            String type = getIntent().getStringExtra("darshan_type");
            if (type != null && type.toLowerCase().contains("paid")) unitPrice = 500;
        } else if (viewModel.getSelectedSlot().getValue() != null) {
            unitPrice = viewModel.getSelectedSlot().getValue().getPrice();
        }
        // Always include platform fee of 20
        return (unitPrice * count) + 20;
    }

    private void showPaymentBottomSheet(String amount) {
        PaymentBottomSheet paymentBottomSheet = PaymentBottomSheet.newInstance(amount);
        paymentBottomSheet.show(getSupportFragmentManager(), "PaymentBottomSheet");
    }

    @Override
    public void onPaymentSuccess() {
        viewModel.createBookingAndPayment("UPI");
    }

    private void startRazorpayPayment(PaymentOrderResponse paymentOrder) {
        String amountInPaise = String.valueOf(paymentOrder.getAmount().multiply(new java.math.BigDecimal(100)).intValue());
        paymentHelper.startPayment(
                paymentOrder.getPaymentOrderId(),
                amountInPaise,
                "Harish",
                "Temple Darshan Booking Payment"
        );
    }
    
    private void verifyPayment(String paymentId, String orderId) {
        PaymentOrderResponse paymentOrder = viewModel.getPaymentOrderCreated().getValue();
        PaymentVerificationRequest request = new PaymentVerificationRequest(
                paymentOrder != null ? paymentOrder.getPaymentOrderId() : orderId,
                paymentId,
                ""
        );
        
        repository.verifyPayment(request, new Callback<PaymentVerificationResponse>() {
            @Override
            public void onResponse(Call<PaymentVerificationResponse> call, Response<PaymentVerificationResponse> response) {
                if (response.body() != null && response.body().isSuccess()) {
                    viewModel.confirmBooking();
                } else {
                    viewModel.setCurrentStep(6);
                }
            }
            @Override
            public void onFailure(Call<PaymentVerificationResponse> call, Throwable t) {
                viewModel.setCurrentStep(6);
            }
        });
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        PaymentOrderResponse paymentOrder = viewModel.getPaymentOrderCreated().getValue();
        verifyPayment(razorpayPaymentId, paymentOrder != null ? paymentOrder.getPaymentOrderId() : "");
    }

    @Override
    public void onPaymentError(int code, String response) {
        viewModel.setCurrentStep(6);
    }

    @Override
    protected void onDestroy() {
        if (redirectRunnable != null) {
            redirectHandler.removeCallbacks(redirectRunnable);
        }
        super.onDestroy();
    }
}
