package com.unimib.petsphere.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.petsphere.data.model.DogFact;
import com.unimib.petsphere.service.DogApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DogFactRepository {
    private static final String BASE_URL = "https://dog-api.kinduff.com/";
    private final DogApiService apiService;

    public DogFactRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(DogApiService.class);
    }

    public LiveData<String> getRandomDogFact() {
        MutableLiveData<String> dogFactData = new MutableLiveData<>();

        apiService.getRandomFact().enqueue(new Callback<DogFact>() {
            @Override
            public void onResponse(Call<DogFact> call, Response<DogFact> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dogFactData.setValue(response.body().getDogFact());
                }
            }

            @Override
            public void onFailure(Call<DogFact> call, Throwable t) {
                dogFactData.setValue("Errore: " + t.getMessage());
            }
        });

        return dogFactData;
    }
}
