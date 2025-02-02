package com.unimib.petsphere.ui.Main.pet;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.unimib.petsphere.R;
import com.unimib.petsphere.data.database.PetRoomDatabase;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.repository.PetRepository;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.PetViewModel;
import com.unimib.petsphere.viewModel.PetViewModelFactory;

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

        PetRepository petRepository =
                ServiceLocator.getInstance().getPetsRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );


        petViewModel = new ViewModelProvider(
                requireActivity(),
                new PetViewModelFactory(petRepository)).get(PetViewModel.class);

       petList = new ArrayList<>();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pets_list, container, false);
        View recycler= view.findViewById(R.id.list);

        RecyclerView recyclerView = (RecyclerView) recycler;
        Button newPetButton = view.findViewById(R.id.new_pet_btn);
        newPetButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePetActivity.class);
            startActivity(intent);
        });

        List<PetModel> petList = PetRoomDatabase.getDatabase(getContext()).PetDAO().getAll();
        recyclerView.setLayoutManager(new LinearLayoutManager(recycler.getContext()));

        adapter =
                new PetRecyclerViewAdapter(R.layout.preview_pet_card,petList,  new PetRecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onPetItemClick(PetModel pet) {

                            }

                        });

        recyclerView.setAdapter(adapter);

        petViewModel.getPets().observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        int initialSize = this.petList.size();
                        this.petList.clear();
                        this.petList.addAll(((Result.PetSuccess) result).getData().getPets());
                        adapter.notifyItemRangeInserted(initialSize, this.petList.size());
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        Snackbar.make(view,
                                "error",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

        return view;
    }
}
