package com.fiuba.tdp.petadopt.model;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class Pet {
    private String name;
    private String age;
    private Type type;
    private Gender gender;
    private String description;
    private Boolean vaccinated;
    private LatLng location;
    private String firstColor;
    private String secondColor;
    private String colors;


    public Pet() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Type getType() {
        return type;
    }

    //TODO - improve
    public void setType(String type) {
        if (type.equals("Gato")) {
            this.type = Type.Cat;
        } else {
            this.type = Type.Dog;
        }
    }

    public Gender getGender() {
        return gender;
    }

    //TODO - improve
    public void setGender(String gender) {
        if (gender.equals("Macho")) {
            this.gender = Gender.male;
        } else {
            this.gender = Gender.female;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getVaccinated() {
        return vaccinated;
    }

    public void setVaccinated(Boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getColors() {
        return firstColor + " " + secondColor;
    }

    public void setFirstColor(String firstColor) {
        this.firstColor = firstColor;
        if (secondColor != null) {
            colors = getColors();
        } else {
            colors = firstColor;
        }
    }

    public void setSecondColor(String secondColor) {
        this.secondColor = secondColor;
        if (firstColor != null) {
            colors = getColors();
        } else {
            colors = secondColor;
        }
    }


    public String toJson() {
        Gson gson = new Gson();
        JsonElement je = gson.toJsonTree(this);
        JsonObject jo = new JsonObject();
        jo.add("pet", je);
        return jo.toString();
    }

    public enum Type {
        Cat,
        Dog
    }


    public enum Gender {
        male,
        female
    }
}
