package com.easydarshan.ui.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.easydarshan.R;
import com.easydarshan.ui.home.HomeActivity;
import com.easydarshan.ui.login.MobileLoginActivity;

@SuppressLint("CustomSplash")
public class SplashActivity extends AppCompatActivity {

    private SplashViewModel viewModel;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> viewModel.startSplash());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        viewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        viewModel.getNavigateToLogin().observe(this, shouldNavigate -> {
            if (shouldNavigate) {
                startActivity(new Intent(this, MobileLoginActivity.class));
                finish();
            }
        });

        viewModel.getNavigateToHome().observe(this, shouldNavigate -> {
            if (shouldNavigate) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        });

        requestNotificationPermissionThenStart();
    }

    private void requestNotificationPermissionThenStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }
        viewModel.startSplash();
    }
}
