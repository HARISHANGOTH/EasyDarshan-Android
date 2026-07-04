package com.easydarshan.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
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
    
    private final AppRepository repository;
    private final SessionManager sessionManager;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> profileUpdated = new MutableLiveData<>(false);
    
    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
        sessionManager = SessionManager.getInstance(application);
    }
    
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<User> getUser() { return user; }
    public LiveData<Boolean> getProfileUpdated() { return profileUpdated; }
    
    public void loadUserProfile() {
        User current = sessionManager.getCurrentUser();
        if (current != null) {
            user.setValue(current);
        }

        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);
        repository.getUserProfile(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User userData = response.body().getData();
                    sessionManager.setCurrentUser(userData);
                    user.postValue(userData);
                } else {
                    errorMessage.postValue(ErrorHandler.getErrorMessage(response.code(), null));
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
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
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User userData = response.body().getData();
                    sessionManager.setCurrentUser(userData);
                    user.postValue(userData);
                    profileUpdated.postValue(true);
                } else {
                    errorMessage.postValue(ErrorHandler.getErrorMessage(response.code(), null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
            }
        });
    }

    public void logout() {
        sessionManager.clearSession();
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }
    
    public void onUpdateHandled() {
        profileUpdated.setValue(false);
    }
}
