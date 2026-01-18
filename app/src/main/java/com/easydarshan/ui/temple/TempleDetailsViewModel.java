package com.easydarshan.ui.temple;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.DarshanType;
import com.easydarshan.data.model.Temple;
import com.easydarshan.data.repository.AppRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TempleDetailsViewModel extends ViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Temple> temple = new MutableLiveData<>();
    private MutableLiveData<List<DarshanType>> darshanTypes = new MutableLiveData<>();
    private MutableLiveData<String> navigateToPreBooking = new MutableLiveData<>();
    private MutableLiveData<String> navigateToLiveQueue = new MutableLiveData<>();
    
    public TempleDetailsViewModel() {
        repository = AppRepository.getInstance();
        loadDarshanTypes();
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
    
    public void loadTempleDetails(int templeId) {
        isLoading.setValue(true);
        repository.getTempleDetails(templeId, new Callback<ApiResponse<Temple>>() {
            @Override
            public void onResponse(Call<ApiResponse<Temple>> call, Response<ApiResponse<Temple>> response) {
                isLoading.postValue(false);
                if (response.body() != null && response.body().isSuccess()) {
                    temple.postValue(response.body().getData());
                } else {
                    errorMessage.postValue("Failed to load temple details");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Temple>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error. Please check your connection.");
            }
        });
    }
    
    private void loadDarshanTypes() {
        List<DarshanType> types = new ArrayList<>();
        types.add(new DarshanType("Free Darshan", "Free", "30-45 mins"));
        types.add(new DarshanType("Special Darshan", "₹300", "15-20 mins"));
        types.add(new DarshanType("Senior Citizen", "Free", "20-30 mins"));
        darshanTypes.setValue(types);
    }
    
    public void onPreBookClick() {
        navigateToPreBooking.setValue("pre_booking");
    }
    
    public void onJoinQueueClick() {
        navigateToLiveQueue.setValue("live_queue");
    }
}

