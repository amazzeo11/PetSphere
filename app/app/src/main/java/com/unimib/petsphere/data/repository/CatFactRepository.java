package com.unimib.petsphere.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.petsphere.data.model.CatFact;
import com.unimib.petsphere.service.CatApiService;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatFactRepository {
    private static final String BASE_URL = "https://meowfacts.herokuapp.com/";
    private final CatApiService apiService;

    public CatFactRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(CatApiService.class);
    }

    public LiveData<String> getRandomCatFact() {
        MutableLiveData<String> catFactData = new MutableLiveData<>();

        apiService.getRandomFact().enqueue(new Callback<CatFact>() {
            @Override
            public void onResponse(Call<CatFact> call, Response<CatFact> response) {
                if (response.isSuccessful() && response.body() != null) {
                    catFactData.setValue(response.body().getCatFact());
                }
            }

            @Override
            public void onFailure(Call<CatFact> call, Throwable t) {
                catFactData.setValue("Errore: " + t.getMessage());
            }
        });

        return catFactData;
    }
}
