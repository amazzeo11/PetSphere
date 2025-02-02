package com.unimib.petsphere.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.unimib.petsphere.data.BasePetDataSource;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.model.PetResponseModel;
import com.unimib.petsphere.data.model.Result;

import java.util.List;


public class PetRepository implements PetResponseCallback {

    private static final String TAG = PetRepository.class.getSimpleName();

    private final MutableLiveData<Result> allPetsMutableLiveData;
    private final MutableLiveData<Result> petsMutableLiveData;
    private final BasePetDataSource petDataSource;

    public PetRepository(BasePetDataSource petDataSource) {

        allPetsMutableLiveData = new MutableLiveData<>();
        petsMutableLiveData = new MutableLiveData<>();
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

    public void updateArticle(PetModel pet) {
        petDataSource.updatePet(pet);
    }

    public void deletePet() {
        petDataSource.deletePet();
    }


    public void onSuccess(List<PetModel> petList) {
        Result.PetSuccess result = new Result.PetSuccess(new PetResponseModel(petList));
        allPetsMutableLiveData.postValue(result);
    }

    public void onFailure(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allPetsMutableLiveData.postValue(resultError);
        petsMutableLiveData.postValue(resultError);
    }



    public void onDeletePetSuccess(List<PetModel> petList) {
        Result allNewsResult = allPetsMutableLiveData.getValue();

        if (allNewsResult != null && allNewsResult.isSuccess()) {
            List<PetModel> oldPets = ((Result.PetSuccess)allNewsResult).getData().getPets();
            for (PetModel pet : petList) {
                if (oldPets.contains(pet)) {
                    oldPets.set(oldPets.indexOf(pet), pet);
                }
            }
            allPetsMutableLiveData.postValue(allNewsResult);
        }

        if (petsMutableLiveData.getValue() != null &&
                petsMutableLiveData.getValue().isSuccess()) {
            petList.clear();
            Result.PetSuccess result = new Result.PetSuccess(new PetResponseModel(petList));
            petsMutableLiveData.postValue(result);
        }
    }
}