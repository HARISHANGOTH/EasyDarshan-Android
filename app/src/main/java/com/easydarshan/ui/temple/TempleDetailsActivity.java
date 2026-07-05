package com.easydarshan.ui.temple;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import java.util.List;

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
        
        templeId = getIntent().getLongExtra("temple_id", 1L);
        
        viewModel = new ViewModelProvider(this).get(TempleDetailsViewModel.class);
        
        setupObservers();
        setupListeners();
        
        viewModel.loadTempleDetails(templeId);
    }
    
    private void setupObservers() {
        viewModel.getTemple().observe(this, temple -> {
            if (temple != null) {
                this.templeName = temple.getName();
                binding.tvTempleTitle.setText(temple.getName());
                binding.tvTempleLocation.setText(temple.getLocation());
                
                if (temple.getDistance() != null) {
                    binding.tvDistanceHero.setText(temple.getDistance());
                    binding.tvDistanceText.setText(temple.getDistance());
                }

                if (temple.getImage() != null && !temple.getImage().isEmpty()) {
                    Glide.with(this)
                            .load(temple.getImage())
                            .placeholder(R.drawable.ic_temple_placeholder)
                            .into(binding.ivTempleHero);
                }

                String opening = temple.getOpeningTime();
                String closing = temple.getClosingTime();

                if (opening != null && !opening.isEmpty() && closing != null && !closing.isEmpty()) {
                    String hours = String.format("%s - %s", opening, closing);
                    binding.tvHoursVal.setText(hours);
                    binding.tvOpenSubtitle.setText("Closes at " + closing);
                } else if (opening != null && !opening.isEmpty()) {
                    binding.tvHoursVal.setText(opening);
                } else if (closing != null && !closing.isEmpty()) {
                    binding.tvHoursVal.setText(closing);
                    binding.tvOpenSubtitle.setText("Closes at " + closing);
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
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnJoinLiveQueue.setOnClickListener(v -> startPreBooking());
        binding.btnFavorite.setOnClickListener(v -> Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show());
        binding.btnShare.setOnClickListener(v -> Toast.makeText(this, "Sharing temple details", Toast.LENGTH_SHORT).show());
        
        binding.cardOpenStatus.setOnClickListener(v -> Toast.makeText(this, "Opening hours details", Toast.LENGTH_SHORT).show());
        binding.btnOpeningHours.setOnClickListener(v -> Toast.makeText(this, "Detailed opening hours", Toast.LENGTH_SHORT).show());
        binding.btnAboutTemple.setOnClickListener(v -> Toast.makeText(this, "About temple info", Toast.LENGTH_SHORT).show());
        binding.btnGuidelines.setOnClickListener(v -> Toast.makeText(this, "Temple guidelines", Toast.LENGTH_SHORT).show());
    }

    private void startPreBooking() {
        Intent intent = new Intent(this, PreBookingFlowActivity.class);
        intent.putExtra("temple_id", templeId);
        intent.putExtra("temple_name", templeName);
        startActivity(intent);
    }
}
