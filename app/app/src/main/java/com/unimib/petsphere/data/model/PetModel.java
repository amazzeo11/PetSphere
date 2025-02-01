package com.unimib.petsphere.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
@Entity
public class PetModel {
    @PrimaryKey(autoGenerate = true)
    public long uid;

    private String animal_type;
    private String microchip;
    private String name;
    private String nickname;
    private String weight;
    private String age;
    private String birthday;
    private String colore;
    private String notes;
    private String allergies;


    public PetModel(String animalType, String microchip, String name, String nickname, String weight, String age, String birthday, String colore, String notes, String allergies){
        animal_type = animalType;
        this.microchip = microchip;
        this.name = name;
        this.nickname = nickname;
        this.weight = weight;
        this.age = age;
        this.birthday = birthday;
        this.colore=colore;
        this.notes = notes;
        this.allergies = allergies;
    }

    public PetModel() {

    }


//getters and setters:

    public String getAnimal_type() {
        return animal_type;
    }

    public void setAnimal_type(String animal_type) {
        this.animal_type = animal_type;
    }

    public String getMicrochip() {
        return microchip;
    }

    public void setMicrochip(String microchip) {
        this.microchip = microchip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }


    public String getColore() {
        return colore;
    }

    public void setColore(String colore) {
        this.colore = colore;
    }
}
