package com.easydarshan.ui.splash;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.session.SessionManager;

public class SplashViewModel extends AndroidViewModel {
    
    private final MutableLiveData<Boolean> navigateToLogin = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigateToHome = new MutableLiveData<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    public SplashViewModel(@NonNull Application application) {
        super(application);
    }
    
    public LiveData<Boolean> getNavigateToLogin() { return navigateToLogin; }
    public LiveData<Boolean> getNavigateToHome() { return navigateToHome; }
    
    public void startSplash() {
        boolean isLoggedIn = SessionManager.getInstance(getApplication()).isLoggedIn();
        handler.postDelayed(() -> {
            if (isLoggedIn) navigateToHome.postValue(true);
            else navigateToLogin.postValue(true);
        }, 2000);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacksAndMessages(null);
    }
}
