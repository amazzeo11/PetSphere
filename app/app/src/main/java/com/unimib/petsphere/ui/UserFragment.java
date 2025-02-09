// Author: Sara Angeretti

package com.unimib.petsphere.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.petsphere.R;
import com.unimib.petsphere.repository.user.IUserRepository;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.UserViewModel;
import com.unimib.petsphere.viewModel.UserViewModelFactory;

public class UserFragment extends Fragment {

    private UserViewModel userViewModel;
    private SignInClient oneTapClient;
    //private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth mAuth;

    private Button bottoneSignOut, bottoneModificaNomeUtente, bottoneModificaPassword, bottoneConfermaUserName, bottoneConfermaPassword;
    private EditText textUserName, textUserEmail, textUserPassword, textUserOldPassword;
    private TextInputLayout boxVecchiaPassword, boxNuovaPassword;
    private LinearLayout campoVecchiaPassword, campoNuovaPassword, campoPlaceholderPassword;
    private boolean isUpdatingProfile = false;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // bottone sign out
        bottoneSignOut = view.findViewById(R.id.bottoneSignOut);
        // tasto modifica nome utente
        bottoneModificaNomeUtente = view.findViewById(R.id.bottoneModificaNomeUtente);
        // tasto modifica password
        bottoneModificaPassword = view.findViewById(R.id.bottoneModificaPassword);
        // tasto conferma modifiche username
        bottoneConfermaUserName = view.findViewById(R.id.bottoneConfermaUserName);
        // tasto conferma modifiche username
        bottoneConfermaPassword = view.findViewById(R.id.bottoneConfermaPassword);
        // dati utente
        textUserName = view.findViewById(R.id.testoUserDatiNome);
        textUserEmail = view.findViewById(R.id.testoUserDatiEmail);
        textUserPassword = view.findViewById(R.id.testoUserDatiPassword);
        textUserOldPassword = view.findViewById(R.id.testoUserVecchiaPassword);
        boxVecchiaPassword = view.findViewById(R.id.boxUserVecchiaPassword);
        boxNuovaPassword = view.findViewById(R.id.boxUserDatiPassword);
        campoVecchiaPassword = view.findViewById(R.id.layoutUserOldPassword);
        campoNuovaPassword = view.findViewById(R.id.layoutUserPassword);
        campoPlaceholderPassword = view.findViewById(R.id.layoutUserPlaceholderPassword);

        // sign out
        bottoneSignOut.setOnClickListener(v -> signOut());

        // modifica password: osservo il ViewMOdel usando il ViewModelFactory
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        // inizializzo
        textUserName.setEnabled(false);
        textUserEmail.setEnabled(false);
        textUserPassword.setEnabled(false);
        bottoneConfermaUserName.setVisibility(View.GONE);
        bottoneConfermaPassword.setVisibility(View.GONE);
        campoVecchiaPassword.setVisibility(View.GONE);
        campoNuovaPassword.setVisibility(View.GONE);
        campoPlaceholderPassword.setVisibility(View.VISIBLE);
        // inizializzo toggle password nascosto
        boxVecchiaPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
        boxNuovaPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);

        // TextWatcher per l'icona della password
        TextWatcher passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // campo vecchia password
                if (textUserOldPassword.hasFocus()) {
                    if (s.length() > 0) { // così compare appena l'utente pigia qualche tasto
                        boxVecchiaPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                    } else {
                        boxVecchiaPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    }
                }
                // campo nuova password
                else if (textUserPassword.hasFocus()) {
                    if (s.length() > 0) {
                        boxNuovaPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                    } else {
                        boxNuovaPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // metto un listener per quando le password perdono il focus così sparisce il toggle
        textUserOldPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                boxVecchiaPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
            }
        });
        textUserPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                boxNuovaPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
            }
        });

        // NOME UTENTE
        // abilito modifica nome utente:
        bottoneModificaNomeUtente.setOnClickListener(view0 -> {
            isUpdatingProfile = true;
            textUserName.setEnabled(true);
            textUserEmail.setEnabled(false);
            textUserOldPassword.setEnabled(false);
            textUserPassword.setEnabled(false);
            bottoneModificaPassword.setEnabled(false);
            bottoneModificaNomeUtente.setEnabled(false);
            bottoneModificaPassword.setVisibility(View.GONE);
            bottoneModificaNomeUtente.setVisibility(View.GONE);
            bottoneConfermaUserName.setVisibility(View.VISIBLE);
            bottoneConfermaPassword.setVisibility(View.GONE);
        });

        // disabilito modifica nome utente:
        bottoneConfermaUserName.setOnClickListener(view1 -> {
            isUpdatingProfile = false;
            textUserName.setEnabled(false);
            textUserEmail.setEnabled(false);
            textUserOldPassword.setEnabled(false);
            textUserPassword.setEnabled(false);
            bottoneModificaPassword.setEnabled(true);
            bottoneModificaNomeUtente.setEnabled(true);
            bottoneModificaNomeUtente.setVisibility(View.VISIBLE);
            bottoneModificaPassword.setVisibility(View.VISIBLE);
            bottoneConfermaUserName.setVisibility(View.GONE);
            bottoneConfermaPassword.setVisibility(View.GONE);

            // nuovi dati inseriti dall'utente
            String newUserName = textUserName.getText().toString().trim();

            if (!(newUserName.isEmpty())) {
                userViewModel.updateUserName(newUserName);
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        requireActivity().getString(R.string.error_update_username),
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        userViewModel.isUserNameUpdated().observe(getViewLifecycleOwner(), isNameUpdated -> {
            if (isNameUpdated != null) {
                if (isNameUpdated) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            "Nome utente aggiornato con successo!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            "Errore durante l'aggiornamento del nome utente.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        // PASSWORD
        // abilito modifica password:
        bottoneModificaPassword.setOnClickListener(view2 -> {
            isUpdatingProfile = true;
            textUserName.setEnabled(false);
            textUserEmail.setEnabled(false);
            textUserPassword.setEnabled(true);
            textUserOldPassword.setEnabled(true);
            bottoneModificaPassword.setEnabled(false);
            bottoneModificaNomeUtente.setEnabled(false);
            bottoneModificaPassword.setVisibility(View.GONE);
            bottoneModificaNomeUtente.setVisibility(View.GONE);
            campoVecchiaPassword.setVisibility(View.VISIBLE);
            campoNuovaPassword.setVisibility(View.VISIBLE);
            campoPlaceholderPassword.setVisibility(View.GONE);
            bottoneConfermaUserName.setVisibility(View.GONE);
            bottoneConfermaPassword.setVisibility(View.VISIBLE);

            // aggiungo TextWatcher per password
            textUserPassword.addTextChangedListener(passwordTextWatcher);
            textUserOldPassword.addTextChangedListener(passwordTextWatcher);
        });

        // disabilito modifica password:
        bottoneConfermaPassword.setOnClickListener(view3 -> {
            isUpdatingProfile = false;
            textUserName.setEnabled(false);
            textUserEmail.setEnabled(false);
            textUserPassword.setEnabled(false);
            textUserOldPassword.setEnabled(false);
            bottoneModificaPassword.setEnabled(true);
            bottoneModificaNomeUtente.setEnabled(true);
            bottoneModificaNomeUtente.setVisibility(View.VISIBLE);
            bottoneModificaPassword.setVisibility(View.VISIBLE);
            campoVecchiaPassword.setVisibility(View.GONE);
            campoNuovaPassword.setVisibility(View.GONE);
            campoPlaceholderPassword.setVisibility(View.VISIBLE);
            bottoneModificaNomeUtente.setVisibility(View.VISIBLE);
            bottoneModificaPassword.setVisibility(View.VISIBLE);
            bottoneConfermaUserName.setVisibility(View.GONE);
            bottoneConfermaPassword.setVisibility(View.GONE);

            // rimuovo TextWatcher
            textUserPassword.removeTextChangedListener(passwordTextWatcher);
            textUserOldPassword.removeTextChangedListener(passwordTextWatcher);

            // nuovi dati inseriti dall'utente
            String newPassword = textUserPassword.getText().toString().trim();
            String oldPassword = textUserOldPassword.getText().toString().trim();

            // l'utente si deve re-autenticare per cambiare la password
            if (!(oldPassword.isEmpty()) && !(newPassword.isEmpty())) {
                userViewModel.updatePassword(oldPassword, newPassword);
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        requireActivity().getString(R.string.error_empty_fields),
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        userViewModel.isPasswordUpdated().observe(getViewLifecycleOwner(), isUpdated -> {
            if (isUpdated != null) {
                if (isUpdated) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            "Password aggiornata con successo!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            "Errore durante l'aggiornamento della password.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        userViewModel.getPasswordUpdateError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            errorMessage, Snackbar.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        textUserName = view.findViewById(R.id.testoUserDatiNome);
        textUserEmail = view.findViewById(R.id.testoUserDatiEmail);
        textUserPassword = view.findViewById(R.id.testoUserDatiPassword);

        if (currentUser != null) {
            userViewModel.getUserLiveData(currentUser.getUid()).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    String userName = user.getUserName();
                    String userEmail = user.getEmail();

                    // dati utente come placeholder
                    if (textUserName.getText().toString().isEmpty()) {
                        textUserName.setText(userName);
                    }
                    if (textUserEmail.getText().toString().isEmpty()) {
                        textUserEmail.setText(userEmail);
                    }
                    textUserPassword.setText(getString(R.string.placeholder_vuoto));
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // recupero i dati dal UserViewModel
        String uid = mAuth.getInstance().getCurrentUser().getUid();
        userViewModel.getUserLiveData(uid).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                isUpdatingProfile = false;

                textUserName.setText(user.getUserName());
                textUserEmail.setText(user.getEmail());
                textUserPassword.setText(getString(R.string.placeholder_vuoto));

                // disabilito la modifica di default
                textUserName.setEnabled(false);
                textUserEmail.setEnabled(false);
                textUserPassword.setEnabled(false);
                textUserOldPassword.setEnabled(false);
                bottoneModificaPassword.setEnabled(true);
                bottoneModificaNomeUtente.setEnabled(true);
                bottoneModificaPassword.setVisibility(View.VISIBLE);
                bottoneModificaNomeUtente.setVisibility(View.VISIBLE);
                campoVecchiaPassword.setVisibility(View.GONE);
                campoNuovaPassword.setVisibility(View.GONE);
                campoPlaceholderPassword.setVisibility(View.VISIBLE);
                bottoneConfermaUserName.setVisibility(View.GONE);
                bottoneConfermaPassword.setVisibility(View.GONE);
            }
        });
    }

    public void signOut() {
        // faccio il logout da firebase auth
        mAuth.signOut();
        // faccio tornare al LoginFragment nella WelcomeActivity
        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        startActivity(intent);
        // chiudo l'acitvity corrente così l'utente non può tornare indietro col tasto back del telefono
        getActivity().finish();
    }

}
