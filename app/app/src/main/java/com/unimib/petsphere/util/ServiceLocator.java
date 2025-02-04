package com.unimib.petsphere.util;

import android.app.Application;

import com.unimib.petsphere.R;
import com.unimib.petsphere.repository.user.IUserRepository;
import com.unimib.petsphere.repository.user.UserRepository;
import com.unimib.petsphere.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.petsphere.source.user.BaseUserDataRemoteDataSource;
import com.unimib.petsphere.source.user.UserAuthenticationFirebaseDataSource;
import com.unimib.petsphere.source.user.UserFirebaseDataSource;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;
    private IUserRepository userRepository;

    private ServiceLocator() {}

    /**
     * Returns an instance of ServiceLocator class.
     * @return An instance of ServiceLocator.
     */
    public static synchronized ServiceLocator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServiceLocator();
        }
        return INSTANCE;
    }

    public IUserRepository getUserRepository(Application application) {
        UserFirebaseDataSource userDataRemoteDataSource = new UserFirebaseDataSource();
        UserAuthenticationFirebaseDataSource userRemoteAuthenticationDataSource = new UserAuthenticationFirebaseDataSource(userDataRemoteDataSource);
        return new UserRepository(userRemoteAuthenticationDataSource, userDataRemoteDataSource);
    }
}
