package com.unimib.petsphere.source.user;

import static com.unimib.petsphere.util.Constants.*;

import android.util.Log;

import androidx.annotation.NonNull;
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

// Classe che gestisce l'autenticazione dell'utente usando Firebase Authentication

public class UserAuthenticationFirebaseDataSource extends BaseUserAuthenticationRemoteDataSource {

    private static final String TAG = UserAuthenticationFirebaseDataSource.class.getSimpleName();

    private final FirebaseAuth firebaseAuth;
    private final UserFirebaseDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData = new MutableLiveData<>();

    public UserAuthenticationFirebaseDataSource(UserFirebaseDataSource userDataRemoteDataSource) {
        this.userDataRemoteDataSource = userDataRemoteDataSource;
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
    public void signUp(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User user = new User(firebaseUser.getDisplayName(), email, firebaseUser.getUid());
                        // log di controlo per verificare l'utente prima di salvarlo
                        Log.d("UserSignUp", "utente da salvare: " + user.toString());
                        // lo salvo
                        userDataRemoteDataSource.saveUserData(user);

                        userResponseCallback.onSuccessFromAuthentication(user);
                    } else {
                        Log.d("UserSignUp", "dà null");
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                    }
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
        });
    }

    @Override
    public void signIn(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            Log.e(TAG, "Email e password vuote");
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                userResponseCallback.onSuccessFromAuthentication(
                                        new User(firebaseUser.getDisplayName(), email, firebaseUser.getUid())
                                );
                            } else {
                                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                            }
                        } else {
                            userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                        }
                    });
        }
    }

    @Override
    public void signInWithGoogle(String idToken) {
        if (idToken !=  null) {
            // Got an ID token from Google. Use it to authenticate with Firebase.
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User user = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getUid());
                        userMutableLiveData.postValue(new Result.UserSuccess(user));
                        userResponseCallback.onSuccessFromAuthentication(user);
                    } else {
                        String errorMessage = getErrorMessage(task.getException());
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        userMutableLiveData.postValue(new Result.Error(errorMessage));
                        userResponseCallback.onFailureFromAuthentication(errorMessage);
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            });
        }
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
