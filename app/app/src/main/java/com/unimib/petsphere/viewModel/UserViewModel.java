package com.unimib.petsphere.viewModel;
//Author: Alessia Mazzeo
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.model.User;
import com.unimib.petsphere.data.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final UserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<User> loggedUserLiveData;
    private boolean authenticationError;
    private MutableLiveData<Result> signUpResultMutableLiveData;
    private MutableLiveData<Result> signInResultMutableLiveData;
    private MutableLiveData<Result> changePasswordResultMutableLiveData;
    private MutableLiveData<Result> signInWithGoogleResult = new MutableLiveData<>();

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
        loggedUserLiveData = new MutableLiveData<>();
        signUpResultMutableLiveData = new MutableLiveData<>();
        signInResultMutableLiveData = new MutableLiveData<>();
        changePasswordResultMutableLiveData = new MutableLiveData<>();

        User loggedUser = userRepository.getLoggedUser();
        if (loggedUser != null) {
            loggedUserLiveData.setValue(loggedUser);
        }
    }

    public LiveData<User> getLoggedUserLiveData() {
        return loggedUserLiveData;
    }


    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    public void signUp(String email, String password) {
        userRepository.signUp(email, password).observeForever(result -> {
            signUpResultMutableLiveData.postValue(result);
            if (result instanceof Result.UserSuccess) {
                loggedUserLiveData.postValue(((Result.UserSuccess) result).getUser());
            }
        });
    }


    public void signIn(String email, String password) {
        userRepository.signIn(email, password).observeForever(result -> {
            signInResultMutableLiveData.postValue(result);
        });
    }

    public LiveData<Result> getSignInResult() {
        return signInResultMutableLiveData;
    }

    public void changePassword(String email) {
        userRepository.changePassword(email).observeForever(result -> {
            changePasswordResultMutableLiveData.postValue(result);
        });
    }

    public void signInWithGoogle(String token) {
        userRepository.signInWithGoogle(token).observeForever(result -> {
            signInWithGoogleResult.postValue(result);
            if (result instanceof Result.UserSuccess) {
                loggedUserLiveData.postValue(((Result.UserSuccess) result).getUser());
            }
        });
    }
    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        userRepository.clearLoggedUser();
        loggedUserLiveData.postValue(null);

        return userMutableLiveData;
    }


    public LiveData<Result> getChangePasswordResult() {
        return changePasswordResultMutableLiveData;
    }
    public void changePw(String password) {
        userRepository.changePw(password).observeForever(result -> {
            changePasswordResultMutableLiveData.postValue(result);
        });
    }

    public LiveData<Result> getSignInWithGoogleResult() {
        return signInWithGoogleResult;
    }
}
