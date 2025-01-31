package com.unimib.petsphere.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.unimib.petsphere.data.model.PetModel;

@Database(entities = {PetModel.class}, version = 1)
public abstract class PetDatabase extends RoomDatabase {
    public abstract PetDAO PetDAO();
}
