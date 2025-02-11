package com.unimib.petsphere.data.repository;

import androidx.lifecycle.MutableLiveData;


import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.model.User;

import java.util.Set;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> getUserPreferences(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    MutableLiveData<Result>  signUp(String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
    void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken);
}
