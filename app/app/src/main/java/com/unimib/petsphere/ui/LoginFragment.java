package com.unimib.petsphere.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.petsphere.R;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getName();

    private Button loginButton, signUpButton;
    private EditText textInputEmail, textInputPassword;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginButton = view.findViewById(R.id.loginButton);
        signUpButton = view.findViewById(R.id.signUpButton);
        textInputEmail = view.findViewById(R.id.textInputEmail);
        textInputPassword = view.findViewById(R.id.textInputPassword);

        loginButton.setOnClickListener(v -> {
            mAuth.signInWithEmailAndPassword(textInputEmail.getText().toString(), textInputPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);

                                Intent intent = new Intent(requireActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                //NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                                //navController.navigate(R.id.action_loginFragment_to_mainActivity);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(requireContext(), "Autenticazione fallita",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
            /*
            if (true) {
            } else {
                // messaggio di errore
                Snackbar.make(view, "Autenticazione fallita, controlla mail e password inserite", Snackbar.LENGTH_SHORT).show();
            }*/
        });

        View navHost = requireActivity().findViewById(R.id.nav_host_fragment);
        if (navHost == null) {
            Log.e("LoginFragment", "lil debug per nav host");
        }

        signUpButton.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_loginFragment_to_signUpFragment);

                //Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signUpFragment);
            } catch (Exception e) {
                Log.e("LoginFragment", "Errore nella navigazione al SignUpFragment", e);
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // per ottenere l'utente corrnete
        FirebaseUser user = mAuth.getCurrentUser();

        Log.i(TAG, user + "");

    }

    /*
    private boolean isEmailOk(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordOk(String password) {
        int i = 0;
        boolean num = false, maiusc = false;

        if (password == null || password.length() <= 6) {
            return false;
        }

        while (!num && i < password.length()) {
            if (password.charAt(i) >= 48 && password.charAt(i) <= 57) {
                num = true;
            }
            if (password.charAt(i) >= 65 && password.charAt(i) <= 90) {
                maiusc = true;
            }
            i++;
        }

        if (!num || !maiusc)
            return false;
        return true;
    }
    */
}