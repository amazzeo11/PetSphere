// Author: Sara Angeretti

package com.unimib.petsphere.repository.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.petsphere.model.Result;
import com.unimib.petsphere.model.User;

import java.util.Set;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    LiveData<User> getUserLiveData (String uid);
    MutableLiveData<Result> logout();
    void signUpWithEmailAndPassword(String userName, String email, String password, String confirmPassword, SignUpCallback callback);
    void saveUser(User user);
    User getLoggedUser();
    void signUp(String email, String password, String userName);
    void signIn(String email, String password);
}
