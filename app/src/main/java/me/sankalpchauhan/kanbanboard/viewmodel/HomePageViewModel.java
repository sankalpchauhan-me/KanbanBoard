package me.sankalpchauhan.kanbanboard.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import me.sankalpchauhan.kanbanboard.model.User;
import me.sankalpchauhan.kanbanboard.repository.HomePageRepository;

public class HomePageViewModel extends AndroidViewModel {
    private HomePageRepository homePageRepository;
    public LiveData<User> isUserAuthenticatedLiveData;
    public LiveData<User> userLiveData;

    public HomePageViewModel(Application application) {
        super(application);
        homePageRepository = new HomePageRepository();
    }

    public void checkIfUserIsAuthenticated() {
        isUserAuthenticatedLiveData = homePageRepository.checkIfUserIsAuthenticatedInFirebase();
    }

    public void setUid(String uid) {
        userLiveData = homePageRepository.addUserToLiveData(uid);
    }
}
