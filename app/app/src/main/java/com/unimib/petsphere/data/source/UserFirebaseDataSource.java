package com.unimib.petsphere.data.source;


import static com.unimib.petsphere.util.constants.FIREBASE_REALTIME_DATABASE;
import static com.unimib.petsphere.util.constants.FIREBASE_USERS_COLLECTION;

import android.util.Log;

import androidx.annotation.NonNull;


import com.unimib.petsphere.data.model.User;
import com.unimib.petsphere.util.SharedPreferencesUtils;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UserFirebaseDataSource extends BaseUserDataRemoteDataSource {

    private static final String TAG = UserFirebaseDataSource.class.getSimpleName();

    public UserFirebaseDataSource(SharedPreferencesUtils sharedPreferencesUtil) {
        super();
    }


    @Override
    public void saveUserData(User user) {
        
    }

    @Override
    public void getUserPreferences(String idToken) {

    }

    @Override
    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {

    }
}
