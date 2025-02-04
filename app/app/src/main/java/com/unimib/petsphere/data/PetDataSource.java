package com.unimib.petsphere.data;

import static com.unimib.petsphere.util.constants.UNEXPECTED_ERROR;

import com.unimib.petsphere.data.database.PetDAO;
import com.unimib.petsphere.data.database.PetRoomDatabase;
import com.unimib.petsphere.data.model.PetModel;


import java.util.List;


public class PetDataSource extends BasePetDataSource {

    private final PetDAO petDAO;

    public PetDataSource(PetRoomDatabase petRoomDatabase) {
        this.petDAO = petRoomDatabase.PetDAO();
    }


    @Override
    public void getPets() {
        PetRoomDatabase.databaseWriteExecutor.execute(() -> {
          petCallback.onSuccess(petDAO.getAll());
        });
    }


    @Override
    public void updatePet(PetModel pet) {
        PetRoomDatabase.databaseWriteExecutor.execute(() -> {
            int rowUpdatedCounter = petDAO.updatePet(pet);


            if (rowUpdatedCounter == 1) {
                PetModel updatepet = petDAO.getPet(pet.getUid());
            } else {
                petCallback.onFailure(new Exception(UNEXPECTED_ERROR));
            }
        });
    }



    public void deletePet(PetModel pet) {
        PetRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                int deletedRows = petDAO.deletePet(pet);

                if (deletedRows > 0) {
                    petCallback.onDeletePetSuccess();
                } else {
                    petCallback.onFailure(new Exception("Errore: eliminazione non riuscita"));
                }
            } catch (Exception e) {
               petCallback.onFailure(e);
            }
        });
    }


    @Override
    public void insertPets(List<PetModel> petList) {
        PetRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<PetModel> allPets = petDAO.getAll();
            if (petList != null) {
                for (PetModel pet : allPets) {

                    if (petList.contains(pet)) {
                        petList.set(petList.indexOf(pet), pet);
                    }
                }
                List<Long> insertedNewsIds = petDAO.insertPetList(petList);
                for (int i = 0; i < petList.size(); i++) {
                    petList.get(i).setUid(insertedNewsIds.get(i));
                }
                petCallback.onSuccess(petList);
            }
        });
    }
}
