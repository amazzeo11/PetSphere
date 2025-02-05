package com.unimib.petsphere.viewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.repository.PetRepository;
import com.unimib.petsphere.data.repository.PetResponseCallback;

import java.util.List;


public class PetViewModel extends ViewModel {

    private static final String TAG = PetViewModel.class.getSimpleName();

    private final PetRepository petRepository;

    private MutableLiveData<Result> petsListLiveData;
    private MutableLiveData<String> deleteMsg;

    public PetViewModel(PetRepository petRepository) {
        this.petRepository = petRepository;
        this.deleteMsg = petRepository.getDeleteMsg();
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

    public void deletePet(PetModel pet){
        petRepository.deletePet(pet);
    }


    public LiveData<String> getDeleteMsg() {
        return deleteMsg;
    }
}