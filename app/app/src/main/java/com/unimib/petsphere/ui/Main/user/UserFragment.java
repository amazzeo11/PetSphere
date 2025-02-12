package com.unimib.petsphere.ui.Main.user;
//Author: Alessia Mazzeo
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.petsphere.R;
import com.unimib.petsphere.ui.welcome.WelcomeActivity;

public class UserFragment extends Fragment {

    private TextView textViewEmail;
    private EditText editTextNewPassword;
    private Button buttonChangePassword, buttonLogout;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    public UserFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        textViewEmail = view.findViewById(R.id.textViewEmail);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        editTextNewPassword.setVisibility(View.GONE);


        if (currentUser != null) {
            textViewEmail.setText(currentUser.getEmail());
        } else {
            textViewEmail.setText(R.string.error_no_user);
        }

        buttonChangePassword.setOnClickListener(v -> {
            String newPassword = editTextNewPassword.getText().toString().trim();
            editTextNewPassword.setVisibility(View.VISIBLE);
            if (newPassword.length() < 6) {
                editTextNewPassword.setError(getString(R.string.error_password_too_short));
                return;
            }

            if (currentUser != null) {
                currentUser.updatePassword(newPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password cambiata con successo!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Errore nel cambio password.", Toast.LENGTH_SHORT).show();
                                Log.e("UserFragment", "Errore cambio password", task.getException());
                            }
                        });
            }
        });


        buttonLogout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
