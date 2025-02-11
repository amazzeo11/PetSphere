package com.unimib.petsphere.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.model.User;
import com.unimib.petsphere.data.source.AuthCallback;
import com.unimib.petsphere.data.source.BaseUserAuthenticationRemoteDataSource;

public class UserRepository {

    private final BaseUserAuthenticationRemoteDataSource authDataSource;
    private final MutableLiveData<Result> userLiveData = new MutableLiveData<>();
    private User loggedUser;

    public UserRepository(BaseUserAuthenticationRemoteDataSource authDataSource) {
        this.authDataSource = authDataSource;
    }

    public LiveData<Result> signUp(String email, String password) {
        MutableLiveData<Result> signUpLiveData = new MutableLiveData<>();

        authDataSource.signUp(email, password, new AuthCallback() {
            @Override
            public void onSuccess(User user) {
                loggedUser = user;
                signUpLiveData.postValue(new Result.UserSuccess(user));
                userLiveData.postValue(new Result.UserSuccess(user));
            }

            @Override
            public void onFailure(String message) {
                signUpLiveData.postValue(new Result.Error(message));
            }
        });
        return signUpLiveData;
    }


    public LiveData<Result> signIn(String email, String password) {
        authDataSource.signIn(email, password, new AuthCallback() {
            @Override
            public void onSuccess(User user) {
                loggedUser = user;
                userLiveData.postValue(new Result.UserSuccess(user));
            }

            @Override
            public void onFailure(String message) {
                userLiveData.postValue(new Result.Error(message));
            }
        });
        return userLiveData;
    }

    public LiveData<Result> signInWithGoogle(String token) {
        authDataSource.signInWithGoogle(token, new AuthCallback() {
            @Override
            public void onSuccess(User user) {
                loggedUser = user;
                userLiveData.postValue(new Result.UserSuccess(user));
            }

            @Override
            public void onFailure(String message) {
                userLiveData.postValue(new Result.Error(message));
            }
        });
        return userLiveData;
    }

    public MutableLiveData<Result> logout() {
        authDataSource.logout(new AuthCallback() {
            @Override
            public void onSuccess(User user) {
                clearLoggedUser();
                userLiveData.postValue(new Result.Success("Logout successful"));
            }

            @Override
            public void onFailure(String message) {
                userLiveData.postValue(new Result.Error(message));
            }
        });
        return userLiveData;
    }

    public LiveData<Result> changePassword(String email) {
        authDataSource.changePassword(email, new AuthCallback() {
            @Override
            public void onSuccess(User user) {
                userLiveData.postValue(new Result.Success("Password reset email sent"));
            }

            @Override
            public void onFailure(String message) {
                userLiveData.postValue(new Result.Error(message));
            }
        });
        return userLiveData;
    }

    public User getLoggedUser() {
        if (loggedUser == null) {
            fetchLoggedUser();
        }
        return loggedUser;
    }

    public void fetchLoggedUser() {
        authDataSource.getLoggedUser(new AuthCallback() {
            @Override
            public void onSuccess(User user) {
                loggedUser = user;
            }

            @Override
            public void onFailure(String message) {
                loggedUser = null;
            }
        });
    }

    public void clearLoggedUser() {
        loggedUser = null;
    }

    public void getUser(String email, String password, boolean isUserRegistered) {
    }
}
