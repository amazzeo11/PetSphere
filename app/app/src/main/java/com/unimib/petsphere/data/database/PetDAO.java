package com.unimib.petsphere.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.unimib.petsphere.data.model.PetModel;

import java.util.List;

@Dao
public interface PetDAO {
    @Query("SELECT * FROM PetModel")
    List<PetModel> getAll();

    @Query("SELECT * FROM PetModel WHERE uid IN (:userIds)")
    List<PetModel> loadAllByIds(int[] userIds);

    @Insert
    void insertAll(PetModel... users);

    @Delete
    void delete(PetModel user);
}