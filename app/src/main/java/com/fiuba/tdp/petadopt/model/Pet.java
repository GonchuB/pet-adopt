package com.fiuba.tdp.petadopt.model;


import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


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
    private Boolean published = true;


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
    }

    public void setSecondColor(String secondColor) {
        this.secondColor = secondColor;
    }


    public String toJson() {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("type", type.toString());
            jsonObject.put("gender", gender.toString());
            jsonObject.put("name", name);
            jsonObject.put("age", age);
            jsonObject.put("description", description);
            jsonObject.put("published", published);
            if (location!=null) {
                jsonObject.put("location", prettyLocation());
            }
            jsonObject.put("colors", getColors());
            jsonObject.put("vaccinated", vaccinated);

            JSONObject jo = new JSONObject();
            jo.put("pet", jsonObject);
            return jo.toString();
        } catch (JSONException e){
            Log.e("Error creating pet JSON",e.getLocalizedMessage());
            return null;
        }
    }

    private String prettyLocation() {
        if (location!=null){
            return String.valueOf(location.latitude)+","+String.valueOf(location.longitude);
        }
        return "";
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
