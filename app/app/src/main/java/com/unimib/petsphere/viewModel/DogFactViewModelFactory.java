package com.unimib.petsphere.viewModel;
//Author: Alessia Mazzeo
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.unimib.petsphere.data.repository.DogFactRepository;
import com.unimib.petsphere.util.ServiceLocator;


public class DogFactViewModelFactory implements ViewModelProvider.Factory {

    private final DogFactRepository dogFactRepository;

    public DogFactViewModelFactory(Application app) {
        this.dogFactRepository = ServiceLocator.getInstance().getDogFactRepository(app);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DogFactViewModel(dogFactRepository);
    }
}
