package com.easydarshan.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
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
    
    private final AppRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Temple>> temples = new MutableLiveData<>(new ArrayList<>());
    private final Debouncer searchDebouncer = new Debouncer(500);
    private String lastSearch = "";
    
    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
    
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<Temple>> getTemples() { return temples; }
    
    public void loadTemples(String search) {
        String query = search != null ? search.trim() : "";
        if (query.equals(lastSearch) && !temples.getValue().isEmpty()) {
            return;
        }
        lastSearch = query;

        searchDebouncer.debounce(() -> {
            if (!NetworkUtils.isNetworkAvailable(getApplication())) {
                errorMessage.postValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
                return;
            }

            isLoading.postValue(true);
            repository.getTemples(query.isEmpty() ? null : query, new Callback<TempleListResponse>() {
                @Override
                public void onResponse(@NonNull Call<TempleListResponse> call, @NonNull Response<TempleListResponse> response) {
                    isLoading.postValue(false);
                    if (response.isSuccessful() && response.body() != null) {
                        List<Temple> list = response.body().getTemples();
                        temples.postValue(list != null ? list : new ArrayList<>());
                    } else {
                        errorMessage.postValue(ErrorHandler.getErrorMessage(response.code(), null));
                    }
                }
                
                @Override
                public void onFailure(@NonNull Call<TempleListResponse> call, @NonNull Throwable t) {
                    isLoading.postValue(false);
                    errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
                }
            });
        });
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        searchDebouncer.cancel();
    }
}
