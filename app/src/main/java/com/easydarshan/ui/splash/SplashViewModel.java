package com.easydarshan.ui.splash;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SplashViewModel extends ViewModel {
    
    private MutableLiveData<Boolean> navigateToLogin = new MutableLiveData<>();
    
    public LiveData<Boolean> getNavigateToLogin() {
        return navigateToLogin;
    }
    
    public void startSplash() {
        // Simulate splash screen delay
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                navigateToLogin.postValue(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

