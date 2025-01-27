package com.unimib.petsphere.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.petsphere.R;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Trova il NavController
        navController = Navigation.findNavController(this, R.id.fragment_container);

        // Collega il NavController alla BottomNavigationView
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}