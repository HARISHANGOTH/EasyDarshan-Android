package com.easydarshan.ui.otp;

import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.OtpVerifyRequest;
import com.easydarshan.data.model.User;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.data.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationViewModel extends ViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<User> navigateToHome = new MutableLiveData<>();
    private MutableLiveData<Integer> timer = new MutableLiveData<>();
    
    private String mobile;
    private CountDownTimer countDownTimer;
    
    public OtpVerificationViewModel() {
        repository = AppRepository.getInstance();
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<User> getNavigateToHome() {
        return navigateToHome;
    }
    
    public LiveData<Integer> getTimer() {
        return timer;
    }
    
    public void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        timer.setValue(30);
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.postValue((int) (millisUntilFinished / 1000));
            }
            
            @Override
            public void onFinish() {
                timer.postValue(0);
            }
        }.start();
    }
    
    public void verifyOtp(String otp) {
        if (otp == null || otp.length() != 6) {
            errorMessage.setValue("Please enter a valid 6-digit OTP");
            return;
        }
        
        isLoading.setValue(true);
        OtpVerifyRequest request = new OtpVerifyRequest(mobile, otp);
        
        repository.verifyOtp(request, new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                isLoading.postValue(false);
                if (response.body() != null && response.body().isSuccess()) {
                    SessionManager.getInstance().setCurrentUser(response.body().getData());
                    navigateToHome.postValue(response.body().getData());
                } else {
                    errorMessage.postValue("Invalid OTP. Please try again.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error. Please check your connection.");
            }
        });
    }
    
    public void resendOtp() {
        startTimer();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
