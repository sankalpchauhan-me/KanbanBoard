package me.sankalpchauhan.kanbanboard.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthCredential;

import me.sankalpchauhan.kanbanboard.model.User;
import me.sankalpchauhan.kanbanboard.repository.LoginRepository;

public class SignUpViewModel extends AndroidViewModel {
    private LoginRepository authRepository;
    public LiveData<User> authenticatedUserLiveData;
    public LiveData<User> createdUserLiveData;

    public SignUpViewModel(Application application) {
        super(application);
        authRepository = new LoginRepository();
    }

    public void signUpWithEmail(Context context, String email, String password, String name) {
        authenticatedUserLiveData = authRepository.firebaseCreateWithEmail(context, email, password, name);
    }

    public void createUser(User authenticatedUser) {
        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser);
    }
}
