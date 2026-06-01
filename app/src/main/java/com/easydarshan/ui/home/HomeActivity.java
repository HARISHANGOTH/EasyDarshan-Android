package com.easydarshan.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityHomeBinding;
import com.easydarshan.ui.BaseActivity;
import com.easydarshan.ui.adapter.TempleAdapter;
import com.easydarshan.ui.temple.TempleDetailsActivity;

public class HomeActivity extends BaseActivity {
    
    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;
    private TempleAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        setupRecyclerView();
        setupObservers();
        setupListeners();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadTemples(binding.searchInput.getText().toString());
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_home;
    }
    
    private void setupRecyclerView() {
        adapter = new TempleAdapter(null, temple -> {
            Intent intent = new Intent(this, TempleDetailsActivity.class);
            intent.putExtra("temple_id", temple.getId());
            startActivity(intent);
        });
        
        binding.templesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.templesRecyclerView.setAdapter(adapter);
    }
    
    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getTemples().observe(this, temples -> {
            if (temples != null) {
                adapter.updateList(temples);
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
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.loadTemples(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
