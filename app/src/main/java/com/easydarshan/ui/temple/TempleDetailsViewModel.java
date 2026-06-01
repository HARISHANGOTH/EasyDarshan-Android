package com.easydarshan.ui.temple;

import android.app.Application;

import androidx.annotation.NonNull;
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
    
    private final AppRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Temple> temple = new MutableLiveData<>();
    private final MutableLiveData<List<DarshanType>> darshanTypes = new MutableLiveData<>(new ArrayList<>());
    
    public TempleDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
    
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Temple> getTemple() { return temple; }
    public LiveData<List<DarshanType>> getDarshanTypes() { return darshanTypes; }
    
    public void loadTempleDetails(Long templeId) {
        if (templeId == null) {
            errorMessage.setValue("Invalid temple ID");
            return;
        }

        // Load from cache first
        repository.getCachedTemple(templeId, cached -> {
            if (cached != null) {
                temple.postValue(cached);
            }
        });
        
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        repository.getTempleDetails(templeId, new Callback<Temple>() {
            @Override
            public void onResponse(@NonNull Call<Temple> call, @NonNull Response<Temple> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    temple.postValue(response.body());
                    loadDarshanTypes(templeId);
                } else if (temple.getValue() == null) {
                    errorMessage.postValue(ErrorHandler.getErrorMessage(response.code(), null));
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<Temple> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                if (temple.getValue() == null) {
                    errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
                }
            }
        });
    }
    
    private void loadDarshanTypes(Long templeId) {
        repository.getTempleDarshans(templeId, new Callback<ApiResponse<List<DarshanType>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<DarshanType>>> call, @NonNull Response<ApiResponse<List<DarshanType>>> response) {
                if (response.body() != null && response.body().isSuccess()) {
                    List<DarshanType> list = response.body().getData();
                    darshanTypes.postValue(list != null ? list : new ArrayList<>());
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<DarshanType>>> call, @NonNull Throwable t) {}
        });
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }
}
