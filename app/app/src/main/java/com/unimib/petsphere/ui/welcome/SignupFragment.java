package com.unimib.petsphere.ui.welcome;


import static com.unimib.petsphere.util.constants.USER_COLLISION_ERROR;
import static com.unimib.petsphere.util.constants.WEAK_PASSWORD_ERROR;

import static java.lang.Character.isDigit;
import static java.lang.Character.isUpperCase;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.model.User;
import com.unimib.petsphere.data.repository.IUserRepository;
import com.unimib.petsphere.ui.Main.MainActivity;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.UserViewModel;
import com.unimib.petsphere.viewModel.UserViewModelFactory;

import org.apache.commons.validator.routines.EmailValidator;

public class SignupFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputEditText textInputEmail, textInputPassword;


    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());

        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        textInputEmail = view.findViewById(R.id.textInputEmail);
        textInputPassword = view.findViewById(R.id.textInputPassword);
        Button backlog =   view.findViewById(R.id.back_to_log);
        view.findViewById(R.id.signupButton).setOnClickListener(v -> {
            String email = textInputEmail.getText().toString().trim();
            String password = textInputPassword.getText().toString().trim();

            if (isEmailOk(email) & isPasswordOk(password)) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    Log.d("SignupFragment", "Registrazione avvenuta con successo: " + user.getEmail());
                                    goToNextPage();
                                }
                            } else {

                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        "Errore durante la registrazione", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        backlog.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_signupFragment_to_loginFragment);
        });


        return view;
    }
    private final FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("SignupFragment", "Utente autenticato: " + currentUser.getEmail());
            goToNextPage();
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    private String getErrorMessage(String message) {
        switch(message) {
            case WEAK_PASSWORD_ERROR:
                return requireActivity().getString(R.string.error_pw);
            case USER_COLLISION_ERROR:
                return requireActivity().getString(R.string.error_collision_user);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }


    private boolean isEmailOk(String email) {

        if (!EmailValidator.getInstance().isValid((email))) {
            textInputEmail.setError(getString(R.string.error_email_login));
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }


    private boolean isPasswordOk(String password) {
        boolean lunghezza = false;
        boolean maiuscola = false;
        boolean numero = false;
        if (password.length() >= 8) {
            lunghezza = true;
        }

        for (int i = 0; i<password.length(); i++) {
            if (isDigit(password.charAt(i))) {
                numero = true;
            }
            if (isUpperCase(password.charAt(i))) {
                maiuscola = true;
            }
        }

        if(lunghezza && maiuscola && numero){
            return true;
        }else{
            if(!lunghezza){
                Snackbar.make(requireView(), R.string.error_length, Snackbar.LENGTH_SHORT).show();
                return false;
            }
            if(!maiuscola){
                Snackbar.make(requireView(), R.string.error_maiusc, Snackbar.LENGTH_SHORT).show();
                return false;
            }
            if(!numero){
                Snackbar.make(requireView(), R.string.error_num, Snackbar.LENGTH_SHORT).show();
                return false;
            }
            return false;
        }
    }
    private void goToNextPage() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}