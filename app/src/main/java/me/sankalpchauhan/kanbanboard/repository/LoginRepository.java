package me.sankalpchauhan.kanbanboard.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import me.sankalpchauhan.kanbanboard.model.User;
import me.sankalpchauhan.kanbanboard.view.LoginActivity;
import me.sankalpchauhan.kanbanboard.view.SignUpActivity;

import static me.sankalpchauhan.kanbanboard.util.Constants.USERS;
import static me.sankalpchauhan.kanbanboard.util.HelperClass.hideProgressDialog;
import static me.sankalpchauhan.kanbanboard.util.HelperClass.logErrorMessage;
import static me.sankalpchauhan.kanbanboard.util.HelperClass.showProgressDialog;

public class LoginRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(USERS);

    public MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential googleAuthCredential) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    String photoUrl = String.valueOf(firebaseUser.getPhotoUrl());
                    User user;
                    if(photoUrl==null) {
                        user = new User(uid, name, email);
                    }
                    else{
                        user = new User(uid, name, email, photoUrl);
                    }
                    user.isNew = isNewUser;
                    authenticatedUserMutableLiveData.setValue(user);
                }
            } else {
                logErrorMessage(authTask.getException().getMessage());
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<User> firebaseCreateWithEmail(Context context, String email, String password, String userName) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> authTask) {
                ((SignUpActivity) context).setSignUpVisibility();
                if(authTask.isSuccessful()){
                    boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if(firebaseUser!=null){
                        String uid = firebaseUser.getUid();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName).build();
                        firebaseUser.updateProfile(profileUpdates);
                        String email = firebaseUser.getEmail();
                        String photoUrl = String.valueOf(firebaseUser.getPhotoUrl());
                        User user;
                        if(photoUrl==null) {
                            user = new User(uid, userName, email);
                        }
                        else{
                            user = new User(uid, userName, email, photoUrl);
                        }
                        user.isNew = isNewUser;
                        firebaseUser.sendEmailVerification();
                        authenticatedUserMutableLiveData.setValue(user);
                    }
                } else {
                    logErrorMessage(authTask.getException().getMessage());
                    Toast.makeText(context, authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<User> firebaseSignInWithEmail(Context context, String email, String password) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(email, password)
              .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> authTask) {
                      ((LoginActivity) context).setSignInVisible();
                      if(authTask.isSuccessful()){
                          boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                          FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                          if(firebaseUser!=null){
                              String uid = firebaseUser.getUid();
                              String name = firebaseUser.getDisplayName();
                              String email = firebaseUser.getEmail();
                              String photoUrl = String.valueOf(firebaseUser.getPhotoUrl());
                              User user;
                              if(photoUrl==null) {
                                  user = new User(uid, name, email);
                              }
                              else{
                                  user = new User(uid, name, email, photoUrl);
                              }
                              user.isNew = isNewUser;
                              authenticatedUserMutableLiveData.setValue(user);
                          }
                      } else {
                          logErrorMessage(authTask.getException().getMessage());
                          Toast.makeText(context, authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                      }
                  }
              });

        return authenticatedUserMutableLiveData;
    }

    public void sendPasswordReset(Context context, String email){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Password Reset Mail Sent", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public MutableLiveData<User> createUserInFirestoreIfNotExists(User authenticatedUser) {
        MutableLiveData<User> newUserMutableLiveData = new MutableLiveData<>();
        DocumentReference uidRef = usersRef.document(authenticatedUser.uid);
        uidRef.get().addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                DocumentSnapshot document = uidTask.getResult();
                if (!document.exists()) {
                    uidRef.set(authenticatedUser).addOnCompleteListener(userCreationTask -> {
                        if (userCreationTask.isSuccessful()) {
                            authenticatedUser.isCreated = true;
                            newUserMutableLiveData.setValue(authenticatedUser);
                        } else {
                            logErrorMessage(userCreationTask.getException().getMessage());
                        }
                    });
                } else {
                    newUserMutableLiveData.setValue(authenticatedUser);
                }
            } else {
                logErrorMessage(uidTask.getException().getMessage());
            }
        });
        return newUserMutableLiveData;
    }

}
