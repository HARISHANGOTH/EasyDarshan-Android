package com.easydarshan.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.OtpRequest;
import com.easydarshan.data.repository.AppRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileLoginViewModel extends ViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<String> navigateToOtp = new MutableLiveData<>();
    
    public MobileLoginViewModel() {
        repository = AppRepository.getInstance();
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
        if (mobile == null || mobile.length() != 10) {
            errorMessage.setValue("Please enter a valid 10-digit mobile number");
            return;
        }
        
        isLoading.setValue(true);
        OtpRequest request = new OtpRequest(mobile);
        
        repository.sendOtp(request, new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                isLoading.postValue(false);
                if (response.body() != null && response.body().isSuccess()) {
                    successMessage.postValue(response.body().getMessage());
                    navigateToOtp.postValue(mobile);
                } else {
                    errorMessage.postValue("Failed to send OTP. Please try again.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error. Please check your connection.");
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
