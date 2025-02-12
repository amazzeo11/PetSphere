package com.unimib.petsphere.viewModel;
//Author: Alessia Mazzeo

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.petsphere.data.repository.CatFactRepository;
import com.unimib.petsphere.util.ServiceLocator;

public class CatFactViewModelFactory implements ViewModelProvider.Factory {

    private final CatFactRepository catFactRepository;

    public CatFactViewModelFactory(Application app) {
        this.catFactRepository = ServiceLocator.getInstance().getCatFactRepository(app);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CatFactViewModel(catFactRepository);
    }
}
