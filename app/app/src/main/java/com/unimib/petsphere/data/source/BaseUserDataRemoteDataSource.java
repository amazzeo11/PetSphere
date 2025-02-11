package com.unimib.petsphere.data.source;


import com.unimib.petsphere.data.model.User;
import com.unimib.petsphere.data.repository.UserResponseCallback;

import java.util.Set;

public abstract class BaseUserDataRemoteDataSource {
        protected UserResponseCallback userResponseCallback;

        public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
            this.userResponseCallback = userResponseCallback;
        }

        public abstract void saveUserData(User user);


        public abstract void getUserPreferences(String idToken);

        public abstract void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken);
    }

