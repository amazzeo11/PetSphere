package com.unimib.petsphere.data.repository;


import com.unimib.petsphere.data.model.PetModel;

import java.util.List;

public interface IPetRepository {
    void getAllPet(List<PetModel> petlist);
    void getPet(PetModel pet);
    void onFailure(Exception exception);
    void deletePet(List<PetModel> petlist);
    void insertPet(List<PetModel> petlist);
    void updatePet(PetModel pet);
}