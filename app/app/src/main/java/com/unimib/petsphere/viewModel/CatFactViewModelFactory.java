package com.unimib.petsphere.viewModel;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.petsphere.data.repository.CatFactRepository;

public class CatFactViewModelFactory implements ViewModelProvider.Factory {

    private final CatFactRepository catFactRepository;

    public CatFactViewModelFactory(CatFactRepository catFactRepository) {
        this.catFactRepository = catFactRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CatFactViewModel(catFactRepository);
    }
}
