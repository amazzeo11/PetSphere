package com.unimib.petsphere.repository.user;

import com.unimib.petsphere.model.User;

public interface SignUpCallback {
    void onSuccess(User u);

    void onFailure(Exception e);
}
