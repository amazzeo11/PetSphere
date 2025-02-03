package com.unimib.petsphere.data.model;


import java.util.List;

public class PetResponseModel {
    private String status;
    private int totalResults;
    private List<PetModel> petList;

    public PetResponseModel(){}

    public PetResponseModel(List<PetModel> petList) {
        this.petList = petList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<PetModel> getPets() {
        return petList;
    }

    public void setPets(List<PetModel> petList) {
        this.petList = petList;
    }
}