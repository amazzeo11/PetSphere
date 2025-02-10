// Author: Sara Angeretti

package com.unimib.petsphere.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.petsphere.R;
import com.unimib.petsphere.model.Result;
import com.unimib.petsphere.util.Constants;
import com.unimib.petsphere.viewModel.UserViewModel;
import com.unimib.petsphere.model.User;
import com.unimib.petsphere.repository.user.SignUpCallback;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.concurrent.Executor;

public class SignUpFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getName();

    private TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextUserName;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private UserViewModel userViewModel;

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

        editTextUserName = view.findViewById(R.id.textInputUserName);
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

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // passaggio da signUpFragment a mainActivity
        signUpButton.setOnClickListener(v -> {
            String userName = editTextUserName.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            // chiamata al ViewModel per gestire la logica di registrazione
            userViewModel.signUpWithEmailAndPassword(userName, email, password, confirmPassword);
            goToMainPage();
        });

        userViewModel.getSignUpResult().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Error) {
                String errorMessage = ((Result.Error) result).getMessage();
                Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show();
            } else if (result instanceof Result.UserSuccess) {
                goToMainPage();
            }
        });
    }


    private void goToMainPage() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}