package com.unimib.petsphere.data.source;


import static com.unimib.petsphere.util.constants.FIREBASE_REALTIME_DATABASE;
import static com.unimib.petsphere.util.constants.FIREBASE_USERS_COLLECTION;

import static java.lang.Character.isDigit;
import static java.lang.Character.isUpperCase;

import android.util.Log;

import androidx.annotation.NonNull;


import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.User;
import com.unimib.petsphere.util.SharedPreferencesUtils;


import org.apache.commons.validator.routines.EmailValidator;

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
/*
    public  void signUp(String email, String password){
    if (isEmailOk(email) & isPasswordOk(password)) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                           userResponseCallback.onSuccessFromRegistration(user);

                        }
                    } else {
                        userResponseCallback.onFailureFromRegistration();

                    }
                });
    }

    }
    private boolean isEmailOk(String email) {

        if (!EmailValidator.getInstance().isValid((email))) {
            textInputEmail.setError(getString(R.string.error_email_login));
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }


    private boolean isPasswordOk(String password) {
        boolean lunghezza = false;
        boolean maiuscola = false;
        boolean numero = false;
        if (password.length() >= 8) {
            lunghezza = true;
        }

        for (int i = 0; i<password.length(); i++) {
            if (isDigit(password.charAt(i))) {
                numero = true;
            }
            if (isUpperCase(password.charAt(i))) {
                maiuscola = true;
            }
        }

        if(lunghezza && maiuscola && numero){
            return true;
        }else{
            if(!lunghezza){
                Snackbar.make(requireView(), R.string.error_length, Snackbar.LENGTH_SHORT).show();
                return false;
            }
            if(!maiuscola){
                Snackbar.make(requireView(), R.string.error_maiusc, Snackbar.LENGTH_SHORT).show();
                return false;
            }
            if(!numero){
                Snackbar.make(requireView(), R.string.error_num, Snackbar.LENGTH_SHORT).show();
                return false;
            }
            return false;
        }
    }*/
}
