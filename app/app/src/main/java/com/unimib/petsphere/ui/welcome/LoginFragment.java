package com.unimib.petsphere.ui.welcome;
//Author: Alessia Mazzeo
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.ui.Main.MainActivity;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.UserViewModel;
import com.unimib.petsphere.viewModel.UserViewModelFactory;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private TextInputEditText editTextEmail, editTextPassword;
    private UserViewModel userViewModel;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    public LoginFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication()))
        ).get(UserViewModel.class);


        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            if (idToken != null) {
                                userViewModel.signInWithGoogle(idToken);
                            }
                        } catch (ApiException e) {
                            Log.e(TAG, "Google Sign-in failed", e);
                            Snackbar.make(requireView(), R.string.error_unexpected, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);
        Button loginButton = view.findViewById(R.id.loginButton);
        Button signupButton = view.findViewById(R.id.buttonNewAccount);
        Button forgotPasswordButton = view.findViewById(R.id.forgot_pw);
        Button googleButton = view.findViewById(R.id.google_btn);

        userViewModel.getLoggedUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                goToNextPage();
            }
        });

        loginButton.setOnClickListener(v -> {
            String email = editTextEmail.getText() != null ? editTextEmail.getText().toString().trim() : "";
            String password = editTextPassword.getText() != null ? editTextPassword.getText().toString().trim() : "";

            if (isEmailOk(email) && isPasswordOk(password)) {
                userViewModel.signIn(email, password);
                userViewModel.getSignInResult().observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.UserSuccess && ((Result.UserSuccess) result).getUser() != null) {
                        goToNextPage();
                    } else if (result instanceof Result.Error) {
                        Snackbar.make(view, ((Result.Error) result).getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (!isEmailOk(email)) editTextEmail.setError(getString(R.string.error_email_login));
                if (!isPasswordOk(password)) editTextPassword.setError(getString(R.string.error_pw));
            }
        });

        signupButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signupFragment)
        );

        forgotPasswordButton.setOnClickListener(v -> {
            String email = editTextEmail.getText() != null ? editTextEmail.getText().toString().trim() : "";
            if (isEmailOk(email)) {
                userViewModel.changePassword(email);
                Snackbar.make(view, R.string.password_reset_email_sent, Snackbar.LENGTH_SHORT).show();
            } else {
                editTextEmail.setError(getString(R.string.error_email_login));
            }
        });


        googleButton.setOnClickListener(v -> startGoogleSignIn());

        userViewModel.getSignInWithGoogleResult().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.UserSuccess) {
                goToNextPage();
            } else if (result instanceof Result.Error) {
                Snackbar.make(requireView(), ((Result.Error) result).getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void startGoogleSignIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), result -> {
                    try {
                        activityResultLauncher.launch(new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build());
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't start Google Sign-In", e);
                    }
                })
                .addOnFailureListener(requireActivity(), e -> Log.e(TAG, "Google Sign-In failed", e));
    }

    private void goToNextPage() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isEmailOk(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordOk(String password) {
        return password.length() >= 6;
    }
}
