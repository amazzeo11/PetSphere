package com.unimib.petsphere.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimib.petsphere.R;


public class PetsListFragment extends Fragment {


    public PetsListFragment() {
        // Required empty public constructor
    }


    public static PetsListFragment newInstance() {
        PetsListFragment fragment = new PetsListFragment();
        Bundle args = new Bundle();


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
        return inflater.inflate(R.layout.fragment_pets_list, container, false);
    }
}