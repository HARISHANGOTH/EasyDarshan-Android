package com.easydarshan.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityHomeBinding;
import com.easydarshan.ui.BaseActivity;
import com.easydarshan.ui.adapter.TempleAdapter;
import com.easydarshan.ui.notifications.NotificationsActivity;
import com.easydarshan.ui.temple.TempleDetailsActivity;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;
    private TempleAdapter adapter;

    private final ActivityResultLauncher<String> notifPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                viewModel.setShowNotificationCard(false);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(HomeViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupListeners();
        setupBottomNavigation();
        checkNotificationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadTemples("");
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_home;
    }

    private void setupRecyclerView() {
        adapter = new TempleAdapter(temple -> {
            Intent intent = new Intent(this, TempleDetailsActivity.class);
            intent.putExtra("temple_id", temple.getId());
            startActivity(intent);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvNearbyTemples.setLayoutManager(layoutManager);
        binding.rvNearbyTemples.setAdapter(adapter);
        binding.rvNearbyTemples.setHasFixedSize(false);
    }

    private void setupObservers() {
        viewModel.getUserName().observe(this, name ->
                binding.tvUserName.setText(name));

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.shimmerLayout.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                binding.rvNearbyTemples.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.GONE);
                binding.errorState.setVisibility(View.GONE);
            }
        });

        viewModel.getTemples().observe(this, temples -> {
            binding.shimmerLayout.getRoot().setVisibility(View.GONE);
            if (temples == null || temples.isEmpty()) {
                binding.rvNearbyTemples.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.errorState.setVisibility(View.GONE);
            } else {
                binding.rvNearbyTemples.setVisibility(View.VISIBLE);
                binding.emptyState.setVisibility(View.GONE);
                binding.errorState.setVisibility(View.GONE);
                adapter.submitList(temples);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                binding.shimmerLayout.getRoot().setVisibility(View.GONE);
                binding.rvNearbyTemples.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.GONE);
                binding.errorState.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getShowNotificationCard().observe(this, show ->
                binding.notificationCard.setVisibility(show ? View.VISIBLE : View.GONE));
    }

    private void setupListeners() {
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.loadTemples(s.toString());
            }
        });

        binding.btnNotification.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        binding.btnViewAll.setOnClickListener(v ->
                viewModel.loadTemples(""));

        binding.btnBookDarshanNow.setOnClickListener(v ->
                viewModel.loadTemples(""));

        binding.btnRetry.setOnClickListener(v ->
                viewModel.loadTemples(binding.searchInput.getText().toString()));

        binding.btnEnableNotifications.setOnClickListener(v ->
                requestNotificationPermission());

        binding.btnDismissNotif.setOnClickListener(v ->
                viewModel.setShowNotificationCard(false));
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean granted = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
            viewModel.setShowNotificationCard(!granted);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            viewModel.setShowNotificationCard(false);
        }
    }
}
