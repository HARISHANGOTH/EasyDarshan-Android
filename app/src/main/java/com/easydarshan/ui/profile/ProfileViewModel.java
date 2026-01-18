package com.easydarshan.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.User;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.data.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<User> user = new MutableLiveData<>();
    
    public ProfileViewModel() {
        repository = AppRepository.getInstance();
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
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            user.setValue(currentUser);
            return;
        }

        isLoading.setValue(true);
        repository.getUserProfile(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                isLoading.postValue(false);
                if (response.body() != null && response.body().isSuccess()) {
                    SessionManager.getInstance().setCurrentUser(response.body().getData());
                    user.postValue(response.body().getData());
                } else {
                    errorMessage.postValue("Failed to load profile");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error. Please check your connection.");
            }
        });
    }

    public void updateUserProfile(User updatedUser) {
        isLoading.setValue(true);
        repository.updateUserProfile(updatedUser, new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                isLoading.postValue(false);
                if (response.body() != null && response.body().isSuccess()) {
                    SessionManager.getInstance().setCurrentUser(response.body().getData());
                    user.postValue(response.body().getData());
                } else {
                    errorMessage.postValue("Failed to update profile");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error. Please check your connection.");
            }
        });
    }
}
