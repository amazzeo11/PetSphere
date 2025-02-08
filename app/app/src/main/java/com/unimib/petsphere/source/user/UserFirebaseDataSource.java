package com.unimib.petsphere.source.user;

import static com.unimib.petsphere.util.Constants.*;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.petsphere.model.User;
import com.unimib.petsphere.repository.user.UserResponseCallback;

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

    // voglio prendere i dati dell'utente
    public LiveData<User> getUserLiveData(String uid) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    userLiveData.setValue(user);
                } else {
                    Log.e("UserRepository", "Utente non trovato nel database");
                    userLiveData.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserRepository", "Errore nel caricamento utente", databaseError.toException());
                userLiveData.setValue(null);
            }
        });

        return userLiveData;
    }


}
