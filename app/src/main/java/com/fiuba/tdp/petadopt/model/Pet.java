package com.fiuba.tdp.petadopt.model;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class Pet {
    private Integer id;
    private String name;
    private String age;
    private Type type;
    private Gender gender;
    private String description;
    private Boolean vaccinated;
    private Boolean needs_transit_home;
    private LatLng location;
    private String firstColor;
    private String secondColor;
    private ArrayList<String> colors;
    ArrayList<String> images;


    public Pet() {
        colors = new ArrayList<>();
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
        String colors = "";
        for (int i = 0; i < this.colors.size(); i++) {
            colors = colors + this.colors.get(i);
            if (i < this.colors.size() -1 ) {
                if (this.colors.get(i).lastIndexOf(",") < 0) {
                    colors = colors + ", ";
                } else {
                    colors = colors + " ";
                }
            }
        }
        return colors;
    }

    public void setFirstColor(String firstColor) {
        this.firstColor = firstColor;
        if (colors.size() < 1) {
            colors.add(0, firstColor);
        } else {
            colors.set(0,firstColor);
        }
    }

    public void setSecondColor(String secondColor) {
        this.secondColor = secondColor;
        if (colors.size() < 2) {
            colors.add(1,secondColor);
        } else {
            colors.set(1,secondColor);
        }
    }


    public String toJson() {
        Gson gson = new Gson();
        JsonElement je = gson.toJsonTree(this);
        JsonObject jo = je.getAsJsonObject();
        jo.remove("id");
        jo.remove("images");
        jo.remove("firstColor");
        jo.remove("secondColor");
        jo.addProperty("location", String.valueOf(this.location.latitude) + "," + String.valueOf(this.location.longitude));
        jo.addProperty("colors", this.getColors());
        JsonObject result = new JsonObject();
        result.add("pet", jo);
        return result.toString();
    }

    public void loadFromJSON(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
        this.description = jsonObject.getString("description");
        this.vaccinated = jsonObject.getBoolean("vaccinated");
        this.needs_transit_home = jsonObject.getBoolean("needs_transit_home");
        this.type = parseType(jsonObject.getString("type"));
        this.gender = parseGender(jsonObject.getString("gender"));
        this.colors = parseColors(jsonObject.getString("colors"));
        this.images = parseImages(jsonObject.getJSONArray("images"));
    }

    private ArrayList<String> parseColors(String colors) {
        ArrayList<String> a = new ArrayList<>(Arrays.asList(colors.split("\\s* \\s*")));
        if (a.size() > 0) {
            firstColor = a.get(0);
        }
        if (a.size() > 1) {
            secondColor = a.get(1);
        }
        return a;
    }

    private ArrayList<String> parseImages(JSONArray imagesArray) throws JSONException {
        ArrayList<String> images = new ArrayList<>(imagesArray.length());
        for(int i = 0; i < imagesArray.length(); i++) {
            String image = imagesArray.getString(i);
            images.add(i,image);
        }

        return images;
    }

    private Gender parseGender(String gender) {
        if (gender.equals("male")) {
            return Gender.male;
        } else {
            return Gender.female;
        }
    }

    private Type parseType(String type) {
        if (type.toLowerCase().equals("cat")) {
            return Type.Cat;
        } else if (type.toLowerCase().equals("dog")) {
            return Type.Dog;
        }
        return Type.Unknown;
    }

    @Override
    public String toString() {
        String gender;
        String type;
        if (this.gender == Gender.male) {
            gender = "Macho";
        } else {
            gender = "Hembra";
        }
        if (this.type == Type.Cat) {
            type = "Gato";
        } else {
            type = "Perro";
        }

        return this.name + ": " + type + " " + gender;
    }

    public enum Type {
        Cat,
        Dog,
        Unknown
    }


    public enum Gender {
        male,
        female
    }
}
