// Author: Sara Angeretti

package com.unimib.petsphere.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unimib.petsphere.model.Result;
import com.unimib.petsphere.model.User;
import com.unimib.petsphere.repository.user.IUserRepository;
import com.unimib.petsphere.repository.user.UserResponseCallback;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private boolean authenticationError;
    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private LiveData<User> userLiveData;

    private MutableLiveData<Result> loginResult = new MutableLiveData<>();
    private MutableLiveData<Result> userUpdateResult = new MutableLiveData<>();

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.userMutableLiveData = new MutableLiveData<>();
        this.authenticationError = false;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            userLiveData = userRepository.getUserLiveData(uid);
        } else {
            userLiveData = new MutableLiveData<>();
        }
    }

    // salvo l'utente usando la Repository
    public void saveUser(User user) {
        userRepository.saveUser(user);
    }

    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    public MutableLiveData<Result> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public LiveData<User> getUserLiveData(String uid) {
        return userRepository.getUserLiveData(uid);
    }

    public LiveData<Result> getUserUpdateResult() {
        return userUpdateResult;
    }

    // aggiorno nome utente
    public void updateUserName(String newUserName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            User user = new User(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getUid());

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newUserName)
                    .build();

            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("UserViewModel", "Nome utente aggiornato correttamente");
                            // aggiorno anche il database
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
                            String userId = currentUser.getUid();
                            // aggiungo il nuovo nome utente nel database
                            database.child(userId).child("username").setValue(newUserName)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Log.d("UserViewModel", "Nome utente aggiornato nel database");
                                            userUpdateResult.setValue(new Result.UserSuccess(user));
                                        } else {
                                            Log.e("UserViewModel", "Errore nell'aggiornamento del nome utente nel database", dbTask.getException());
                                            userUpdateResult.setValue(new Result.Error("Errore nell'aggiornamento del nome utente"));
                                        }
                                    });
                        } else {
                            Log.e("UserViewModel", "Errore aggiornamento nome utente", task.getException());
                        }
                    });
        }
    }

    // modifica password - da documentazione Firebase
    public void updatePassword(String oldPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // l'utente si deve reautenticare con la vecchia password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // la reautenticazione ha successo
                        user.updatePassword(newPassword)
                            .addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    isPasswordUpdated.setValue(true); // password aggiornata con successo
                                    Log.d("UserViewModel", "Password aggiornata con successo");
                                } else {
                                    isPasswordUpdated.setValue(false); // password NON aggiornata con successo
                                    Log.e("UserViewModel", "Errore aggiornamento password", updateTask.getException());
                                }
                            });
                    } else {
                        // la reautenticazione fallisce
                        isPasswordUpdated.setValue(false); // Password NON aggiornata con successo
                        Log.e("UserViewModel", "Errore nella reautenticazione", task.getException());
                    }
                });
        }
    }

    // per modifica dati utente, boolean
    private MutableLiveData<Boolean> isPasswordUpdated = new MutableLiveData<>(null);
    // accessibile da fuori, boolean
    public LiveData<Boolean> isPasswordUpdated() {
        return isPasswordUpdated;
    }
    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    private void getUserData(String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(email, password, isUserRegistered);
    }

    public void signIn(String email, String password) {
        userMutableLiveData.setValue(userRepository.getUser(email, password, true).getValue());
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public void loginWithEmailAndPassword(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User user = new User(
                                firebaseUser.getDisplayName(),
                                firebaseUser.getEmail(),
                                firebaseUser.getUid()
                        );

                        loginResult.setValue(new Result.UserSuccess(user));
                    }
                } else {
                    loginResult.setValue(new Result.Error(task.getException().getMessage()));
                }
            });
    }

    public MutableLiveData<Result> getLoginResult() {
        return loginResult;
    }

    public void signUpWithEmailAndPassword(String userName, String email, String password) {
        userRepository.signUp(userName, email, password);
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }
        return userMutableLiveData;
    }

}