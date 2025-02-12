package com.unimib.petsphere.util;
//Author: Alessia Mazzeo

import android.app.Application;


import com.unimib.petsphere.data.source.BasePetDataSource;
import com.unimib.petsphere.data.source.PetDataSource;
import com.unimib.petsphere.data.database.PetRoomDatabase;
import com.unimib.petsphere.data.repository.CatFactRepository;
import com.unimib.petsphere.data.repository.DogFactRepository;
import com.unimib.petsphere.data.repository.PetRepository;
import com.unimib.petsphere.data.repository.UserRepository;
import com.unimib.petsphere.data.source.BaseUserAuthenticationRemoteDataSource;
import com.unimib.petsphere.data.source.UserAuthenticationFirebaseDataSource;

import okhttp3.OkHttpClient;
import okhttp3.Request;



public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}


    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .build();
                return chain.proceed(request);
            })
            .build();



    public PetRoomDatabase getNewsDao(Application application) {
        return PetRoomDatabase.getDatabase(application);
    }


    public PetRepository getPetsRepository(Application application, boolean debugMode) {
        BasePetDataSource petsDataSource;

        petsDataSource = new PetDataSource(getNewsDao(application));

        return new PetRepository(petsDataSource);
    }


    public DogFactRepository getDogFactRepository(Application application, boolean debugMode) {
        return new DogFactRepository();
    }

    public CatFactRepository getCatFactRepository(Application application, boolean debugMode) {
        return new CatFactRepository();
    }
    public UserRepository getUserRepository(Application application) {

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationFirebaseDataSource();


        return new UserRepository(userRemoteAuthenticationDataSource);
    }
}