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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
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

    private Button bottoneSignOut, bottoneModificaProfilo, bottoneConfermaModifiche, bottoneModificaUtente;
    private ImageButton bottoneConferma;
    private EditText textUserName, textUserEmail, textUserPassword, textUserOldPassword;
    private LinearLayout campoVecchiaPassword;
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
        /*if (getArguments() != null) {

        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // bottone sign out
        bottoneSignOut = view.findViewById(R.id.bottoneSignOut);
        // tasto modifica profilo
        bottoneModificaUtente = view.findViewById(R.id.bottoneModificaDatiUtente);
        // tasto conferma modifiche
        bottoneConferma = view.findViewById(R.id.bottoneConfermaModifiche);
        // dati utente
        textUserName = view.findViewById(R.id.testoUserDatiNome);
        textUserEmail = view.findViewById(R.id.testoUserDatiEmail);
        textUserPassword = view.findViewById(R.id.testoUserDatiPassword);
        textUserOldPassword = view.findViewById(R.id.testoUserVecchiaPassword);
        campoVecchiaPassword = view.findViewById(R.id.layoutUserOldPassword);

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
        bottoneConferma.setVisibility(View.GONE);
        campoVecchiaPassword.setVisibility(View.GONE);

        // abilito modifica profilo:
        bottoneModificaUtente.setOnClickListener(view1 -> {
            isUpdatingProfile = true;
            textUserName.setEnabled(true);
            textUserEmail.setEnabled(false); // rimane così com'è
            textUserPassword.setEnabled(true);
            textUserOldPassword.setEnabled(true);
            campoVecchiaPassword.setVisibility(View.VISIBLE);
            bottoneConferma.setVisibility(View.VISIBLE);
        });

        // disabilito modifica profilo:
        bottoneConferma.setOnClickListener(view2 -> {
            textUserName.setEnabled(false);
            textUserEmail.setEnabled(false);
            textUserPassword.setEnabled(false);
            textUserOldPassword.setEnabled(false);
            campoVecchiaPassword.setVisibility(View.GONE);
            bottoneConferma.setVisibility(View.GONE);

            // nuovi dati inseriti dall'utente
            String newUserName = textUserName.getText().toString().trim();
            String newPassword = textUserPassword.getText().toString().trim();
            String oldPassword = textUserOldPassword.getText().toString().trim();

            if (!(newUserName.isEmpty())) {
                userViewModel.updateUserName(newUserName);
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        requireActivity().getString(R.string.error_update_username),
                        Snackbar.LENGTH_SHORT).show();
            }

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
                    textUserPassword.setText(getString(R.string.placeholder_password));
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
                textUserName.setText(user.getUserName());
                textUserEmail.setText(user.getEmail());
                textUserPassword.setText(getString(R.string.placeholder_password));

                // disabilito la modifica di default
                textUserName.setEnabled(false);
                textUserEmail.setEnabled(false);
                textUserPassword.setEnabled(false);
                textUserOldPassword.setEnabled(false);
                bottoneConferma.setVisibility(View.GONE);
                campoVecchiaPassword.setVisibility(View.GONE);
            }
        });
    }

    public void signOut() {
        // faccio il logout da firebase auth
        mAuth.signOut();
        // e da google one-tap, ma solo se già loggato così
        if (oneTapClient != null) {
            oneTapClient.signOut()
                    .addOnCompleteListener(task -> Log.d("Logout", "One Tap logout completato"));
        }
        // faccio tornare al LoginFragment nella WelcomeActivity
        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        startActivity(intent);
        // chiudo l'acitvity corrente così l'utente non può tornare indietro col tasto back del telefono
        getActivity().finish();
    }

}
