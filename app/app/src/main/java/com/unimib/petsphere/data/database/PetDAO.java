package com.unimib.petsphere.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.unimib.petsphere.data.model.PetModel;

import java.util.List;

@Dao
public interface PetDAO {
    @Query("SELECT * FROM PetModel")
    List<PetModel> getAll();
    @Query("SELECT * FROM PetModel WHERE uid = :id")
    PetModel getPet(long id);

    @Query("SELECT * FROM PetModel WHERE uid IN (:userIds)")
    List<PetModel> loadAllByIds(int[] userIds);

    @Insert
    void insertAll(PetModel... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PetModel... pets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertPetList(List<PetModel> petList);

    @Update
    int updatePet(PetModel pet);

    @Update
    int updatePetList(List<PetModel> petList);

    @Delete
    void delete(PetModel pet); //user?

    @Query("DELETE from PetModel")
    void deleteAll();
}