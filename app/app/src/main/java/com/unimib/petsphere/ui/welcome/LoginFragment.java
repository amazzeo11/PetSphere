package com.unimib.petsphere.ui.welcome;

import static com.unimib.petsphere.util.constants.INVALID_CREDENTIALS_ERROR;
import static com.unimib.petsphere.util.constants.INVALID_USER_ERROR;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.model.User;
import com.unimib.petsphere.data.repository.IUserRepository;
import com.unimib.petsphere.ui.Main.MainActivity;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.UserViewModel;
import com.unimib.petsphere.viewModel.UserViewModelFactory;

import org.apache.commons.validator.routines.EmailValidator;


public class LoginFragment extends Fragment {

    private final static String TAG = LoginFragment.class.getSimpleName();

    private TextInputEditText editTextEmail, editTextPassword;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;
    private UserViewModel userViewModel;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    public LoginFragment() {}
    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, "User is logged in: " + currentUser.getEmail());
                goToNextPage();
            }
        };

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
                                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                                firebaseAuth.signInWithCredential(firebaseCredential)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                Log.d(TAG, "Google sign in successful: " + user.getEmail());
                                                goToNextPage();
                                            } else {
                                                Log.w(TAG, "Google sign in failed", task.getException());
                                                Snackbar.make(requireView(), R.string.error_unexpected, Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } catch (ApiException e) {
                            Log.e(TAG, "Google Sign-in failed", e);
                        }
                    }
                });
    }




    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_pw);
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_email_login);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    private void goToNextPage() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel.getLoggedUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                goToNextPage();
            }
        });

        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        Button loginButton = view.findViewById(R.id.loginButton);
        Button signupButton = view.findViewById(R.id.buttonNewAccount);
        Button forgotPasswordButton = view.findViewById(R.id.forgot_pw);

        loginButton.setOnClickListener(v -> {
            if (editTextEmail.getText() != null && isEmailOk(editTextEmail.getText().toString())) {
                if (editTextPassword.getText() != null && isPasswordOk(editTextPassword.getText().toString())) {
                    userViewModel.getUserMutableLiveData(editTextEmail.getText().toString(),
                                    editTextPassword.getText().toString(), true)
                            .observe(getViewLifecycleOwner(), authenticationResult -> {
                                if (authenticationResult.isSuccess()) {
                                    goToNextPage();
                                } else {
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) authenticationResult).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    editTextPassword.setError(getString(R.string.error_pw));
                }
            } else {
                editTextEmail.setError(getString(R.string.error_email_login));
            }
        });

        Button googleLoginButton = view.findViewById(R.id.google_btn);
        googleLoginButton.setOnClickListener(v -> {
            oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(requireActivity(), result -> {
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    })
                    .addOnFailureListener(requireActivity(), e -> {
                        Log.e(TAG, "Google Sign-in failed: " + e.getLocalizedMessage());
                        Snackbar.make(view, R.string.error_unexpected, Snackbar.LENGTH_SHORT).show();
                    });
        });

        signupButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signupFragment);
        });

        forgotPasswordButton.setOnClickListener(v -> {
            if (editTextEmail.getText() != null && isEmailOk(editTextEmail.getText().toString())) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(editTextEmail.getText().toString())
                        .addOnSuccessListener(aVoid -> {
                            Snackbar.make(view, R.string.password_reset_email_sent, Snackbar.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Snackbar.make(view, R.string.error_sending_password_reset, Snackbar.LENGTH_SHORT).show();
                        });
            } else {
                editTextEmail.setError(getString(R.string.error_email_login));
            }
        });
    }




    private boolean isEmailOk(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordOk(String password) {
        return !password.isEmpty();
    }


}