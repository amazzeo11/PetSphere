package com.unimib.petsphere.viewModel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.repository.PetRepository;


public class PetViewModel extends ViewModel {

    private static final String TAG = PetViewModel.class.getSimpleName();

    private final PetRepository petRepository;

    private MutableLiveData<Result> petsListLiveData;

    public PetViewModel(PetRepository petRepository) {
        this.petRepository = petRepository;

    }

    public MutableLiveData<Result> getPets() {
        if (petsListLiveData == null) {
            fetchPets();
        }
        return petsListLiveData;
    }



    public void updatePet(PetModel pet) {
        petRepository.updateArticle(pet);
    }

    private void fetchPets() {
        petsListLiveData = petRepository.fetchPets();
    }



}