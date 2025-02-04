package com.unimib.petsphere.ui;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.petsphere.R;
import com.unimib.petsphere.model.Result;
import com.unimib.petsphere.repository.user.IUserRepository;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.UserViewModel;
import com.unimib.petsphere.viewModel.UserViewModelFactory;

public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getName();

    private Button loginButton, signUpButton, loginGoogleButton;
    private EditText textInputEmail, textInputPassword;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    //risultato activity: account google che sto cercando; intent perché dialoga con s.o.
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;
    private UserViewModel userViewModel;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    //da doc, aggiunto controllo su utente, se già loggato va alla main page
    @Override
    public void onStart() {
        super.onStart();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)
        ).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // per ottenere l'utente corrnete
        FirebaseUser user = mAuth.getCurrentUser();
        Log.i(TAG, user + "");
        if (user != null) {
            goToMainPage();
        }

        //userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        loginButton = view.findViewById(R.id.loginButton);
        signUpButton = view.findViewById(R.id.signUpButton);
        textInputEmail = view.findViewById(R.id.textInputEmail);
        textInputPassword = view.findViewById(R.id.textInputPassword);

        View navHost = requireActivity().findViewById(R.id.nav_host_fragment);
        if (navHost == null) {
            Log.e("LoginFragment", "lil debug per nav host");
        }

        loginButton.setOnClickListener(v -> {
            String email = textInputEmail.getText().toString();
            String password = textInputPassword.getText().toString();
            userViewModel.loginWithEmailAndPassword(email, password);
        });

        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
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
    }
}