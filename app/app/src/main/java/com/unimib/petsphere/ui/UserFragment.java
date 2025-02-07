package com.unimib.petsphere.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.petsphere.R;

public class UserFragment extends Fragment {

    private Button bottoneSignOut;
    private SignInClient oneTapClient;
    //private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth mAuth;

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
        bottoneSignOut.setOnClickListener(v -> signOut());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        //FirebaseUser user = mAuth.getInstance().getCurrentUser();
        if (user != null) {
            String nome = user.getDisplayName();
            String email = user.getEmail();
        }
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
