package com.fiuba.tdp.petadopt.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by tomas on 17/10/15.
 */
public class Adopter {

    private String id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Date createdAt;
    private Boolean approved;

    public Adopter () {}



    public void loadFromJSON(JSONObject adoptantJSON) throws JSONException {
        JSONObject userObject = adoptantJSON.getJSONObject("user");
        this.id = userObject.getString("id");
        this.fullName = userObject.getString("full_name");
        this.firstName = userObject.getString("first_name");
        this.lastName = userObject.getString("last_name");
        this.phone = userObject.getString("phone");
        this.email = userObject.getString("email");
        this.createdAt = parseDate(userObject.getString("created_at"));
        this.approved = adoptantJSON.getBoolean("approved");
    }

    private Date parseDate(String date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        ParsePosition p = new ParsePosition(0);
        parserSDF.setTimeZone(tz);
        return parserSDF.parse(date, p);

    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
