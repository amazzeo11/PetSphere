package com.unimib.petsphere.ui.viewModel;
//Author: Alessia Mazzeo
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.petsphere.data.repository.DogFactRepository;

public class DogFactViewModel extends ViewModel {
    private final DogFactRepository repository;
    private LiveData<String> dogFact;

    public DogFactViewModel(DogFactRepository repository) {
        this.repository = repository;
        dogFact = repository.getRandomDogFact();
    }

    public LiveData<String> getDogFact() {
        return dogFact;
    }

    public void refreshFact() {
        dogFact = repository.getRandomDogFact();
    }
}
