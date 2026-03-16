package com.easydarshan.ui.splash;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.session.SessionManager;

public class SplashViewModel extends AndroidViewModel {
    
    private MutableLiveData<Boolean> navigateToLogin = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateToHome = new MutableLiveData<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    
    public SplashViewModel(Application application) {
        super(application);
    }
    
    public LiveData<Boolean> getNavigateToLogin() {
        return navigateToLogin;
    }
    
    public LiveData<Boolean> getNavigateToHome() {
        return navigateToHome;
    }
    
    public void startSplash() {
        // Check if user is already logged in
        SessionManager sessionManager = SessionManager.getInstance(getApplication());
        boolean isLoggedIn = sessionManager.isLoggedIn();
        
        handler.postDelayed(() -> {
            if (isLoggedIn) {
                navigateToHome.postValue(true);
            } else {
                navigateToLogin.postValue(true);
            }
        }, 2000);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacksAndMessages(null);
    }
}

