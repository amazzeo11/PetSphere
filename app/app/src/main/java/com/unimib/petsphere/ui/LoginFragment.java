package com.unimib.petsphere.ui;

import static com.unimib.petsphere.util.Constants.INVALID_CREDENTIALS_ERROR;
import static com.unimib.petsphere.util.Constants.INVALID_USER_ERROR;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.petsphere.R;
import com.unimib.petsphere.model.Result;
import com.unimib.petsphere.model.User;
import com.unimib.petsphere.repository.user.IUserRepository;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.UserViewModel;
import com.unimib.petsphere.viewModel.UserViewModelFactory;

public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getName();

    private Button loginButton, signUpButton, googleLoginButton;
    private EditText textInputEmail, textInputPassword;

    private static final int RC_SIGN_IN = 9001;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;

    //risultato activity: account google che sto cercando; intent perché dialoga con s.o.
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;
    private UserViewModel userViewModel;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        // effettiva richiesta
        oneTapClient = Identity.getSignInClient(requireContext());
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
            // Automatically sign in when exactly one credential is retrieved: provo a false per lasciar scegliere al'utente
            .setAutoSelectEnabled(false)
            .build();

        // gestisce risultato intent
        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();
        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "result.getResultCode() == Activity.RESULT_OK");
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    Log.d(TAG, "l'idToken è:" + idToken);
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate with Firebase.
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {
                            if (authenticationResult.isSuccess()) {
                                User user = ((Result.UserSuccess) authenticationResult).getData();
                                //FirebaseUser user = mAuth.getCurrentUser();
                                //saveLoginData(user.getEmail(), user.getPassword(), user.getUid());
                                Log.i(TAG, "Logged as: " + user.getEmail());
                                userViewModel.setAuthenticationError(false);
                                goToMainPage();
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
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("LoginFragment", "onViewCreated chiamato");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // per ottenere l'utente corrnete
        FirebaseUser user = mAuth.getCurrentUser();
        Log.i(TAG, user + "");
        if (user != null) {
            goToMainPage();
        }

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        Log.d("LoginFragment", "Before navHost check");
        View navHost = requireActivity().findViewById(R.id.nav_host_fragment);
        if (navHost == null) {
            Log.e("LoginFragment", "lil debug per nav host, null");
        } else {
            Log.e("LoginFragment", "lil debug per nav host, NON null");
        }

        loginButton = view.findViewById(R.id.loginButton);
        signUpButton = view.findViewById(R.id.signUpButton);
        textInputEmail = view.findViewById(R.id.textInputEmail);
        textInputPassword = view.findViewById(R.id.textInputPassword);

        loginButton.setOnClickListener(v -> {
            String email = textInputEmail.getText().toString();
            String password = textInputPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        "Email e password non validi",
                        Snackbar.LENGTH_SHORT).show();
                return;
            }
            userViewModel.loginWithEmailAndPassword(email, password);
        });

        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    User utente = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getUid());
                    userViewModel.saveUser(utente);
                }
                goToMainPage();
            } else {
                String errorMessage = ((Result.Error) result).getMessage();
                Toast.makeText(requireContext(), "Errore: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        signUpButton.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_loginFragment_to_signUpFragment);
            } catch (Exception e) {
                Log.e("LoginFragment", "Errore nella navigazione al SignUpFragment", e);
            }
        });

        googleLoginButton = view.findViewById(R.id.googleLoginButton);
        if (googleLoginButton == null) {
            Log.e("LoginFragment", "googleLoginButton è null");
        } else {
            Log.e("LoginFragment", "googleLoginButton non è null");
        }
        googleLoginButton.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                        //goToMainPage();  spostato dentro activityResultLauncher
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
        }));
        /*userViewModel.getGoogleUserMutableLiveData(token).observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()) {
                Log.d(TAG, "Utente autenticato e registrato con successo");
                goToMainPage();
            } else {
                String errorMessage = ((Result.Error) result).getMessage();
                Toast.makeText(requireContext(), "Errore: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });*/
/*
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // L'utente è loggato, possiamo procedere
            String email = currentUser.getEmail();

            if (email != null && !email.isEmpty()) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email inviata");
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            "Controlla la tua email",
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Log.e(TAG, "Errore: l'email dell'utente è null");
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        "Errore: l'email è assente", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Errore: nessun utente loggato");
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    "Devi essere loggato per recuperare la password", Snackbar.LENGTH_SHORT).show();
        }

        // reset password dimenticata
        mAuth.sendPasswordResetEmail(textInputEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email inviata");
                            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                    "Controlla la tua email",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });*/
    }

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_password_login);
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_email_login);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }

    //da doc, aggiunto controllo su utente, se già loggato va alla main page
    @Override
    public void onStart() {
        super.onStart();

        if (BuildConfig.DEBUG) {
            FirebaseAuth.getInstance().signOut();
        } else {
            // Solo in modalità release, stampa nei log se l'utente è già loggato
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Log.d("DEBUG", "Utente loggato: " + user.getEmail());
            } else {
                Log.d("DEBUG", "Nessun utente loggato");
            }
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Se l'utente è loggato, carica i dati utente tramite ViewModel
            userViewModel.getUser(currentUser.getEmail(), "", true);
            // naviga direttamente alla schermata successiva
            goToMainPage();
        }
    }

    private void goToMainPage() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // chiudo il login così che l'utente non possa tornarci andando indietro
        requireActivity().finish();
    }
    /*
    private void goToSignUpPage() {
        NavController navController = NavHostFragment.findNavController(LoginFragment.this);
        Log.d("NAVIGATION", "Current destination: " + navController.getCurrentDestination().getId());
        navController.navigate(R.id.action_loginFragment_to_signUpFragment);
    }
    */
}