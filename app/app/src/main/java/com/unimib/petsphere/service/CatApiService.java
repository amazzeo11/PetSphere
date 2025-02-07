package com.unimib.petsphere.service;

import com.unimib.petsphere.data.model.CatFact;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CatApiService {
    @GET("/")
    Call<CatFact> getRandomFact();
}
