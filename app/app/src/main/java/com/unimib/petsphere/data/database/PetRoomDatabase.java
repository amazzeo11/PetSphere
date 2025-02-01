package com.unimib.petsphere.data;

import static com.unimib.petsphere.util.constants.DATABASE_VERSION;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.util.constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PetModel.class}, version = DATABASE_VERSION, exportSchema = true)
public abstract class PetRoomDatabase extends RoomDatabase {

    public abstract PetDAO PetDAO();

    private static volatile PetRoomDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static PetRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PetRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                   PetRoomDatabase.class, constants.PET_DATABASE)
                            .allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}