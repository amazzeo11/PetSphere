package com.unimib.petsphere.data.model;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CatFact {
    @SerializedName("data")
    private List<String> facts;

    public String getCatFact() {
        return (facts != null && !facts.isEmpty()) ? facts.get(0) : "Nessun fatto disponibile";
    }
}

