package com.unimib.petsphere.ui.welcome;
//Author: Alessia Mazzeo

import static java.lang.Character.isDigit;
import static java.lang.Character.isUpperCase;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.unimib.petsphere.R;
import com.unimib.petsphere.ui.Main.MainActivity;
import com.unimib.petsphere.ui.viewModel.UserViewModel;
import com.unimib.petsphere.ui.viewModel.UserViewModelFactory;

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


        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(this.getActivity().getApplication())).get(UserViewModel.class);

        userViewModel.setAuthenticationError(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        textInputEmail = view.findViewById(R.id.textInputEmail);
        textInputPassword = view.findViewById(R.id.textInputPassword);
        Button backlog = view.findViewById(R.id.back_to_log);
        Button signupButton = view.findViewById(R.id.signupButton);


        userViewModel.getLoggedUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                goToNextPage();
            }
        });



        signupButton.setOnClickListener(v -> {
            String email = textInputEmail.getText().toString().trim();
            String password = textInputPassword.getText().toString().trim();
            if (isEmailOk(email) & isPasswordOk(password)) {
                userViewModel.signUp(email, password);
            }
        });

        backlog.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_signupFragment_to_loginFragment));

        return view;
    }


    private void goToNextPage() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
                textInputPassword.setError(getString(R.string.error_length));
                return false;
            }
            if(!maiuscola){
                textInputPassword.setError(getString(R.string.error_maiusc));
                return false;
            }
            if(!numero){
                textInputPassword.setError(getString(R.string.error_num));
                return false;
            }
            return false;
        }
    }
}
