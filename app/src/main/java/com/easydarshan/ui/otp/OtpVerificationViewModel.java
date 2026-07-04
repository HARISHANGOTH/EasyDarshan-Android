package com.easydarshan.ui.otp;

import android.app.Application;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.DeviceRegistrationRequest;
import com.easydarshan.data.model.OtpReponse;
import com.easydarshan.data.model.OtpRequest;
import com.easydarshan.data.model.OtpVerifyRequest;
import com.easydarshan.data.model.User;
import com.easydarshan.data.model.VerifyOtpResponse;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.data.session.SessionManager;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;
import com.easydarshan.utils.ValidationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationViewModel extends AndroidViewModel {
    
    private final AppRepository repository;
    private final SessionManager sessionManager;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigateToHome = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> timer = new MutableLiveData<>(0);
    
    private String mobile;
    private CountDownTimer countDownTimer;
    
    public OtpVerificationViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
        sessionManager = SessionManager.getInstance(application);
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getNavigateToHome() { return navigateToHome; }
    public LiveData<Integer> getTimer() { return timer; }
    
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
        String validationError = ValidationUtils.getOtpValidationError(otp);
        if (validationError != null) {
            errorMessage.setValue(validationError);
            return;
        }
        
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
        
        OtpVerifyRequest request = new OtpVerifyRequest();
        request.setPhoneNumber(mobile);
        request.setOtp(otp);
        request.setCountryCode("+91");
        
        repository.verifyOtp(request, new Callback<VerifyOtpResponse>() {
            @Override
            public void onResponse(@NonNull Call<VerifyOtpResponse> call, @NonNull Response<VerifyOtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VerifyOtpResponse otpResponse = response.body();
                    if (otpResponse.isSuccess() && otpResponse.isVerified()) {
                        saveSessionAndRegisterDevice(otpResponse);
                    } else {
                        isLoading.postValue(false);
                        errorMessage.postValue(otpResponse.getMessage() != null ? otpResponse.getMessage() : "Invalid OTP");
                    }
                } else {
                    isLoading.postValue(false);
                    errorMessage.postValue(ErrorHandler.getErrorMessage(response.code(), null));
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<VerifyOtpResponse> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
            }
        });
    }

    private void saveSessionAndRegisterDevice(VerifyOtpResponse response) {
        if (response.getAccessToken() != null) {
            sessionManager.setToken(response.getAccessToken());
        }
        if (response.getRefreshToken() != null) {
            sessionManager.setRefreshToken(response.getRefreshToken());
        }
        
        User user = new User();
        user.setPhone(mobile);
        user.setId(response.getUserId());
        sessionManager.setCurrentUser(user);

        // Register FCM Token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                registerToken(task.getResult());
            } else {
                isLoading.postValue(false);
                navigateToHome.postValue(true);
            }
        });
    }

    private void registerToken(String token) {
        repository.registerDevice(new DeviceRegistrationRequest(token, "ANDROID"), new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                isLoading.postValue(false);
                navigateToHome.postValue(true);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                navigateToHome.postValue(true); // Navigate anyway
            }
        });
    }
    
    public void resendOtp() {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }
        
        isLoading.setValue(true);
        OtpRequest request = new OtpRequest();
        request.setPhoneNumber(mobile);
        
        repository.sendOtp(request, new Callback<OtpReponse>() {
            @Override
            public void onResponse(@NonNull Call<OtpReponse> call, @NonNull Response<OtpReponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    startTimer();
                } else {
                    errorMessage.postValue("Failed to resend OTP");
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<OtpReponse> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
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

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }
}
