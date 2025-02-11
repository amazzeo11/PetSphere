package com.unimib.petsphere.data;
//Author: Alessia Mazzeo

import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.repository.PetResponseCallback;

import java.util.List;

public abstract class BasePetDataSource {
    protected PetResponseCallback petCallback;

    public void setArticleCallback(PetResponseCallback petCallback) {
        this.petCallback = petCallback;
    }

    public abstract void getPets();

    public abstract void updatePet(PetModel pet);

    public abstract void deletePet(PetModel pet);

    public abstract void insertPets(List<PetModel> petList);
}
