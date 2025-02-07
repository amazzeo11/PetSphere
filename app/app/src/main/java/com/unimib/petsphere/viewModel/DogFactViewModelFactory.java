package com.unimib.petsphere.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.unimib.petsphere.data.repository.DogFactRepository;


public class DogFactViewModelFactory implements ViewModelProvider.Factory {

    private final DogFactRepository dogFactRepository;

    public DogFactViewModelFactory(DogFactRepository dogFactRepository) {
        this.dogFactRepository = dogFactRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DogFactViewModel(dogFactRepository);
    }
}
