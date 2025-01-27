package com.unimib.petsphere.data;

import java.util.Date;

public class PetModel {
    private String animal_type;
    private String microchip;
    private String name;
    private String nickname;
    private float weight;
    private int age;
    private Date birthday;
    private String notes;
    private String allergies;

    public PetModel(String animalType, String microchip, String name, String nickname, float weight, int age, Date birthday, String notes, String allergies){
        animal_type = animalType;
        this.microchip = microchip;
        this.name = name;
        this.nickname = nickname;
        this.weight = weight;
        this.age = age;
        this.birthday = birthday;
        this.notes = notes;
        this.allergies = allergies;
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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
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



}
