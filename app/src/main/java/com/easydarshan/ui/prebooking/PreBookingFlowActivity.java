package com.easydarshan.ui.prebooking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easydarshan.databinding.ActivityPreBookingFlowBinding;
import com.easydarshan.data.model.PaymentOrderResponse;
import com.easydarshan.data.model.PaymentVerificationRequest;
import com.easydarshan.data.model.PaymentVerificationResponse;
import com.easydarshan.data.model.User;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.data.session.SessionManager;
import com.easydarshan.payment.RazorpayPaymentHelper;
import com.easydarshan.ui.adapter.DateAdapter;
import com.easydarshan.ui.adapter.TimeSlotAdapter;
import com.easydarshan.ui.livequeue.LiveQueueActivity;
import com.razorpay.PaymentResultListener;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreBookingFlowActivity extends AppCompatActivity implements PaymentResultListener {
    
    private ActivityPreBookingFlowBinding binding;
    private PreBookingViewModel viewModel;
    private RazorpayPaymentHelper paymentHelper;
    private AppRepository repository;
    private DateAdapter dateAdapter;
    private TimeSlotAdapter timeSlotAdapter;
    private final Handler redirectHandler = new Handler(Looper.getMainLooper());
    private Runnable redirectRunnable;
    private boolean paymentInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreBookingFlowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        long templeId = getIntent().getLongExtra("temple_id", 1L);
        String templeName = getIntent().getStringExtra("temple_name");
        long darshanTypeId = getIntent().getLongExtra("darshan_type_id", 1L);
        String darshanType = getIntent().getStringExtra("darshan_type");

        if (templeName != null) {
            binding.templeName.setText(templeName);
        }
        
        viewModel = new ViewModelProvider(this).get(PreBookingViewModel.class);
        viewModel.setTempleId(templeId);
        viewModel.setDarshanTypeId(darshanTypeId);
        viewModel.setSelectedDarshanType(darshanType);
        
        repository = AppRepository.getInstance(this);
        paymentHelper = new RazorpayPaymentHelper(this, new RazorpayPaymentHelper.PaymentCallback() {
            @Override
            public void onPaymentSuccess(String paymentId, String orderId) {}
            @Override
            public void onPaymentError(int code, String response) {
                handlePaymentFailure();
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
        binding.datesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dateAdapter = new DateAdapter(new ArrayList<>(), date -> viewModel.selectDate(date));
        binding.datesRecyclerView.setAdapter(dateAdapter);

        binding.slotsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timeSlotAdapter = new TimeSlotAdapter(new ArrayList<>(), slot -> viewModel.selectSlot(slot));
        binding.slotsRecyclerView.setAdapter(timeSlotAdapter);
    }
    
    private void setupObservers() {
        viewModel.getCurrentStep().observe(this, step -> {
            updateStepVisibility(step);
            if (step == 4) {
                populatePaymentSummary();
            } else if (step == 6) {
                populateSuccessSummary();
                startAutoRedirect();
            }
        });
        
        viewModel.getAdultCount().observe(this, count -> {
            binding.adultCount.setText(String.valueOf(count));
            updateDevoteeNameInputs();
        });

        viewModel.getChildrenCount().observe(this, count -> {
            binding.childrenCount.setText(String.valueOf(count));
            updateDevoteeNameInputs();
        });
        
        viewModel.getAvailableDates().observe(this, dates -> dateAdapter.updateDates(dates));
        viewModel.getTimeSlots().observe(this, slots -> timeSlotAdapter.updateSlots(slots));
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                viewModel.clearErrorMessage();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> 
            binding.loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE));
        
        viewModel.getPaymentOrderCreated().observe(this, paymentOrder -> {
            if (paymentOrder != null && paymentOrder.isSuccess() && !paymentInProgress) {
                startRazorpayPayment(paymentOrder);
            }
        });
        
        viewModel.getBookingCreated().observe(this, booking -> {
            if (booking != null) {
                viewModel.setCurrentStep(6);
            }
        });
    }

    private void updateDevoteeNameInputs() {
        int adultCount = viewModel.getAdultCount().getValue() != null ? viewModel.getAdultCount().getValue() : 1;
        int childrenCount = viewModel.getChildrenCount().getValue() != null ? viewModel.getChildrenCount().getValue() : 0;
        int total = adultCount + childrenCount;

        binding.namesContainer.removeAllViews();
        for (int i = 0; i < total; i++) {
            com.google.android.material.textfield.TextInputLayout textInputLayout = new com.google.android.material.textfield.TextInputLayout(this, null, com.google.android.material.R.attr.textInputOutlinedStyle);
            android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 8, 0, 16);
            textInputLayout.setLayoutParams(lp);
            textInputLayout.setHint((i < adultCount ? "Adult " + (i + 1) : "Child " + (i - adultCount + 1)) + " Name");
            float radius = (float) dpToPx(12);
            textInputLayout.setBoxCornerRadii(radius, radius, radius, radius);

            com.google.android.material.textfield.TextInputEditText editText = new com.google.android.material.textfield.TextInputEditText(textInputLayout.getContext());
            textInputLayout.addView(editText);
            binding.namesContainer.addView(textInputLayout);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void populatePaymentSummary() {
        binding.summaryTempleName.setText(binding.templeName.getText());
        
        String dateStr = viewModel.getSelectedDate().getValue();
        if (dateStr != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateStr);
                if (date != null) binding.summaryDate.setText(outputFormat.format(date));
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
        binding.summaryDevotees.setText(String.format(Locale.getDefault(), "%d %s", count, count > 1 ? "Devotees" : "Devotee"));

        int unitPrice = 0;
        if (viewModel.isLiveQueue()) {
            String type = getIntent().getStringExtra("darshan_type");
            if (type != null && type.toLowerCase().contains("paid")) unitPrice = 500;
        } else if (viewModel.getSelectedSlot().getValue() != null) {
            unitPrice = viewModel.getSelectedSlot().getValue().getPrice();
        }

        int darshanTotal = unitPrice * count;
        int platformFee = 20;
        int total = darshanTotal + platformFee;

        binding.darshanFeeLabel.setText(String.format(Locale.getDefault(), "Darshan Fee (%d Members)", count));
        binding.darshanFee.setText(String.format(Locale.getDefault(), "₹%d", darshanTotal));
        binding.totalAmount.setText(String.format(Locale.getDefault(), "₹%d", total));
        binding.confirmPayButton.setText(total > 0 ? String.format(Locale.getDefault(), "Confirm & Pay ₹%d", total) : "Confirm Booking");
        
        List<String> names = viewModel.getDevoteeNames().getValue();
        if (names != null && !names.isEmpty()) {
            binding.summaryDevoteeNames.setText(String.format("Names: %s", String.join(", ", names)));
            binding.summaryDevoteeNames.setVisibility(View.VISIBLE);
        } else {
            binding.summaryDevoteeNames.setVisibility(View.GONE);
        }
        
        binding.summaryReferenceId.setText(viewModel.getTempReferenceId().getValue());
    }

    private void populateSuccessSummary() {
        binding.successTempleName.setText(binding.templeName.getText());
        binding.successPersons.setText(String.valueOf(viewModel.getTotalDevotees()));
        
        String refId = viewModel.getTempReferenceId().getValue();
        binding.successBookingId.setText(refId != null ? refId : String.format(Locale.getDefault(), "BK%d", System.currentTimeMillis() / 1000));
        binding.successToken.setText(String.format(Locale.getDefault(), "#%d", 100 + (int)(Math.random() * 900)));
        binding.successEntryTime.setText("11:30 AM"); 
    }

    private void startAutoRedirect() {
        if (redirectRunnable != null) redirectHandler.removeCallbacks(redirectRunnable);
        redirectRunnable = this::navigateToLiveQueue;
        redirectHandler.postDelayed(redirectRunnable, 3000);
    }

    private void navigateToLiveQueue() {
        if (redirectRunnable != null) {
            redirectHandler.removeCallbacks(redirectRunnable);
            redirectRunnable = null;
        }
        
        Intent intent = new Intent(this, LiveQueueActivity.class);
        String bId = viewModel.getBookingId();
        if (bId == null || bId.isEmpty()) bId = binding.successBookingId.getText().toString();
        
        intent.putExtra("booking_id", bId);
        intent.putExtra("temple_name", binding.templeName.getText().toString());
        intent.putExtra("booking_date", viewModel.getSelectedDate().getValue());
        intent.putExtra("devotees", viewModel.getTotalDevotees());

        startActivity(intent);
        finish();
    }
    
    private void updateStepVisibility(int step) {
        binding.stepContentContainer.setVisibility(View.VISIBLE);
        binding.step1Content.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        binding.step2Content.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        binding.step3Content.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        binding.step4Content.setVisibility(step == 4 ? View.VISIBLE : View.GONE);
        binding.verifyingContent.setVisibility(step == 5 ? View.VISIBLE : View.GONE);
        binding.successContent.setVisibility(step == 6 ? View.VISIBLE : View.GONE);
        binding.failedContent.setVisibility(step == 7 ? View.VISIBLE : View.GONE);
        
        binding.continueToPaymentButton.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        binding.confirmPayButton.setVisibility(step == 4 ? View.VISIBLE : View.GONE);
        
        boolean showHeader = step < 5;
        binding.headerLayout.setVisibility(showHeader ? View.VISIBLE : View.GONE);
        binding.bottomActionArea.setVisibility(showHeader ? View.VISIBLE : View.GONE);
    }
    
    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> {
            Integer step = viewModel.getCurrentStep().getValue();
            if (step != null && step > 1 && !viewModel.isLiveQueue()) {
                viewModel.setCurrentStep(step - 1);
            } else {
                finish();
            }
        });
        
        binding.incrementAdults.setOnClickListener(v -> viewModel.incrementAdults());
        binding.decrementAdults.setOnClickListener(v -> viewModel.decrementAdults());
        binding.incrementChildren.setOnClickListener(v -> viewModel.incrementChildren());
        binding.decrementChildren.setOnClickListener(v -> viewModel.decrementChildren());
        
        binding.continueToPaymentButton.setOnClickListener(v -> {
            List<String> names = new ArrayList<>();
            boolean allFilled = true;
            for (int i = 0; i < binding.namesContainer.getChildCount(); i++) {
                View view = binding.namesContainer.getChildAt(i);
                if (view instanceof com.google.android.material.textfield.TextInputLayout) {
                    com.google.android.material.textfield.TextInputLayout layout = (com.google.android.material.textfield.TextInputLayout) view;
                    String name = layout.getEditText() != null ? layout.getEditText().getText().toString().trim() : "";
                    if (name.isEmpty()) {
                        layout.setError("Name required");
                        allFilled = false;
                    } else {
                        layout.setError(null);
                        names.add(name);
                    }
                }
            }
            if (allFilled) {
                viewModel.setDevoteeNames(names);
                viewModel.continueToPayment();
            } else {
                Toast.makeText(this, "Please enter all devotee names", Toast.LENGTH_SHORT).show();
            }
        });
        
        binding.confirmPayButton.setOnClickListener(v -> {
            if (paymentInProgress) return;
            int total = calculateTotalAmount();
            viewModel.createBookingAndPayment(total > 0 ? "RAZORPAY" : "FREE");
        });

        binding.goToLiveQueueButton.setOnClickListener(v -> navigateToLiveQueue());
        binding.retryPaymentButton.setOnClickListener(v -> {
            paymentInProgress = false;
            viewModel.clearPaymentOrder();
            viewModel.setCurrentStep(4);
        });
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
        return (unitPrice * count) + 20;
    }

    private void startRazorpayPayment(PaymentOrderResponse paymentOrder) {
        if (paymentInProgress) return;
        paymentInProgress = true;
        String amountInPaise = String.valueOf(paymentOrder.getAmount().multiply(new BigDecimal(100)).intValue());
        User user = SessionManager.getInstance(this).getCurrentUser();
        paymentHelper.startStandardCheckout(paymentOrder.getPaymentOrderId(), amountInPaise, 
                user != null ? user.getPhone() : "", "user@easydarshan.com");
    }
    
    private void verifyPayment(String paymentId, String orderId, String signature) {
        viewModel.setCurrentStep(5);
        PaymentOrderResponse paymentOrder = viewModel.getPaymentOrderCreated().getValue();
        PaymentVerificationRequest request = new PaymentVerificationRequest(
                paymentOrder != null ? paymentOrder.getPaymentOrderId() : orderId,
                paymentId, signature != null ? signature : "", null);

        repository.verifyPayment(request, new Callback<PaymentVerificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<PaymentVerificationResponse> call, @NonNull Response<PaymentVerificationResponse> response) {
                paymentInProgress = false;
                if (response.body() != null && response.body().isSuccess()) viewModel.confirmBooking();
                else viewModel.setCurrentStep(7);
            }
            @Override
            public void onFailure(@NonNull Call<PaymentVerificationResponse> call, @NonNull Throwable t) {
                paymentInProgress = false;
                viewModel.setCurrentStep(7);
            }
        });
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        PaymentOrderResponse order = viewModel.getPaymentOrderCreated().getValue();
        verifyPayment(razorpayPaymentId, order != null ? order.getPaymentOrderId() : "", "");
    }

    @Override
    public void onPaymentError(int code, String response) { handlePaymentFailure(); }

    private void handlePaymentFailure() {
        paymentInProgress = false;
        viewModel.setCurrentStep(7);
    }

    @Override
    protected void onDestroy() {
        if (redirectRunnable != null) redirectHandler.removeCallbacks(redirectRunnable);
        super.onDestroy();
    }
}
