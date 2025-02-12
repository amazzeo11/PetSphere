package com.unimib.petsphere.data.repository;

import androidx.lifecycle.MutableLiveData;


import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.model.User;



import androidx.lifecycle.LiveData;

public interface IUserRepository {
    LiveData<Result> signUp(String email, String password);
    LiveData<Result> signIn(String email, String password);
    LiveData<Result> signInWithGoogle(String token);
    LiveData<Result> changePassword(String email);
    LiveData<Result> logout();
    User getLoggedUser();
    void fetchLoggedUser();
    void clearLoggedUser();
    void getUser(String email, String password, boolean isUserRegistered);
}
