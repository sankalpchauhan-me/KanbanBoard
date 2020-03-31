package me.sankalpchauhan.kanbanboard.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.AuthCredential;

import me.sankalpchauhan.kanbanboard.model.User;
import me.sankalpchauhan.kanbanboard.repository.LoginRepository;

public class LoginViewModel extends AndroidViewModel {
    private LoginRepository authRepository;
    public LiveData<User> authenticatedUserLiveData;
    public LiveData<User> createdUserLiveData;

    public LoginViewModel(Application application) {
        super(application);
        authRepository = new LoginRepository();
    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authenticatedUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

    public void signInWithEmail(Context context, String email, String password){
        authenticatedUserLiveData = authRepository.firebaseSignInWithEmail(context, email, password);
    }

    public void createUser(User authenticatedUser) {
        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser);
    }

    public void sendPasswordReset(Context context, String email) {
        authRepository.sendPasswordReset(context,email);
    }
}
