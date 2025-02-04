package com.unimib.petsphere.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.petsphere.model.Result;
import com.unimib.petsphere.model.User;
import com.unimib.petsphere.repository.user.IUserRepository;

public class UserViewModel extends ViewModel {
    //private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private boolean authenticationError;

    private MutableLiveData<Result> loginResult = new MutableLiveData<>();

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.userMutableLiveData = new MutableLiveData<>();
        this.authenticationError = false;
    }

    public MutableLiveData<Result> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    private void getUserData(String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(email, password, isUserRegistered);
    }

    private void getUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);
    }

    public void signIn(String email, String password) {
        userMutableLiveData.setValue(userRepository.getUser(email, password, true).getValue());
    }

    public void signInWithGoogle(String token) {
        userMutableLiveData.setValue(userRepository.getGoogleUser(token).getValue());
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public void loginWithEmailAndPassword(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User user = new User(
                                firebaseUser.getDisplayName(),
                                firebaseUser.getEmail(),
                                firebaseUser.getUid()
                        );

                        loginResult.setValue(new Result.UserSuccess(user));
                    }
                } else {
                    loginResult.setValue(new Result.Error(task.getException().getMessage()));
                }
            });
    }

    public MutableLiveData<Result> getLoginResult() {
        return loginResult;
    }

    public void signUpWithEmailAndPassword(String userName, String email, String password) {
        userRepository.signUp(userName, email, password);
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }
        return userMutableLiveData;
    }

}