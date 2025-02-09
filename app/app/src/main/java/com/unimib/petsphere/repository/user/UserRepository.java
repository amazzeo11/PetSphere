// Author: Sara Angeretti

package com.unimib.petsphere.repository.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.petsphere.model.Result;
import com.unimib.petsphere.model.User;
import com.unimib.petsphere.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.petsphere.source.user.BaseUserDataRemoteDataSource;
import com.unimib.petsphere.source.user.UserFirebaseDataSource;

import java.util.List;
import java.util.Set;


/**
 * Repository class che ottiene le informazioni sull'utente, intermediario tra viewModel e UserFirebaseDataSource, fonte dei dati
 */
public class UserRepository implements IUserRepository, UserResponseCallback {

    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final UserFirebaseDataSource userFirebaseDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> signUpResult = new MutableLiveData<>();
    private final DatabaseReference mDatabase;
    private final FirebaseAuth firebaseAuth;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userFirebaseDataSource = new UserFirebaseDataSource();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    public void saveUser(User user) {
        mDatabase.child(user.getUid()).setValue(user)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Firebase", "Dati dell'utente salvati con successo");
                    userMutableLiveData.setValue(new Result.UserSuccess(user));
                } else {
                    String errorMessage;
                    if (task.getException() != null) {
                        errorMessage = task.getException().getMessage();
                    } else {
                        errorMessage = "Errore sconosciuto";
                    }
                    Log.e("Firebase", "Errore nel salvataggio dei dati dell'utente: " + errorMessage);
                    userMutableLiveData.setValue(new Result.Error(errorMessage));
                }
            });
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(getLoggedUser().getUserName(), email, password);
        }
        return userMutableLiveData;
    }

    public LiveData<User> getUserLiveData(String uid) {
        return userFirebaseDataSource.getUserLiveData(uid);
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    public void signUp(String userName, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        saveUserData(userName, email, firebaseUser.getUid());
                    }
                } else {
                    signUpResult.setValue(new Result.Error(task.getException().getMessage()));
                }
            });
    }

    public MutableLiveData<Result> getSignUpResult() {
        return signUpResult;
    }

    private void saveUserData(String userName, String email, String uid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        User user = new User(userName, email, uid);
        databaseReference.child(uid).setValue(user);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            Log.d("DEBUG", "chiamo saveUserData() con utente: " + user);
            userMutableLiveData.postValue(new Result.UserSuccess(user));
            userDataRemoteDataSource.saveUserData(user);
        } else {
            Log.e("DEBUG", "Errore: utente nullo");
            userMutableLiveData.postValue(new Result.Error("Errore: utente nullo"));
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Log.e(TAG, "errore durante l'autenticazione: " + message);
        userMutableLiveData.postValue(new Result.Error(message));
    }
    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Log.d(TAG, "Dati dell'utente caricati correttamente: " + user);
        userMutableLiveData.postValue(new Result.UserSuccess(user));
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Log.e(TAG, "Errore durante il recupero dei dati del'utente: " + message);
        userMutableLiveData.postValue(new Result.Error(message));
    }

    @Override
    public void onSuccessLogout() {
        Log.d(TAG, "Logout avvenuto con successo, top");
        userMutableLiveData.postValue(new Result.UserSuccess(null));
    }

    @Override
    public void onSuccess(User user) {
        userMutableLiveData.postValue(new Result.UserSuccess(user));
    }

    @Override
    public void onFailure(Exception exception) {
        String errorMessage;
        if (exception != null) {
            errorMessage = exception.getMessage();
        } else {
            errorMessage = "errore boh";
        }
        Log.e("Firebase", "Errore nella richiesta: " + errorMessage);
        userMutableLiveData.postValue(new Result.Error(errorMessage));
    }
}
