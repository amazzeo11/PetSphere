package com.unimib.petsphere.service;



import com.unimib.petsphere.data.model.DogFact;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/facts")
    Call<DogFact> getRandomFact();
}
