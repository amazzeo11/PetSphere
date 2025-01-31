package com.unimib.petsphere.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.petsphere.R;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getName();

    private Button loginButton, signUpButton, loginGoogleButton;
    private EditText textInputEmail, textInputPassword;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    //risultato activity: account google che sto cercando; intent perché dialoga con s.o.
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    //da doc
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        oneTapClient = Identity.getSignInClient(requireActivity());
        //da doc
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        //come da lezione, richiesta con lambda
        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "result.getResultCode() == Activity.RESULT_OK");
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // prendo un ID token da Google lo uso per autenticazione con Firebase
                        /*userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {
                            if (authenticationResult.isSuccess()) {
                                User user = ((Result.UserSuccess) authenticationResult).getData();
                                //saveLoginData(user.getEmail(), null, user.getIdToken());
                                Log.i(TAG, "Logged as: " + user.getEmail());
                                userViewModel.setAuthenticationError(false);
                                retrieveUserInformationAndStartActivity(user, getView());
                            } else {
                                userViewModel.setAuthenticationError(true);
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) authenticationResult).getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (ApiException e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.error_unexpected),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });*/
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

        loginButton = view.findViewById(R.id.loginButton);
        signUpButton = view.findViewById(R.id.signUpButton);
        textInputEmail = view.findViewById(R.id.textInputEmail);
        textInputPassword = view.findViewById(R.id.textInputPassword);

        loginButton.setOnClickListener(v -> {
            mAuth.signInWithEmailAndPassword(textInputEmail.getText().toString(), textInputPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);

                                Intent intent = new Intent(requireActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                //NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                                //navController.navigate(R.id.action_loginFragment_to_mainActivity);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(requireContext(), "Autenticazione fallita",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
            /*
            if (true) {
            } else {
                // messaggio di errore
                Snackbar.make(view, "Autenticazione fallita, controlla mail e password inserite", Snackbar.LENGTH_SHORT).show();
            }*/
        });

        View navHost = requireActivity().findViewById(R.id.nav_host_fragment);
        if (navHost == null) {
            Log.e("LoginFragment", "lil debug per nav host");
        }

        signUpButton.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_loginFragment_to_signUpFragment);

                //Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signUpFragment);
            } catch (Exception e) {
                Log.e("LoginFragment", "Errore nella navigazione al SignUpFragment", e);
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // per ottenere l'utente corrnete
        FirebaseUser user = mAuth.getCurrentUser();

        Log.i(TAG, user + "");
/*
        loginGoogleButton.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());

                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                requireActivity().getString(R.string.error_unexpected),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }));*/
    }



    /*
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
    */
}