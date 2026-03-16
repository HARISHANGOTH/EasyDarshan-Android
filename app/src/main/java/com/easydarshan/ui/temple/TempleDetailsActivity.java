package com.easydarshan.ui.temple;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.easydarshan.R;
import com.easydarshan.databinding.ActivityTempleDetailsBinding;
import com.easydarshan.databinding.DialogBookingOptionsBinding;
import com.easydarshan.data.model.DarshanType;
import com.easydarshan.ui.prebooking.PreBookingFlowActivity;

public class TempleDetailsActivity extends AppCompatActivity {
    
    private ActivityTempleDetailsBinding binding;
    private TempleDetailsViewModel viewModel;
    private Long templeId;
    private String templeName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTempleDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        if (getIntent().hasExtra("temple_id")) {
            try {
                templeId = getIntent().getLongExtra("temple_id", 1L);
            } catch (Exception e) {
                int templeIdInt = getIntent().getIntExtra("temple_id", 1);
                templeId = (long) templeIdInt;
            }
        } else {
            templeId = 1L;
        }
        
        viewModel = new ViewModelProvider(this, 
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(TempleDetailsViewModel.class);
        
        setupObservers();
        setupListeners();
        
        viewModel.loadTempleDetails(templeId);
    }
    
    private void setupObservers() {
        viewModel.getTemple().observe(this, temple -> {
            if (temple != null) {
                this.templeName = temple.getName();
                binding.templeName.setText(temple.getName());
                binding.templeLocation.setText(temple.getLocation());
                if (temple.getDistance() != null) {
                    binding.templeDistance.setText(temple.getDistance());
                }
                if (temple.getDescription() != null) {
                    binding.templeDescription.setText(temple.getDescription());
                }

                if (temple.getImage() != null && !temple.getImage().isEmpty()) {
                    Glide.with(this)
                            .load(temple.getImage())
                            .placeholder(R.drawable.ic_temple_placeholder)
                            .into(binding.templeHeaderImage);
                }

                String opening = temple.getOpeningTime();
                String closing = temple.getClosingTime();

                if (opening != null && !opening.isEmpty() && closing != null && !closing.isEmpty()) {
                    binding.templeOpeningTime.setText(opening + " - " + closing);
                } else if (opening != null && !opening.isEmpty()) {
                    binding.templeOpeningTime.setText(opening);
                } else if (closing != null && !closing.isEmpty()) {
                    binding.templeOpeningTime.setText(closing);
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
        binding.backButton.setOnClickListener(v -> finish());
        binding.bookButton.setOnClickListener(v -> showBookingOptionsDialog());
    }
    
    private void showBookingOptionsDialog() {
        DialogBookingOptionsBinding dialogBinding = DialogBookingOptionsBinding.inflate(LayoutInflater.from(this));
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setCancelable(true)
                .create();
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        java.util.List<DarshanType> darshanTypes = viewModel.getDarshanTypes().getValue();
        
        if (darshanTypes != null && !darshanTypes.isEmpty()) {
            for (DarshanType type : darshanTypes) {
                String name = type.getName().toLowerCase();
                if (name.contains("vip") || name.contains("paid")) {
                    if (dialogBinding.livePaidPrice != null) {
                        dialogBinding.livePaidPrice.setText(type.getPrice());
                    }
                }
            }
        }
        
        dialogBinding.liveFreeCard.setOnClickListener(v -> {
            dialog.dismiss();
            startPreBooking("Free Live Queue", getDarshanTypeId(darshanTypes, "free"));
        });
        
        dialogBinding.livePaidCard.setOnClickListener(v -> {
            dialog.dismiss();
            startPreBooking("Paid Live Queue", getDarshanTypeId(darshanTypes, "vip"));
        });
        
        dialog.show();
    }
    
    private Long getDarshanTypeId(java.util.List<DarshanType> types, String searchTerm) {
        if (types == null) return 1L; 
        for (DarshanType type : types) {
            if (type.getName().toLowerCase().contains(searchTerm)) {
                return type.getId();
            }
        }
        return 1L;
    }
    
    private void startPreBooking(String darshanTypeName, Long darshanTypeId) {
        Intent intent = new Intent(this, PreBookingFlowActivity.class);
        intent.putExtra("temple_id", templeId);
        intent.putExtra("temple_name", templeName);
        intent.putExtra("darshan_type_id", darshanTypeId);
        intent.putExtra("darshan_type", darshanTypeName);
        startActivity(intent);
    }
}
