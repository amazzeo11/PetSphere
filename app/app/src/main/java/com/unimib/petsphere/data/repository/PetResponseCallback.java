package com.unimib.petsphere.data.repository;


import com.unimib.petsphere.data.model.PetModel;

import java.util.List;

public interface PetResponseCallback {


    void onSuccess(List<PetModel> petList);
    void onFailure(Exception exception);
    //void onNewsFavoriteStatusChanged(Article news, List<Article> favoriteNews);
   // void onNewsFavoriteStatusChanged(List<Article> news);
    void onDeletePetSuccess(List<PetModel> petList);
}