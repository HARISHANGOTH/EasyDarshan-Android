package com.easydarshan.ui.login;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.AppInfo;
import com.easydarshan.data.model.DeviceInfo;
import com.easydarshan.data.model.OtpReponse;
import com.easydarshan.data.model.OtpRequest;
import com.easydarshan.data.model.UserContext;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;
import com.easydarshan.utils.ValidationUtils;

import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileLoginViewModel extends AndroidViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<String> navigateToOtp = new MutableLiveData<>();
    private AtomicInteger retryCount = new AtomicInteger(0);
    
    public MobileLoginViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
    
    public LiveData<String> getNavigateToOtp() {
        return navigateToOtp;
    }
    
    public void sendOtp(String mobile) {
        // Validate phone number
        String validationError = ValidationUtils.getPhoneValidationError(mobile);
        if (validationError != null) {
            errorMessage.setValue(validationError);
            return;
        }
        
        // Check network connectivity
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }
        
        // Clean phone number
        String cleanedPhone = ValidationUtils.cleanPhoneNumber(mobile);
        retryCount.set(0);
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setPhoneNumber(cleanedPhone);
        otpRequest.setCountryCode("+91");

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceModel("Vivo");
        deviceInfo.setDeviceId("a1b2c3d4e5");
        deviceInfo.setManufacturer("Vivo");
        deviceInfo.setOsVersion("14");
        deviceInfo.setPushToken("pushToken");
        deviceInfo.setDeviceType("ANDROID");

        otpRequest.setDeviceInfo(deviceInfo);

        AppInfo appInfo = new AppInfo();
        appInfo.setAppVersion("1.0.0");
        appInfo.setBuildNumber("100");

       otpRequest.setAppInfo(appInfo);
        UserContext userContext = new UserContext();
        userContext.setLanguage("Asia/kolkata");
        userContext.setTimezone("en");
        otpRequest.setUserContext(userContext);
        repository.sendOtp(otpRequest, new Callback<OtpReponse>() {
            @Override
            public void onResponse(Call<OtpReponse> call, Response<OtpReponse> response) {
                isLoading.postValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    successMessage.postValue(response.body().getMessage() != null ? 
                        response.body().getMessage() : "OTP sent successfully");
                    navigateToOtp.postValue(cleanedPhone);
                    retryCount.set(0);
                } else {
                    String errorMsg = ErrorHandler.getErrorMessage(response.code(), null);
                    errorMessage.postValue(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<OtpReponse> call, Throwable t) {
                isLoading.postValue(false);
                String errorMsg = ErrorHandler.getErrorMessage(t, getApplication());
                errorMessage.postValue(errorMsg);
            }
        });
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    public void onNavigationComplete() {
        navigateToOtp.setValue(null);
    }
}
