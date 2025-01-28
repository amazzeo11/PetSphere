package com.unimib.petsphere.ui;

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

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.petsphere.R;

import org.apache.commons.validator.routines.EmailValidator;

public class SignUpFragment extends Fragment {

    private TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextFirstName, editTextLastName;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextFirstName = view.findViewById(R.id.textInputUserFirstName);
        editTextLastName = view.findViewById(R.id.textInputUserLastName);
        editTextEmail = view.findViewById(R.id.textInputEmailInSignUp);
        editTextPassword = view.findViewById(R.id.textInputPasswordInSignUp);
        editTextConfirmPassword = view.findViewById(R.id.textInputConfirmPasswordInSignUp);

        Button backToLoginButton = view.findViewById(R.id.alreadySignedUpButton);

        Button signUpButton = view.findViewById(R.id.signUpButtonInSignUp);

        Log.d("DEBUG", "signUpButton = " + signUpButton);

        // sei già un utente?
        // passaggio da signUpFragment a loginFragment
        backToLoginButton.setOnClickListener(v -> {
            try {
                NavController navController = NavHostFragment.findNavController(SignUpFragment.this);
                Log.d("NAVIGATION", "Current destination: " + navController.getCurrentDestination().getId());
                navController.navigate(R.id.action_signUpFragment_to_loginFragment);

            } catch (Exception e) {
                Log.e("DEBUG", "Errore navigazione", e);
            }
        });

        // passaggio da signUpFragment a mainActivity
        signUpButton.setOnClickListener(v -> {

            if (isNameValid(editTextFirstName.getText().toString())) {
                if (isNameValid(editTextLastName.getText().toString())) {
                    if (isEmailOk(editTextEmail.getText().toString())) {
                        if (isPasswordOk(editTextPassword.getText().toString())) {
                            if (isPasswordTheSame(editTextPassword.getText().toString(), editTextConfirmPassword.getText().toString())) {
                                //Log.d(TAG, "Va <3 registra l'utente");
                                //Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_mainActivity);
                            } else {
                                Snackbar.make(requireView(), "The password isn't the same", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            //Log.e(TAG, "Error, Ig");
                            Snackbar.make(requireView(), "The password should be at least 7 chars long and made by small and capital letters, numbers and symbols", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        //Log.e(TAG, "Error, Ig");
                        Snackbar.make(requireView(), "Check your email", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(requireView(), "Your first name should be made only of letters, and at least 3 chars long", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(requireView(), "Your last name should be made only of letters, and at least 3 chars long", Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    private boolean isNameValid(String name) {
        int i = 0;
        boolean letters = false;

        if (name == null || name.length() <= 2) {
            return false;
        }

        // solo lettere maiuscole o minuscole + apostrofo (value 39)
        while (!letters && i < name.length()) {
            if (name.charAt(i) == 39 || name.charAt(i) >= 65 && name.charAt(i) <= 90 || name.charAt(i) >= 97 && name.charAt(i) <= 122) {
                letters = true;
            }
            i++;
        }

        return letters;
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

    private boolean isPasswordTheSame(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

}