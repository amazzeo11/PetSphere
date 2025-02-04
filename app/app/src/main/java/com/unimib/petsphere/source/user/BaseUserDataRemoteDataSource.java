package com.unimib.petsphere.source.user;


import com.unimib.petsphere.model.User;
import com.unimib.petsphere.repository.user.UserResponseCallback;

import java.util.Set;

// classe base, gestisce i dati dell'utente

public abstract class BaseUserDataRemoteDataSource {
        protected UserResponseCallback userResponseCallback;

        public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
            this.userResponseCallback = userResponseCallback;
        }

        public abstract void saveUserData(User user);

    }

