package com.easydarshan.ui.login;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.AppInfo;
import com.easydarshan.data.model.DeviceInfo;
import com.easydarshan.data.model.OtpReponse;
import com.easydarshan.data.model.OtpRequest;
import com.easydarshan.data.model.UserContext;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;
import com.easydarshan.utils.ValidationUtils;

import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileLoginViewModel extends AndroidViewModel {
    
    private final AppRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> navigateToOtp = new MutableLiveData<>();
    
    public MobileLoginViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
    
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getNavigateToOtp() { return navigateToOtp; }
    
    public void sendOtp(String mobile) {
        if (mobile == null || mobile.isEmpty()) {
            errorMessage.setValue("Please enter mobile number");
            return;
        }

        String validationError = ValidationUtils.getPhoneValidationError(mobile);
        if (validationError != null) {
            errorMessage.setValue(validationError);
            return;
        }
        
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }
        
        String cleanedPhone = ValidationUtils.cleanPhoneNumber(mobile);
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.sendOtp(createOtpRequest(cleanedPhone), new Callback<OtpReponse>() {
            @Override
            public void onResponse(@NonNull Call<OtpReponse> call, @NonNull Response<OtpReponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    navigateToOtp.postValue(cleanedPhone);
                } else {
                    errorMessage.postValue(ErrorHandler.getErrorMessage(response.code(), null));
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<OtpReponse> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
            }
        });
    }

    private OtpRequest createOtpRequest(String mobile) {
        OtpRequest request = new OtpRequest();
        request.setPhoneNumber(mobile);
        request.setCountryCode("+91");

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceModel(Build.MODEL);
        deviceInfo.setManufacturer(Build.MANUFACTURER);
        deviceInfo.setOsVersion(Build.VERSION.RELEASE);
        deviceInfo.setDeviceType("ANDROID");
        // Device ID and Push Token should ideally come from a helper/provider
        deviceInfo.setDeviceId("android_" + Build.ID); 
        deviceInfo.setPushToken(""); 
        request.setDeviceInfo(deviceInfo);

        AppInfo appInfo = new AppInfo();
        appInfo.setAppVersion("1.1.1");
        appInfo.setBuildNumber("1");
        request.setAppInfo(appInfo);

        UserContext userContext = new UserContext();
        userContext.setLanguage("en");
        userContext.setTimezone(TimeZone.getDefault().getID());
        request.setUserContext(userContext);

        return request;
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    public void onNavigationComplete() {
        navigateToOtp.setValue(null);
    }
}
