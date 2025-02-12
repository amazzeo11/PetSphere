package com.unimib.petsphere.data.source;

import com.unimib.petsphere.data.repository.AuthCallback;
import com.unimib.petsphere.data.repository.UserRepository;

public abstract class BaseUserAuthenticationRemoteDataSource {
    public abstract void signUp(String email, String password, AuthCallback callback);
    public abstract void signIn(String email, String password, AuthCallback callback);
    public abstract void signInWithGoogle(String token, AuthCallback callback);
    public abstract void logout(AuthCallback callback);
    public abstract void changePassword(String email, AuthCallback callback);
    public abstract void getLoggedUser(AuthCallback callback);
    public abstract void changePw(String password, AuthCallback callback);
}