package com.easydarshan.ui.temple;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.DarshanType;
import com.easydarshan.data.model.Temple;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TempleDetailsViewModel extends AndroidViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Temple> temple = new MutableLiveData<>();
    private MutableLiveData<List<DarshanType>> darshanTypes = new MutableLiveData<>();
    private MutableLiveData<String> navigateToPreBooking = new MutableLiveData<>();
    private MutableLiveData<String> navigateToLiveQueue = new MutableLiveData<>();
    
    public TempleDetailsViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Temple> getTemple() {
        return temple;
    }
    
    public LiveData<List<DarshanType>> getDarshanTypes() {
        return darshanTypes;
    }
    
    public LiveData<String> getNavigateToPreBooking() {
        return navigateToPreBooking;
    }
    
    public LiveData<String> getNavigateToLiveQueue() {
        return navigateToLiveQueue;
    }
    
    public void loadTempleDetails(Long templeId) {
        if (templeId == null) {
            errorMessage.setValue("Invalid temple ID");
            return;
        }
        
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        repository.getTempleDetails(templeId, new Callback<Temple>() {
            @Override
            public void onResponse(Call<Temple> call, Response<Temple> response) {
                isLoading.postValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Backend returns a plain Temple object (not wrapped in ApiResponse)
                    temple.postValue(response.body());
                    // Load darshan types for this temple
                    loadDarshanTypes(templeId);
                } else {
                    String errorMsg = ErrorHandler.getErrorMessage(response.code(), null);
                    errorMessage.postValue(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<Temple> call, Throwable t) {
                isLoading.postValue(false);
                String errorMsg = ErrorHandler.getErrorMessage(t, getApplication());
                errorMessage.postValue(errorMsg);
            }
        });
    }
    
    private void loadDarshanTypes(Long templeId) {
        repository.getTempleDarshans(templeId, new Callback<ApiResponse<List<DarshanType>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DarshanType>>> call, Response<ApiResponse<List<DarshanType>>> response) {
                if (response.body() != null && response.body().isSuccess()) {
                    darshanTypes.postValue(response.body().getData());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<DarshanType>>> call, Throwable t) {
                // Silently fail - darshan types are optional
            }
        });
    }
    
    public void onPreBookClick() {
        navigateToPreBooking.setValue("pre_booking");
    }
    
    public void onJoinQueueClick() {
        navigateToLiveQueue.setValue("live_queue");
    }
}

