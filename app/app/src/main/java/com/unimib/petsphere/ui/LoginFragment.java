package com.unimib.petsphere.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.petsphere.R;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    private TextInputEditText editTextEmail, editTextPassword;

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

        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        Button loginButton = view.findViewById(R.id.loginButton);

        Button signUpButton = view.findViewById(R.id.signUpButton);

        // passaggio da fragment_login a fragment_sign_up
        signUpButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signUpFragment);
        });

        // passaggio da fragment_login a MainActivity
        loginButton.setOnClickListener(v -> {
            if (isEmailOk(editTextEmail.getText().toString())) {
                if (isPasswordOk(editTextPassword.getText().toString())) {
                    //Log.d(TAG, "Launch new activity.");
                    //Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_mainActivity);
                } else {
                    //Log.e(TAG, "Error, Ig");
                    Snackbar.make(view.findViewById(android.R.id.content), "Check your password", Snackbar.LENGTH_SHORT)
                            .show();
                }
            } else {
                //Log.e(TAG, "Error, Ig");
                Snackbar.make(view.findViewById(android.R.id.content), "Check your email", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

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
}

