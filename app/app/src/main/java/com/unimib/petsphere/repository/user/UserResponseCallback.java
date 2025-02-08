// Author: Sara Angeretti

package com.unimib.petsphere.repository.user;

import com.unimib.petsphere.model.User;

import java.util.List;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
    void onSuccess(User user);
    void onFailure(Exception exception);
}
