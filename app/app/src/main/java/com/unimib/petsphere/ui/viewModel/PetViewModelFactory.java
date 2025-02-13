package com.unimib.petsphere.ui.viewModel;
//Author: Alessia Mazzeo
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.petsphere.data.repository.PetRepository;
import com.unimib.petsphere.util.ServiceLocator;


public class PetViewModelFactory implements ViewModelProvider.Factory {

    private final PetRepository petRepository;

    public PetViewModelFactory(Application app) {
        this.petRepository = ServiceLocator.getInstance().getPetsRepository(app);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PetViewModel(petRepository);
    }
}
