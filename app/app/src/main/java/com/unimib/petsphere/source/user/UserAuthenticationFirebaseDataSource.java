// Author: Sara Angeretti

package com.unimib.petsphere.source.user;

import static com.unimib.petsphere.util.Constants.*;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.petsphere.model.Result;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.unimib.petsphere.model.User;
import com.unimib.petsphere.repository.user.UserResponseCallback;

// Classe che gestisce l'autenticazione dell'utente usando Firebase Authentication

public class UserAuthenticationFirebaseDataSource extends BaseUserAuthenticationRemoteDataSource implements UserResponseCallback {

    private static final String TAG = UserAuthenticationFirebaseDataSource.class.getSimpleName();

    private final FirebaseAuth firebaseAuth;
    private final UserFirebaseDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Result> loginLiveData = new MutableLiveData<>();
    private UserResponseCallback userResponseCallback;

    public UserAuthenticationFirebaseDataSource(UserFirebaseDataSource userDataRemoteDataSource) {
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        userDataRemoteDataSource.setUserResponseCallback(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public User getLoggedUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            return new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getUid());
        } else {
            return null;
        }
    }

    public LiveData<Result> getLoginLiveData() {
        return loginLiveData;
    }

    @Override
    public void signUp(String userName, String email, String password) {
        Log.e("UserSignUp", "signUp() è stato chiamato con email: " + email); // cancella dopo

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    Log.e("UserSignUp", "sono dentro listener");
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser(); // dalla registrazione invece di current
                        //FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        Log.e("UserSignUp", "sono dentro if task succesful");
                        if (firebaseUser != null) {
                            Log.e("UserSignUp", "sono dentro if user non null");
                            String uid = firebaseUser.getUid();
                            User user = new User(userName, email, uid);
                            // log di controlo per verificare l'utente prima di salvarlo
                            Log.e("UserSignUp", "utente da salvare: " + user.toString());

                            Log.e("UserAuth", "Registrazione riuscita! UID: " + firebaseUser.getUid());
                            user.setUid(firebaseUser.getUid());
                            if (userResponseCallback != null) {
                                userResponseCallback.onSuccessFromAuthentication(user); // è qua che nullpointerexc
                            }

                            // lo salvo
                            userDataRemoteDataSource.saveUserData(user);
                        } else {
                            Log.e("UserAuth", "Registrazione riuscita, ma utente nullo!");
                            userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                        }
                    } else {
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                        Log.e("UserAuth", "Registrazione fallita: " + task.getException());
                    }
                });
    }

    @Override
    public void signIn(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            Log.e(TAG, "Email e password vuote");
            //userResponseCallback.onFailureFromAuthentication("Email e password non possono essere vuoti");
            loginLiveData.postValue(new Result.Error("Email e password non possono essere vuoti"));
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                /*if (userResponseCallback != null) {
                                    userResponseCallback.onSuccessFromAuthentication(new User(firebaseUser.getDisplayName(), email, firebaseUser.getUid()));
                                } else {
                                    Log.e(TAG, "userResponseCallback is null when attempting to call onSuccessFromAuthentication.");
                                }*/
                                loginLiveData.postValue(new Result.UserSuccess(new User(firebaseUser.getDisplayName(), email, firebaseUser.getUid())));
                                /*userResponseCallback.onSuccessFromAuthentication( //npe
                                        new User(firebaseUser.getDisplayName(), email, firebaseUser.getUid())
                                );*/
                            } else {
                                //userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                                loginLiveData.postValue(new Result.Error(getErrorMessage(task.getException())));
                            }
                        } else {
                            //userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                           loginLiveData.postValue(new Result.Error(getErrorMessage(task.getException())));
                        }
                    });
        }
    }

    @Override
    public void logout() {
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    firebaseAuth.removeAuthStateListener(this);
                    Log.d(TAG, "User logged out");
                    userResponseCallback.onSuccessLogout();
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.signOut();
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
    public void onFailure(Exception e) {
        userMutableLiveData.postValue(new Result.Error(e.getMessage()));
    }

    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return WEAK_PASSWORD_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return INVALID_CREDENTIALS_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            return INVALID_USER_ERROR;
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return USER_COLLISION_ERROR;
        }
        return UNEXPECTED_ERROR;
    }
}
