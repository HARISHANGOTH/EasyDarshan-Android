package com.easydarshan.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easydarshan.data.model.Temple;
import com.easydarshan.data.model.TempleListResponse;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.data.session.SessionManager;
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
    private final Debouncer searchDebouncer = new Debouncer(500);

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Temple>> temples = new MutableLiveData<>();
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showNotificationCard = new MutableLiveData<>(false);

    public HomeViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
        loadUserName();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<Temple>> getTemples() { return temples; }
    public LiveData<String> getUserName() { return userName; }
    public LiveData<Boolean> getShowNotificationCard() { return showNotificationCard; }

    private void loadUserName() {
        SessionManager session = SessionManager.getInstance();
        if (session != null && session.getCurrentUser() != null) {
            String name = session.getCurrentUser().getName();
            userName.setValue(name != null ? name : "Devotee");
        } else {
            userName.setValue("Devotee");
        }
    }

    public void loadTemples(String search) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(NetworkUtils.getNetworkErrorMessage(getApplication()));
            return;
        }

        String query = search != null ? search.trim() : "";
        
        // If query is empty, load immediately without debouncing for faster initial/view-all load
        if (query.isEmpty()) {
            searchDebouncer.cancel();
            executeTempleLoad("");
        } else {
            searchDebouncer.debounce(() -> executeTempleLoad(query));
        }
    }

    private void executeTempleLoad(String query) {
        isLoading.postValue(true);
        errorMessage.postValue(null);

        repository.getTemples(query.isEmpty() ? null : query, new Callback<TempleListResponse>() {
            @Override
            public void onResponse(Call<TempleListResponse> call, Response<TempleListResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Temple> list = response.body().getTemples();
                    temples.postValue(list != null ? list : new ArrayList<>());
                } else {
                    errorMessage.postValue(ErrorHandler.getErrorMessage(response.code(), null));
                    temples.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<TempleListResponse> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(ErrorHandler.getErrorMessage(t, getApplication()));
                temples.postValue(new ArrayList<>());
            }
        });
    }

    public void setShowNotificationCard(boolean show) {
        showNotificationCard.setValue(show);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        searchDebouncer.cancel();
    }
}
