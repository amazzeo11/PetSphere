package com.unimib.petsphere.service;
//Author: Alessia Mazzeo
import com.unimib.petsphere.data.model.DogFact;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DogApiService {
    @GET("api/facts")
    Call<DogFact> getRandomFact();
}
