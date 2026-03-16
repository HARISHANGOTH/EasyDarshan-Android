package com.easydarshan.ui.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.easydarshan.R;
import com.easydarshan.data.session.SessionManager;
import com.easydarshan.ui.login.MobileLoginActivity;

public class SplashActivity extends AppCompatActivity {
    
    private SplashViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Initialize SessionManager with context
        SessionManager.getInstance(this);
        
        viewModel = new ViewModelProvider(this, 
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(SplashViewModel.class);
        
        viewModel.getNavigateToLogin().observe(this, shouldNavigate -> {
            if (shouldNavigate) {
                startActivity(new Intent(this, MobileLoginActivity.class));
                finish();
            }
        });
        
        viewModel.getNavigateToHome().observe(this, shouldNavigate -> {
            if (shouldNavigate) {
                startActivity(new Intent(this, com.easydarshan.ui.home.HomeActivity.class));
                finish();
            }
        });
        
        viewModel.startSplash();
    }
}

