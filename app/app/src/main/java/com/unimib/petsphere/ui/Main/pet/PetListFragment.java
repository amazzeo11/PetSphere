package com.unimib.petsphere.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.unimib.petsphere.R;
import com.unimib.petsphere.data.PetRoomDatabase;
import com.unimib.petsphere.data.model.PetModel;

import java.util.List;


public class PetListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public PetListFragment() {
    }

    public static PetListFragment newInstance(int columnCount) {
        PetListFragment fragment = new PetListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pets_list, container, false);
        View recycler= view.findViewById(R.id.list);


        Button newPetButton = view.findViewById(R.id.new_pet_btn);
        newPetButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePetActivity.class);
            startActivity(intent);
        });

        List<PetModel> petList = PetRoomDatabase.getDatabase(getContext()).PetDAO().getAll();

        if (recycler instanceof RecyclerView) {
            Context context = recycler.getContext();
            RecyclerView recyclerView = (RecyclerView) recycler;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new PetRecyclerViewAdapter(R.layout.preview_pet_card,petList));
        }
        return view;
    }
}