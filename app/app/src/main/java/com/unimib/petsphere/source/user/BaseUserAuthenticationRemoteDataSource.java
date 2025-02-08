// Author: Sara Angeretti

package com.unimib.petsphere.source.user;


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
    public abstract void signUp(String email, String password);
    public abstract void signIn(String email, String password);
    public abstract void signInWithGoogle(String idToken);

    public void saveUserData(String userName, String email, String uid) {
    }
}
