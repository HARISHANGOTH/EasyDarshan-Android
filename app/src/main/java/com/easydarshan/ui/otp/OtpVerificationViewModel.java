package com.easydarshan.ui.otp;

import android.app.Application;
import android.os.CountDownTimer;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.OtpRequest;
import com.easydarshan.data.model.OtpVerifyRequest;
import com.easydarshan.data.model.User;
import com.easydarshan.data.model.VerifyOtpResponse;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.data.session.SessionManager;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;
import com.easydarshan.utils.ValidationUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationViewModel extends AndroidViewModel {
    
    private AppRepository repository;
    private SessionManager sessionManager;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<User> navigateToHome = new MutableLiveData<>();
    private MutableLiveData<Integer> timer = new MutableLiveData<>();
    
    private String mobile;
    private CountDownTimer countDownTimer;
    
    public OtpVerificationViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
        sessionManager = SessionManager.getInstance(application);
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
        // Validate OTP
        String validationError = ValidationUtils.getOtpValidationError(otp);
        if (validationError != null) {
            errorMessage.setValue(validationError);
            return;
        }
        
        // Check network connectivity
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }
        
        if (mobile == null || mobile.isEmpty()) {
            errorMessage.setValue("Mobile number is required");
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        OtpVerifyRequest request = new OtpVerifyRequest(mobile, otp);
        
        repository.verifyOtp(request, new Callback<VerifyOtpResponse>() {
            @Override
            public void onResponse(Call<VerifyOtpResponse> call, Response<VerifyOtpResponse> response) {
                isLoading.postValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    VerifyOtpResponse otpResponse = response.body();
                    if (otpResponse.isSuccess() && otpResponse.isVerified()) {
                        // Store tokens from OTP verification response
                        String accessToken = otpResponse.getAccessToken();
                        String refreshToken = otpResponse.getRefreshToken();
                        String userId = otpResponse.getUserId();
                        
                        if (accessToken != null && !accessToken.isEmpty()) {
                            sessionManager.setToken(accessToken);
                        }
                        if (refreshToken != null && !refreshToken.isEmpty()) {
                            sessionManager.setRefreshToken(refreshToken);
                        }
                        
                        // Create a User object for navigation
                        User user = new User();
                        user.setPhone(mobile);
                        if (userId != null) {
                            user.setId(userId);
                        }
                        sessionManager.setCurrentUser(user);
                        navigateToHome.postValue(user);
                    } else {
                        String errorMsg = otpResponse.getMessage();
                        errorMessage.postValue(errorMsg != null ? errorMsg : "Invalid OTP. Please try again.");
                    }
                } else {
                    String errorMsg = ErrorHandler.getErrorMessage(response.code(), null);
                    errorMessage.postValue(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                isLoading.postValue(false);
                String errorMsg = ErrorHandler.getErrorMessage(t, getApplication());
                errorMessage.postValue(errorMsg);
            }
        });
    }
    
    public void resendOtp() {
        if (mobile == null || mobile.isEmpty()) {
            errorMessage.setValue("Mobile number is required");
            return;
        }
        
        // Check network connectivity
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }
        
        // Resend OTP
        isLoading.setValue(true);
        OtpRequest request = new OtpRequest(mobile);
        
        repository.sendOtp(request, new Callback<com.easydarshan.data.model.ApiResponse<String>>() {
            @Override
            public void onResponse(Call<com.easydarshan.data.model.ApiResponse<String>> call, 
                                 Response<com.easydarshan.data.model.ApiResponse<String>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    startTimer();
                } else {
                    String errorMsg = ErrorHandler.getErrorMessage(response.code(), null);
                    errorMessage.postValue(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<com.easydarshan.data.model.ApiResponse<String>> call, Throwable t) {
                isLoading.postValue(false);
                String errorMsg = ErrorHandler.getErrorMessage(t, getApplication());
                errorMessage.postValue(errorMsg);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
