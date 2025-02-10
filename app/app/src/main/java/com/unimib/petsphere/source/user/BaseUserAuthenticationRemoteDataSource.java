// Author: Sara Angeretti

package com.unimib.petsphere.source.user;


import android.util.Log;

import com.unimib.petsphere.model.Result;
import com.unimib.petsphere.model.User;
import com.unimib.petsphere.repository.user.UserResponseCallback;

// classe base, gestisce l'autenticazione dell'utente

public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract User getLoggedUser();
    public abstract void logout();
    public abstract void signUp(String userName, String email, String password);
    public abstract void signIn(String email, String password);
    public abstract void onSuccessFromAuthentication(User user);
    public abstract void onFailureFromAuthentication(String message);
    public abstract void onSuccessFromRemoteDatabase(User user);
    public abstract void onFailureFromRemoteDatabase(String message);
    public abstract void onSuccessLogout();
    public abstract void onSuccess(User user);
    public abstract void onFailure(Exception e);

    public void saveUserData(String userName, String email, String uid) {
    }
}
