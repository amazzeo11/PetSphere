package com.unimib.petsphere.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.petsphere.data.repository.PetRepository;


public class PetViewModelFactory implements ViewModelProvider.Factory {

    private final PetRepository petRepository;

    public PetViewModelFactory(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PetViewModel(petRepository);
    }
}
