// Author: Sara Angeretti

package com.unimib.petsphere.repository.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.petsphere.model.Result;
import com.unimib.petsphere.model.User;
import com.unimib.petsphere.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.petsphere.source.user.BaseUserDataRemoteDataSource;
import com.unimib.petsphere.source.user.UserFirebaseDataSource;
import com.unimib.petsphere.source.user.UserAuthenticationFirebaseDataSource;
import com.unimib.petsphere.util.Constants;
import com.unimib.petsphere.repository.user.UserResponseCallback;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.List;
import java.util.Set;


/**
 * Repository class che ottiene le informazioni sull'utente, intermediario tra viewModel e UserFirebaseDataSource, fonte dei dati
 */
public class UserRepository implements IUserRepository, UserResponseCallback {

    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final UserFirebaseDataSource userFirebaseDataSource;
    private final UserAuthenticationFirebaseDataSource userAuthenticationFirebaseDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> signUpResult = new MutableLiveData<>();
    private final DatabaseReference mDatabase;
    private final FirebaseAuth firebaseAuth;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userFirebaseDataSource = new UserFirebaseDataSource();
        this.userAuthenticationFirebaseDataSource = new UserAuthenticationFirebaseDataSource(this.userFirebaseDataSource);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    private boolean isNameValid(String name) {
        return name != null && !name.isEmpty();
    }

    private boolean isEmailOk(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordWithNum(String password) {
        int i = 0;
        boolean num = false;
        while (i < password.length()) {
            if (password.charAt(i) >= 48 && password.charAt(i) <= 57) {
                num = true;
            }
            i++;
        }
        return num;
    }

    private boolean isPasswordWithMaiusc(String password) {
        int i = 0;
        boolean maiusc = false;
        while (i < password.length()) {
            if (password.charAt(i) >= 65 && password.charAt(i) <= 90) {
                maiusc = true;
            }
            i++;
        }
        return maiusc;
    }

    private boolean isPasswordLongEnough (String password) {
        if (password == null || password.length() < Constants.MINIMUM_LENGTH_PASSWORD) {
            return false;
        }
        return true;
    }

    private boolean isPasswordTheSame(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    // validazione dati registrazione

    public void signUpWithEmailAndPassword(String userName, String email, String password, String confirmPassword) {
        // validazioni
        if (!isNameValid(userName)) {
            signUpResult.setValue(new Result.Error("Il nome utente non è valido."));
            return;
        }

        if (!isEmailOk(email)) {
            signUpResult.setValue(new Result.Error("L'email non è valida."));
            return;
        }

        if (!isPasswordLongEnough(password)) {
            signUpResult.setValue(new Result.Error("La password deve essere lunga almeno 7 caratteri."));
            return;
        }

        if (!isPasswordWithNum(password)) {
            signUpResult.setValue(new Result.Error("La password deve contenere almeno un numero."));
            return;
        }

        if (!isPasswordWithMaiusc(password)) {
            signUpResult.setValue(new Result.Error("La password deve contenere almeno una lettera maiuscola."));
            return;
        }

        if (!isPasswordTheSame(password, confirmPassword)) {
            signUpResult.setValue(new Result.Error("La conferma della password non è valida."));
            return;
        }

        userAuthenticationFirebaseDataSource.signUp(userName, email, password);
        //User newUser = new User(userName, email, password);
        signUp(userName, email, password);
        //saveUser(newUser);
        //signUpResult.setValue(new Result.UserSuccess(newUser));
    }

    public void saveUser(User user) {
        userDataRemoteDataSource.saveUserData(user);
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(getLoggedUser().getUserName(), email, password);
        }
        return userMutableLiveData;
    }

    public LiveData<User> getUserLiveData(String uid) {
        return userFirebaseDataSource.getUserLiveData(uid);
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    public void signUp(String userName, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User user = new User(userName, email, firebaseUser.getUid());
                        saveUserData(userName, email, firebaseUser.getUid());
                        signUpResult.setValue(new Result.UserSuccess(user));
                    }
                } else {
                    signUpResult.setValue(new Result.Error(task.getException().getMessage()));
                }
            });
    }

    public MutableLiveData<Result> getSignUpResult() {
        return signUpResult;
    }

    private void saveUserData(String userName, String email, String uid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        User user = new User(userName, email, uid);
        databaseReference.child(uid).setValue(user);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        userRemoteDataSource.onSuccessFromAuthentication(user);
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        userRemoteDataSource.onFailureFromAuthentication(message);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        userRemoteDataSource.onSuccessFromRemoteDatabase(user);
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        userRemoteDataSource.onFailureFromRemoteDatabase(message);
    }

    @Override
    public void onSuccessLogout() {
        userRemoteDataSource.onSuccessLogout();
    }

    @Override
    public void onSuccess(User user) {
        userRemoteDataSource.onSuccess(user);
    }

    @Override
    public void onFailure(Exception exception) {
        userRemoteDataSource.onFailure(exception);
    }
}
