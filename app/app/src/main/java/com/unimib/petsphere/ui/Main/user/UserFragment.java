package com.unimib.petsphere.ui.Main.user;

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
import com.unimib.petsphere.data.repository.PetRepository;
import com.unimib.petsphere.data.repository.UserRepository;
import com.unimib.petsphere.ui.welcome.WelcomeActivity;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.PetViewModel;
import com.unimib.petsphere.viewModel.PetViewModelFactory;
import com.unimib.petsphere.viewModel.UserViewModel;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.viewModel.UserViewModelFactory;

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
        UserRepository userRepository = ServiceLocator.getInstance().getUserRepository(
                requireActivity().getApplication()
        );
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);


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
            if (newPassword.length() < 6) {
                editTextNewPassword.setError(getString(R.string.error_password_too_short));
                return;
            }

            userViewModel.changePw(newPassword);
            userViewModel.getChangePasswordResult().observe(getViewLifecycleOwner(), result -> {
                if (result instanceof Result.UserSuccess) {
                    Toast.makeText(getContext(), "Password cambiata con successo!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Errore nel cambio password.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonLogout.setOnClickListener(v -> {
            userViewModel.logout().observe(getViewLifecycleOwner(), result -> {
                if (result instanceof Result.UserSuccess) {
                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Errore durante il logout.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}