package com.unimib.petsphere.source.user;

import static com.unimib.petsphere.util.Constants.*;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.petsphere.model.User;

/**
 * Classe che ottiene le informazioni dell'utente usando Firebase Realtime Database
 */
public class UserFirebaseDataSource extends BaseUserDataRemoteDataSource {

    private static final String TAG = UserFirebaseDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;

    public UserFirebaseDataSource() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    // voglio salvare i dati dell'utente
    @Override
    public void saveUserData(User user) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = databaseReference.child(uid);

        // salvo l'utente
        userRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("UserFirebaseDataSource", "Dati utente salvati con successo.");
                userResponseCallback.onSuccess(user);
            } else {
                Log.e("UserFirebaseDataSource", "Errore nel salvataggio dei dati", task.getException());
                userResponseCallback.onFailure(task.getException());
            }
        });
    }

    // voglio i dati dell'utente
    public void getUserData(String uid) {
        DatabaseReference userRef = databaseReference.child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                } else {
                    userResponseCallback.onFailureFromRemoteDatabase("Dati non trovati");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userResponseCallback.onFailureFromRemoteDatabase(databaseError.getMessage());
            }
        });
    }

}
