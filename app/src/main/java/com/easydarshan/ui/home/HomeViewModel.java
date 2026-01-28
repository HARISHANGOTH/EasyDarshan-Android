package com.easydarshan.ui.home;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easydarshan.data.model.Temple;
import com.easydarshan.data.model.TempleListResponse;
import com.easydarshan.data.repository.AppRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Temple>> temples = new MutableLiveData<>();
    private MutableLiveData<Temple> selectedTemple = new MutableLiveData<>();
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    
    public HomeViewModel() {
        repository = AppRepository.getInstance();
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<List<Temple>> getTemples() {
        return temples;
    }
    
    public LiveData<Temple> getSelectedTemple() {
        return selectedTemple;
    }
    
    public void loadTemples(String search) {
        isLoading.setValue(true);
        searchHandler.removeCallbacks(searchRunnable);
        searchRunnable = () -> {
            repository.getTemples(search, new Callback<TempleListResponse>() {
                @Override
                public void onResponse(Call<TempleListResponse> call, Response<TempleListResponse> response) {
                    isLoading.postValue(false);
                    if (response.body() != null && response.body().getTemples() != null) {
                        temples.postValue(response.body().getTemples());
                    } else {
                        errorMessage.postValue("Failed to load temples");
                    }
                }
                
                @Override
                public void onFailure(Call<TempleListResponse> call, Throwable t) {
                    isLoading.postValue(false);
                    errorMessage.postValue("Network error. Please check your connection.");
                }
            });
        };
        searchHandler.postDelayed(searchRunnable, 300);
    }
    
    public void onTempleSelected(Temple temple) {
        selectedTemple.setValue(temple);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        searchHandler.removeCallbacks(searchRunnable);
    }
}
