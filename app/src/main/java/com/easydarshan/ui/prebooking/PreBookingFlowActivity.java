package com.easydarshan.ui.prebooking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.easydarshan.R;
import com.easydarshan.databinding.ActivityPreBookingFlowBinding;
import com.easydarshan.databinding.ItemVisitorCardBinding;
import com.easydarshan.data.model.PaymentOrderResponse;
import com.easydarshan.data.model.PaymentVerificationRequest;
import com.easydarshan.data.model.PaymentVerificationResponse;
import com.easydarshan.data.model.Visitor;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.payment.RazorpayPaymentHelper;
import com.easydarshan.ui.BaseActivity;
import com.easydarshan.ui.livequeue.LiveQueueActivity;
import com.razorpay.PaymentResultListener;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreBookingFlowActivity extends BaseActivity implements PaymentResultListener {
    
    private ActivityPreBookingFlowBinding binding;
    private PreBookingViewModel viewModel;
    private String templeName;
    private RazorpayPaymentHelper paymentHelper;
    private AppRepository repository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreBookingFlowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        Long templeId = getIntent().getLongExtra("temple_id", 1L);
        templeName = getIntent().getStringExtra("temple_name");
        if (templeName != null) binding.templeName.setText(templeName);
        
        viewModel = new ViewModelProvider(this, 
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(PreBookingViewModel.class);
        viewModel.setTempleId(templeId);
        
        repository = AppRepository.getInstance(getApplication());
        paymentHelper = new RazorpayPaymentHelper(this, new RazorpayPaymentHelper.PaymentCallback() {
            @Override
            public void onPaymentSuccess(String paymentId, String orderId) { verifyPayment(paymentId, "dev_signature"); }
            @Override
            public void onPaymentError(int code, String response) { viewModel.setCurrentStep(5); }
        });
        
        setupObservers();
        setupListeners();
    }
    
    private void setupObservers() {
        viewModel.getTemple().observe(this, temple -> {
            if (temple != null) {
                this.templeName = temple.getName();
                binding.templeName.setText(temple.getName());
                binding.templeLocation.setText(temple.getLocation());
                Glide.with(this).load(temple.getImage()).placeholder(R.drawable.ic_temple_placeholder).into(binding.ivTempleIcon);
            }
        });

        viewModel.getCurrentStep().observe(this, this::updateStepVisibility);
        
        viewModel.getVisitors().observe(this, visitors -> {
            if (visitors != null) {
                binding.tvGlobalVisitorCount.setText(String.valueOf(visitors.size()));
                renderVisitorCards(visitors);
            }
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
        
        viewModel.getPaymentOrderCreated().observe(this, paymentOrder -> {
            if (paymentOrder != null && paymentOrder.isSuccess()) startRazorpayPayment(paymentOrder);
        });
        
        viewModel.getBookingCreated().observe(this, booking -> {
            if (booking != null) viewModel.setCurrentStep(4);
        });
    }

    private void updateStepVisibility(int step) {
        binding.step1Content.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        binding.step2Content.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        binding.step3Content.setVisibility(View.GONE); // Extras screen always hidden
        binding.step4Content.setVisibility(step == 3 ? View.VISIBLE : View.GONE); // logic step 3 is Review & Pay
        binding.successContent.setVisibility(step == 4 ? View.VISIBLE : View.GONE);
        binding.failedContent.setVisibility(step == 5 ? View.VISIBLE : View.GONE);

        // UI design: Hide stepper and main toolbar on success screen
        boolean isSuccess = step == 4;
        binding.stepperLayout.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
        binding.preBookingToolbar.setVisibility(isSuccess ? View.GONE : View.VISIBLE);

        updateStepperUI(step);

        if (step >= 4) binding.bottomActionArea.setVisibility(View.GONE);
        else binding.bottomActionArea.setVisibility(View.VISIBLE);

        if (step == 3) populatePaymentSummary();
        if (step == 4) populateSuccessDetails();
    }

    private void populateSuccessDetails() {
        com.easydarshan.data.model.CreateBookingResponse booking = viewModel.getBookingCreated().getValue();
        if (booking == null) return;

        binding.successBookingId.setText(booking.getBookingId());
        String now = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new java.util.Date());
        binding.successConfirmDate.setText(now);

        com.easydarshan.data.model.Temple temple = viewModel.getTemple().getValue();
        if (temple != null) {
            binding.successTempleName.setText(temple.getName());
            binding.successTempleLoc.setText(temple.getLocation());
            Glide.with(this).load(temple.getImage()).placeholder(R.drawable.ic_temple_placeholder).into(binding.successTempleIcon);
        }

        binding.successDate.setText(binding.chipDate.getText().toString());
        
        List<Visitor> visitors = viewModel.getVisitors().getValue();
        int count = visitors != null ? visitors.size() : 0;
        binding.successMeta.setText(count + " Visitors\n" + viewModel.getSelectedDarshanType());
        binding.successVisitorsCount.setText("Visitors (" + count + ")");

        // Payment Summary Re-sync
        int unitPrice = calculateUnitPrice();
        int total = (unitPrice * count) + 10;
        binding.successPaidLabel.setText("Darshan Amount (" + count + " × ₹" + unitPrice + ")");
        binding.successPaidAmount.setText("₹" + (unitPrice * count));
        binding.successTotalPaid.setText("₹" + total);

        // Click listeners for actions
        binding.btnViewPass.setOnClickListener(v -> navigateToLiveQueue());
        binding.btnDownloadReceipt.setOnClickListener(v -> Toast.makeText(this, "Downloading Receipt...", Toast.LENGTH_SHORT).show());
        binding.btnShareBooking.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "My Darshan is Booked at " + (temple != null ? temple.getName() : "Temple") + "! Booking ID: " + booking.getBookingId());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, null));
        });
    }

    private void updateStepperUI(int step) {
        // Step 1
        binding.step1Circle.setBackgroundResource(step >= 1 ? R.drawable.bg_step_done : R.drawable.bg_step_todo);
        binding.step1Circle.setText(step > 1 ? "✓" : "1");
        binding.step1Circle.setTextColor(ContextCompat.getColor(this, step >= 1 ? R.color.white : R.color.foreground));
        binding.label1.setTextColor(ContextCompat.getColor(this, step >= 1 ? R.color.primary : R.color.foreground_secondary));

        // Step 2
        binding.step2Circle.setBackgroundResource(step >= 2 ? R.drawable.bg_step_done : R.drawable.bg_step_todo);
        binding.step2Circle.setText(step > 2 ? "✓" : "2");
        binding.step2Circle.setTextColor(ContextCompat.getColor(this, step >= 2 ? R.color.white : R.color.foreground));
        binding.label2.setTextColor(ContextCompat.getColor(this, step >= 2 ? R.color.primary : R.color.foreground_secondary));

        // Step 3 (Review & Pay)
        binding.step3Circle.setBackgroundResource(step >= 3 ? R.drawable.bg_step_done : R.drawable.bg_step_todo);
        binding.step3Circle.setText("3");
        binding.step3Circle.setTextColor(ContextCompat.getColor(this, step >= 3 ? R.color.white : R.color.foreground));
        binding.label3.setTextColor(ContextCompat.getColor(this, step >= 3 ? R.color.primary : R.color.foreground_secondary));
    }
    
    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> {
            Integer currentStep = viewModel.getCurrentStep().getValue();
            if (currentStep != null && currentStep > 1) {
                if (currentStep == 3) viewModel.setCurrentStep(2);
                else viewModel.setCurrentStep(currentStep - 1);
            } else finish();
        });

        // Step 1 clicks
        binding.cardSpecial.setOnClickListener(v -> selectDarshanType(1));
        binding.cardSarva.setOnClickListener(v -> selectDarshanType(2));

        // Step 2 Global Counter
        binding.btnIncrementGlobal.setOnClickListener(v -> viewModel.addVisitor());
        binding.btnDecrementGlobal.setOnClickListener(v -> {
            int current = viewModel.getTotalDevotees();
            if (current > 1) viewModel.removeVisitor(current - 1);
        });
        binding.btnAddVisitor.setOnClickListener(v -> viewModel.addVisitor());
        
        binding.continueButton.setOnClickListener(v -> {
            Integer step = viewModel.getCurrentStep().getValue();
            if (step == 1) {
                if (viewModel.getDarshanTypeId() == null) {
                    Toast.makeText(this, "Please select a darshan type", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.setCurrentStep(2);
            } else if (step == 2) {
                if (validateAllVisitors()) viewModel.setCurrentStep(3); // Directly to Review
            } else if (step == 3) {
                viewModel.createBookingAndPayment(calculateTotalAmount() > 0 ? "RAZORPAY" : "FREE");
            }
        });

        binding.chipDate.setOnClickListener(v -> showDatePicker());
        binding.retryPaymentButton.setOnClickListener(v -> viewModel.setCurrentStep(3));
    }

    private void selectDarshanType(int selection) {
        binding.rbSpecial.setChecked(selection == 1);
        binding.rbSarva.setChecked(selection == 2);
        binding.cardSpecial.setStrokeColor(ContextCompat.getColor(this, selection == 1 ? R.color.primary : R.color.border_light));
        binding.cardSarva.setStrokeColor(ContextCompat.getColor(this, selection == 2 ? R.color.primary : R.color.border_light));

        String type = (selection == 1) ? "Special Entry" : "Sarva Darshan";
        viewModel.setSelectedDarshanType(type);
        viewModel.setDarshanTypeId((long) selection); // 1 = Special, 2 = Sarva
        binding.chipType.setText(type.split(" ")[0]);
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
            cal.set(year, month, day);
            String displayFormatted = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.getTime());
            viewModel.setSelectedDate(date);
            binding.chipDate.setText(displayFormatted);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void renderVisitorCards(List<Visitor> visitors) {
        binding.visitorCardsContainer.removeAllViews();
        String[] ageRanges = {"Under 5", "5 - 12 Years", "12 - 25 Years", "25 - 60 Years", "Over 60"};
        String[] idProofs = {"None", "Aadhaar Card", "Voter ID", "Driving License", "Passport"};
        
        for (int i = 0; i < visitors.size(); i++) {
            Visitor visitor = visitors.get(i);
            ItemVisitorCardBinding itemBinding = ItemVisitorCardBinding.inflate(getLayoutInflater(), binding.visitorCardsContainer, false);
            
            itemBinding.tvVisitorTitle.setText("Visitor " + (i + 1));
            itemBinding.tvPrimaryBadge.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
            itemBinding.etFullName.setText(visitor.getFullName());
            
            ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ageRanges);
            itemBinding.spinnerAge.setAdapter(ageAdapter);
            if (visitor.getAge() != null) itemBinding.spinnerAge.setText(visitor.getAge(), false);
            
            ArrayAdapter<String> idAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, idProofs);
            itemBinding.spinnerIdProof.setAdapter(idAdapter);
            if (visitor.getIdProof() == null) visitor.setIdProof("None");
            itemBinding.spinnerIdProof.setText(visitor.getIdProof(), false);
            
            itemBinding.spinnerIdProof.setOnItemClickListener((parent, view, position, id) -> {
                String selected = idProofs[position];
                visitor.setIdProof(selected);
                itemBinding.tilIdNumber.setVisibility(selected.equals("None") ? View.GONE : View.VISIBLE);
            });

            itemBinding.tilIdNumber.setVisibility(visitor.getIdProof().equals("None") ? View.GONE : View.VISIBLE);
            itemBinding.etIdNumber.setText(visitor.getIdNumber());
            
            if ("Female".equals(visitor.getGender())) itemBinding.toggleGender.check(R.id.btnFemale);
            else itemBinding.toggleGender.check(R.id.btnMale);

            final int index = i;
            itemBinding.btnDeleteVisitor.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
            itemBinding.btnDeleteVisitor.setOnClickListener(v -> viewModel.removeVisitor(index));

            binding.visitorCardsContainer.addView(itemBinding.getRoot());
        }
    }

    private boolean validateAllVisitors() {
        List<Visitor> visitors = viewModel.getVisitors().getValue();
        if (visitors == null) return false;
        
        for (int i = 0; i < binding.visitorCardsContainer.getChildCount(); i++) {
            View child = binding.visitorCardsContainer.getChildAt(i);
            ItemVisitorCardBinding itemBinding = ItemVisitorCardBinding.bind(child);

            String name = itemBinding.etFullName.getText() != null ? itemBinding.etFullName.getText().toString().trim() : "";
            if (name.isEmpty()) {
                itemBinding.etFullName.setError("Name required");
                return false;
            }

            Visitor v = visitors.get(i);
            v.setFullName(name);
            v.setAge(itemBinding.spinnerAge.getText().toString());
            v.setGender(itemBinding.toggleGender.getCheckedButtonId() == R.id.btnFemale ? "Female" : "Male");
            v.setIdProof(itemBinding.spinnerIdProof.getText().toString());
            v.setIdNumber(itemBinding.etIdNumber.getText() != null ? itemBinding.etIdNumber.getText().toString() : "");
        }
        return true;
    }

    private void populatePaymentSummary() {
        binding.summaryTempleName.setText(templeName);
        List<Visitor> visitors = viewModel.getVisitors().getValue();
        int count = visitors != null ? visitors.size() : 0;
        
        binding.sumDarshanType.setText(viewModel.getSelectedDarshanType());
        binding.sumDateTime.setText(binding.chipDate.getText().toString());
        binding.summaryDevotees.setText(count + (count > 1 ? " Adults" : " Adult"));

        int unitPrice = calculateUnitPrice();
        int darshanTotal = unitPrice * count;
        int total = darshanTotal + 10; 

        binding.tvDarshanAmountLabel.setText("Darshan Amount (" + count + " × ₹" + unitPrice + ")");
        binding.summaryDarshanTotal.setText("₹" + darshanTotal);
        binding.sumTotalAmount.setText("₹" + total);
        binding.continueButton.setText("Proceed to Pay ₹" + total);
    }

    private int calculateUnitPrice() {
        if (binding.rbSarva.isChecked()) return 100;
        return 300;
    }

    private int calculateTotalAmount() {
        return (calculateUnitPrice() * viewModel.getTotalDevotees()) + 10;
    }

    private void startRazorpayPayment(PaymentOrderResponse paymentOrder) {
        BigDecimal total = paymentOrder.getTotalAmount() != null ? paymentOrder.getTotalAmount() : paymentOrder.getAmount();
        String amountInPaise = String.valueOf(total.multiply(new BigDecimal(100)).intValue());
        
        com.easydarshan.data.session.SessionManager session = com.easydarshan.data.session.SessionManager.getInstance(this);
        String userName = "Mahesh";
        String contact = "";
        
        if (session.getCurrentUser() != null) {
            if (session.getCurrentUser().getName() != null) userName = session.getCurrentUser().getName();
            if (session.getCurrentUser().getPhone() != null) contact = session.getCurrentUser().getPhone();
        }

        paymentHelper.startPayment(
                paymentOrder.getRazorpayKeyId(),
                paymentOrder.getGatewayOrderId(), 
                amountInPaise, 
                userName, 
                "Darshan Booking - " + templeName,
                contact
        );
    }

    private void verifyPayment(String razorpayPaymentId, String razorpaySignature) {
        String orderRef = viewModel.getOrderReference();
        if (orderRef == null) { viewModel.setCurrentStep(5); return; }
        repository.verifyPayment(new PaymentVerificationRequest(orderRef, razorpayPaymentId, razorpaySignature), new Callback<PaymentVerificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<PaymentVerificationResponse> call, @NonNull Response<PaymentVerificationResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) viewModel.confirmBooking();
                else viewModel.setCurrentStep(5);
            }
            @Override
            public void onFailure(@NonNull Call<PaymentVerificationResponse> call, @NonNull Throwable t) { viewModel.setCurrentStep(5); }
        });
    }

    private void navigateToLiveQueue() {
        startActivity(new Intent(this, LiveQueueActivity.class));
        finish();
    }

    @Override public void onPaymentSuccess(String pId) { verifyPayment(pId, "dev_signature"); }
    @Override public void onPaymentError(int c, String r) { viewModel.setCurrentStep(5); }
    @Override protected int getNavigationMenuItemId() { return R.id.nav_home; }
}
