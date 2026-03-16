package com.easydarshan.ui.profile;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.User;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.data.session.SessionManager;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {
    
    private AppRepository repository;
    private SessionManager sessionManager;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<User> user = new MutableLiveData<>();
    
    public ProfileViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
        sessionManager = SessionManager.getInstance(application);
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<User> getUser() {
        return user;
    }
    
    public void loadUserProfile() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            user.setValue(currentUser);
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);
        repository.getUserProfile(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                isLoading.postValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        User userData = response.body().getData();
                        sessionManager.setCurrentUser(userData);
                        user.postValue(userData);
                    } else {
                        String errorMsg = response.body().getMessage();
                        errorMessage.postValue(errorMsg != null ? errorMsg : "Failed to load profile");
                    }
                } else {
                    String errorMsg = ErrorHandler.getErrorMessage(response.code(), null);
                    errorMessage.postValue(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                isLoading.postValue(false);
                String errorMsg = ErrorHandler.getErrorMessage(t, getApplication());
                errorMessage.postValue(errorMsg);
            }
        });
    }

    public void updateUserProfile(User updatedUser) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        repository.updateUserProfile(updatedUser, new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                isLoading.postValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        User userData = response.body().getData();
                        sessionManager.setCurrentUser(userData);
                        user.postValue(userData);
                    } else {
                        String errorMsg = response.body().getMessage();
                        errorMessage.postValue(errorMsg != null ? errorMsg : "Failed to update profile");
                    }
                } else {
                    String errorMsg = ErrorHandler.getErrorMessage(response.code(), null);
                    errorMessage.postValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                isLoading.postValue(false);
                String errorMsg = ErrorHandler.getErrorMessage(t, getApplication());
                errorMessage.postValue(errorMsg);
            }
        });
    }
    
    public void logout() {
        sessionManager.clearSession();
    }
}
