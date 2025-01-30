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

import java.util.concurrent.Executor;

public class SignUpFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getName();

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
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            /*
            if (isNameValid(editTextFirstName.getText().toString())) {
                if (isNameValid(editTextLastName.getText().toString())) {
                    if (isEmailOk(editTextEmail.getText().toString())) {
                        if (isPasswordOk(editTextPassword.getText().toString())) {
                            if (isPasswordTheSame(editTextPassword.getText().toString(), editTextConfirmPassword.getText().toString())) {
                                //Log.d(TAG, "Va <3 registra l'utente");
                                //Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_mainActivity);
                            } else {
                                Snackbar.make(requireView(), "The password should be the same", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            //Log.e(TAG, "Error, Ig");
                            Snackbar.make(requireView(), "The password is not valid", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        //Log.e(TAG, "Error, Ig");
                        Snackbar.make(requireView(), "Check your email", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(requireView(), "Please don't leave the last name blank", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(requireView(), "Please don't leave the name blank", Snackbar.LENGTH_SHORT).show();
            }*/

            mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                    .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });

        });
    }

    private boolean isNameValid(String name) {
        /*int i = 0;
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

        return letters;*/
        return name != null;
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

        while (i < password.length()) {
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