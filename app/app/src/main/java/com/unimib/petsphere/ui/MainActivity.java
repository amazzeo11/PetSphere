package com.unimib.petsphere.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.ui.AppBarConfiguration;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.unimib.petsphere.R;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.fragmentContainerView);

        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
       /* AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.clicker, R.id.petslistfragment, R.id.user
        ).build();*/

        NavigationUI.setupWithNavController(bottomNav, navController);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        FirebaseDatabase.getInstance().goOffline();
        FirebaseDatabase.getInstance().goOnline();

    }
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }
}