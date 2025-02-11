package com.unimib.petsphere.viewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.model.User;
import com.unimib.petsphere.data.repository.IUserRepository;
import java.util.Set;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> userPreferencesMutableLiveData;
    private MutableLiveData<User> loggedUserLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
        loggedUserLiveData = new MutableLiveData<>();

        // Inizializza l'utente loggato
        User loggedUser = userRepository.getLoggedUser();
        if (loggedUser != null) {
            loggedUserLiveData.setValue(loggedUser);
        }
    }

    public LiveData<User> getLoggedUserLiveData() {
        return loggedUserLiveData;
    }

    public MutableLiveData<Result> getUserMutableLiveData(String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            getUserData(token);
        }
        return userMutableLiveData;
    }

    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {
        if (idToken != null) {
            userRepository.saveUserPreferences(favoriteCountry, favoriteTopics, idToken);
        }
    }

    public MutableLiveData<Result> getUserPreferences(String idToken) {
        if (idToken != null) {
            userPreferencesMutableLiveData = userRepository.getUserPreferences(idToken);
        }
        return userPreferencesMutableLiveData;
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        // Resetta lo stato dell'utente loggato
        loggedUserLiveData.postValue(null);

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

        // Aggiorna il LiveData dell'utente loggato se l'accesso ha successo
        userMutableLiveData.observeForever(result -> {
            if (result instanceof Result.UserSuccess) {
                loggedUserLiveData.postValue(((Result.UserSuccess) result).getData());
            }
        });
    }

    private void getUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);

        // Aggiorna il LiveData dell'utente loggato se l'accesso ha successo
        userMutableLiveData.observeForever(result -> {
            if (result instanceof Result.UserSuccess) {
                loggedUserLiveData.postValue(((Result.UserSuccess) result).getData());
            }
        });
    }
}
