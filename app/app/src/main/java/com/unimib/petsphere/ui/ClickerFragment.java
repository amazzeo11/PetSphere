package com.unimib.petsphere.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimib.petsphere.R;


public class ClickerFragment extends Fragment {




    public ClickerFragment() {
        // Required empty public constructor
    }

    public static ClickerFragment newInstance(String param1, String param2) {
        ClickerFragment fragment = new ClickerFragment();

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
        return inflater.inflate(R.layout.fragment_clicker, container, false);
    }
}