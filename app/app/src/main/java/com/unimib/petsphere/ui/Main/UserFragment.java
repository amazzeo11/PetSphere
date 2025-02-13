package com.unimib.petsphere.ui.Main;

import static java.lang.Character.isDigit;
import static java.lang.Character.isUpperCase;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;

import com.unimib.petsphere.R;
import com.unimib.petsphere.ui.welcome.WelcomeActivity;
import com.unimib.petsphere.ui.viewModel.UserViewModel;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.ui.viewModel.UserViewModelFactory;

public class UserFragment extends Fragment {

    private TextView textViewEmail;
    private EditText editTextNewPassword;
    private Button buttonChangePassword, buttonLogout;
    private UserViewModel userViewModel;

    public UserFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(this.getActivity().getApplication())).get(UserViewModel.class);



        textViewEmail = view.findViewById(R.id.textViewEmail);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        editTextNewPassword.setVisibility(View.GONE);

        userViewModel.getLoggedUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                textViewEmail.setText(user.getEmail());
            } else {
                textViewEmail.setText(R.string.error_no_user);
            }
        });

        buttonChangePassword.setOnClickListener(v -> {
            String newPassword = editTextNewPassword.getText().toString().trim();
            editTextNewPassword.setVisibility(View.VISIBLE);
            if (isPasswordOk(newPassword)) {
                userViewModel.changePw(newPassword);
                userViewModel.getChangePasswordResult().observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.UserSuccess) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.password_cambiata_con_successo), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.errore_nel_cambio_password), Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });

        buttonLogout.setOnClickListener(v -> {
            userViewModel.logout().observe(getViewLifecycleOwner(), result -> {
                if (result instanceof Result.UserSuccess) {
                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.errore_durante_il_logout), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
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
                editTextNewPassword.setError(getString(R.string.error_length));
                return false;
            }
            if(!maiuscola){
                editTextNewPassword.setError(getString(R.string.error_maiusc));
                return false;
            }
            if(!numero){
                editTextNewPassword.setError(getString(R.string.error_num));
                return false;
            }
            return false;
        }
    }
}