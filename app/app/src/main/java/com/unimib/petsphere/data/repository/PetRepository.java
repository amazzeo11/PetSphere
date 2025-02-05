package com.unimib.petsphere.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.unimib.petsphere.data.BasePetDataSource;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.model.PetResponseModel;
import com.unimib.petsphere.data.model.Result;

import java.util.ArrayList;
import java.util.List;


public class PetRepository implements PetResponseCallback , IPetRepository{

    private static final String TAG = PetRepository.class.getSimpleName();

    private final MutableLiveData<Result> allPetsMutableLiveData;
    private final MutableLiveData<Result> petsMutableLiveData;
    private final BasePetDataSource petDataSource;
    private MutableLiveData<String> deleteMsg;

    public PetRepository(BasePetDataSource petDataSource) {

        allPetsMutableLiveData = new MutableLiveData<>();
        petsMutableLiveData = new MutableLiveData<>();
        deleteMsg = new MutableLiveData<>();
        this.petDataSource = petDataSource;
        this.petDataSource.setArticleCallback(this);
    }

    public MutableLiveData<Result> fetchPets() {
            petDataSource.getPets();
        return allPetsMutableLiveData;
    }

    public MutableLiveData<Result> getPets() {
        petDataSource.getPets();
        return petsMutableLiveData;
    }

    public void updatePet(PetModel pet) {
        petDataSource.updatePet(pet);
    }

    public void deletePet(PetModel pet) {
        petDataSource.deletePet(pet);
    }


    public void onSuccess(List<PetModel> petList) {
        Result.PetSuccess result = new Result.PetSuccess(new PetResponseModel(petList));
        allPetsMutableLiveData.postValue(result);
    }

    @Override
    public void getAllPet(List<PetModel> petlist) {

    }

    @Override
    public void getPets(PetModel pet) {

    }


    public void onFailure(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allPetsMutableLiveData.postValue(resultError);
        petsMutableLiveData.postValue(resultError);
    }


    @Override
    public void insertPet(PetModel pet) {
        List<PetModel> pets = new ArrayList<PetModel>();
        pets.add(pet);
        petDataSource.insertPets(pets);
    }

    @Override
    public void onDeletePetSuccess() {
    String deletemessage = "Pet eliminato con successo";
        deleteMsg.postValue(deletemessage);
    }
    public MutableLiveData<String> getDeleteMsg() {
        return deleteMsg;
    }

}