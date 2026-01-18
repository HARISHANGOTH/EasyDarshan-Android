package com.easydarshan.ui.temple;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityTempleDetailsBinding;
import com.easydarshan.ui.prebooking.PreBookingFlowActivity;

public class TempleDetailsActivity extends AppCompatActivity {
    
    private ActivityTempleDetailsBinding binding;
    private TempleDetailsViewModel viewModel;
    private int templeId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTempleDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        templeId = getIntent().getIntExtra("temple_id", 1);
        
        viewModel = new ViewModelProvider(this).get(TempleDetailsViewModel.class);
        
        setupObservers();
        setupListeners();
        
        viewModel.loadTempleDetails(templeId);
    }
    
    private void setupObservers() {
        viewModel.getTemple().observe(this, temple -> {
            if (temple != null) {
                binding.templeName.setText(temple.getName());
                binding.templeLocation.setText(temple.getLocation());
                binding.templeDistance.setText(temple.getDistance());
                binding.templeDescription.setText(temple.getDescription());
            }
        });
        
        viewModel.getDarshanTypes().observe(this, types -> {
            // Setup RecyclerView for darshan types if needed
        });
        
        viewModel.getNavigateToPreBooking().observe(this, navigate -> {
            if (navigate != null) {
                Intent intent = new Intent(this, PreBookingFlowActivity.class);
                intent.putExtra("temple_id", templeId);
                startActivity(intent);
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
        
        binding.preBookButton.setOnClickListener(v -> viewModel.onPreBookClick());
        binding.joinQueueButton.setOnClickListener(v -> viewModel.onJoinQueueClick());
    }
}

