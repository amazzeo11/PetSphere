package com.unimib.petsphere.data.model;
//Author: Alessia Mazzeo

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DogFact {
    @SerializedName("facts")
    private List<String> facts;

    public String getDogFact() {
        return (facts != null && !facts.isEmpty()) ? facts.get(0) : "Nessun fatto disponibile";
    }
}
