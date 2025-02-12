package com.unimib.petsphere.data.repository;


import com.unimib.petsphere.data.model.User;

public interface AuthCallback {
    void onSuccess(User user);
    void onFailure(String message);
}
