package com.unimib.petsphere.ui.Main.petList;
//Author: Alessia Mazzeo
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.ui.Main.petList.pet.CreatePetActivity;
import com.unimib.petsphere.ui.Main.petList.pet.ViewPetActivity;
import com.unimib.petsphere.ui.viewModel.PetViewModel;
import com.unimib.petsphere.ui.viewModel.PetViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class PetListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private List<PetModel> petList;
    private PetRecyclerViewAdapter adapter;
    private PetViewModel petViewModel;

    public PetListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        petViewModel = new ViewModelProvider(
                requireActivity(),
                new PetViewModelFactory(this.getActivity().getApplication())).get(PetViewModel.class);

        petList = new ArrayList<>();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pets_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.list);
        Button newPetButton = view.findViewById(R.id.new_pet_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        adapter = new PetRecyclerViewAdapter(R.layout.preview_pet_card, petList, pet -> {
            Intent intent = new Intent(getActivity(), ViewPetActivity.class);
            intent.putExtra("pet", pet);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        petViewModel.getPets().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                petList.clear();
                petList.addAll(((Result.PetSuccess) result).getData().getPets());
                adapter.notifyDataSetChanged();
            } else {
                Snackbar.make(view, "Errore nel caricamento", Snackbar.LENGTH_SHORT).show();
            }
        });

        newPetButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePetActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        petViewModel.refreshPets();
    }


}
