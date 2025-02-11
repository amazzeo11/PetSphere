package com.unimib.petsphere.data.repository;
//Author: Alessia Mazzeo

import com.unimib.petsphere.data.model.PetModel;

import java.util.List;

public interface PetResponseCallback {
    void onSuccess(List<PetModel> petList);
    void onFailure(Exception exception);
    void onDeletePetSuccess();
}