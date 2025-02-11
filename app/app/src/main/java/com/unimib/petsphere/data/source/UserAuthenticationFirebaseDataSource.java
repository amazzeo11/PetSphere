package com.unimib.petsphere.data.source;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.unimib.petsphere.data.model.User;

public class UserAuthenticationFirebaseDataSource extends BaseUserAuthenticationRemoteDataSource {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void signUp(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            User user = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                            callback.onSuccess(user);
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    @Override
    public void signIn(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            callback.onSuccess(new User(firebaseUser.getUid(), firebaseUser.getEmail()));
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    @Override
    public void signInWithGoogle(String token, AuthCallback callback) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(token, null);

        auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            User user = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure("Firebase user is null after Google sign-in.");
                        }
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Google sign-in failed.");
                    }
                });
    }


    @Override
    public void logout(AuthCallback callback) {
        auth.signOut();
        callback.onSuccess(null);
    }

    @Override
    public void changePassword(String email, AuthCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void getLoggedUser(AuthCallback callback) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            callback.onSuccess(new User(firebaseUser.getUid(), firebaseUser.getEmail()));
        } else {
            callback.onFailure("No logged-in user");
        }
    }

    public void clearLoggedUser() {
        auth.signOut();
    }
}
