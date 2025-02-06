package com.unimib.petsphere.data.repository;


import com.unimib.petsphere.data.model.PetModel;

import java.util.List;

public interface IPetRepository {
    void deletePet(PetModel pet);
    void insertPet(PetModel pet);
    void updatePet(PetModel pet);
}