package com.unimib.petsphere.data.model;
//Author: Alessia Mazzeo
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
@Entity
public class PetModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long uid;
    private String image;
    private String animal_type;
    private String microchip;
    private String name;
    private String nickname;
    private double weight;
    private String age;
    private String birthday;
    private String color;
    private String notes;
    private String allergies;


    public PetModel(String image, String animalType, String microchip, String name, String nickname, double weight, String age, String birthday, String color, String notes, String allergies){
        this.image=image;
        this.animal_type = animalType;
        this.microchip = microchip;
        this.name = name;
        this.nickname = nickname;
        this.weight = weight;
        this.age = age;
        this.birthday = birthday;
        this.color=color;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
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


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(Long uid) { this.uid=uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
