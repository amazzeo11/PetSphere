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

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.petsphere.R;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getName();

    private Button loginButton, signUpButton;
    private EditText textInputEmail, textInputPassword;

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
            if (true) {
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Chiude WelcomeActivity
                startActivity(intent);
            } else {
                Snackbar.make(view, "Check your email and password", Snackbar.LENGTH_SHORT).show();
            }
        });

        View navHost = requireActivity().findViewById(R.id.nav_host_fragment);
        if (navHost == null) {
            Log.e("LoginFragment", "Il nav_host_fragment non è stato trovato!");
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