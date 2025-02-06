package com.unimib.petsphere.viewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.repository.PetRepository;



public class PetViewModel extends ViewModel {

    private static final String TAG = PetViewModel.class.getSimpleName();

    private final PetRepository petRepository;

    private MutableLiveData<Result> petsListLiveData;
    private MutableLiveData<String> deleteMsg;
    private MutableLiveData<String> createPetMsg = new MutableLiveData<>();

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
        petRepository.updatePet(pet);
    }

    private void fetchPets() {
        petsListLiveData = petRepository.fetchPets();
    }

    public void deletePet(PetModel pet){
        petRepository.deletePet(pet);
    }

    public void createPet(PetModel pet) {
        petRepository.insertPet(pet);
        createPetMsg.setValue("Pet creato con successo!");
    }

    public LiveData<String> getCreatePetMsg() {
        return createPetMsg;
    }

    public LiveData<String> getDeleteMsg() {
        return deleteMsg;
    }
}