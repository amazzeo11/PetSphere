package com.unimib.petsphere.ui.welcome;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.unimib.petsphere.R;


public class WelcomeActivity extends AppCompatActivity {

    public static final String TAG = WelcomeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }
}