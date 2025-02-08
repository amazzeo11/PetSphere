package com.unimib.petsphere.viewModel;
//Author: Alessia Mazzeo

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.petsphere.data.repository.CatFactRepository;

public class CatFactViewModel extends ViewModel {
    private final CatFactRepository repository;
    private LiveData<String> catFact;

    public CatFactViewModel(CatFactRepository repository) {
        this.repository = repository;
        catFact = repository.getRandomCatFact();
    }

    public LiveData<String> getCatFact() {
        return catFact;
    }

    public void refreshFact() {
        catFact = repository.getRandomCatFact();
    }
}

