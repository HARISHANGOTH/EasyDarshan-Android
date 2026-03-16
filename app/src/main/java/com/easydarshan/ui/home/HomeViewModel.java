package com.easydarshan.ui.home;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.Temple;
import com.easydarshan.data.model.TempleListResponse;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.utils.Debouncer;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {
    
    private AppRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Temple>> temples = new MutableLiveData<>();
    private MutableLiveData<Temple> selectedTemple = new MutableLiveData<>();
    private Debouncer searchDebouncer = new Debouncer(500); // 500ms debounce
    
    public HomeViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
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
        // Check network connectivity
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }
        
        // Debounce search to avoid too many API calls
        searchDebouncer.debounce(() -> {
            isLoading.setValue(true);
            errorMessage.setValue(null);
            
            String searchQuery = search != null ? search.trim() : "";
            repository.getTemples(searchQuery.isEmpty() ? null : searchQuery, new Callback<TempleListResponse>() {
                @Override
                public void onResponse(Call<TempleListResponse> call, Response<TempleListResponse> response) {
                    isLoading.postValue(false);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        List<Temple> templeList = response.body().getTemples();
                        if (templeList != null && !templeList.isEmpty()) {
                            temples.postValue(templeList);
                        } else {
                            temples.postValue(new ArrayList<>()); // Empty list
                        }
                    } else {
                        String errorMsg = ErrorHandler.getErrorMessage(response.code(), null);
                        errorMessage.postValue(errorMsg);
                        temples.postValue(new ArrayList<>());
                    }
                }
                
                @Override
                public void onFailure(Call<TempleListResponse> call, Throwable t) {
                    isLoading.postValue(false);
                    String errorMsg = ErrorHandler.getErrorMessage(t, getApplication());
                    errorMessage.postValue(errorMsg);
                    temples.postValue(new ArrayList<>());
                }
            });
        });
    }
    
    public void onTempleSelected(Temple temple) {
        selectedTemple.setValue(temple);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        searchDebouncer.cancel();
    }
}
