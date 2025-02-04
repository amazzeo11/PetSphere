package com.unimib.petsphere.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.unimib.petsphere.model.Result;
import com.unimib.petsphere.model.User;

import java.util.Set;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    void signUp(String email, String password, String userName);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
}
