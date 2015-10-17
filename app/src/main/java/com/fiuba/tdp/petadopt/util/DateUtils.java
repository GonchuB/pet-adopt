package com.fiuba.tdp.petadopt.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtils {
    public static Date dateFromString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return format.parse(dateString);
        } catch (Exception e) {
            Log.e("Error parsing date", e.getLocalizedMessage());
            return null;
        }
    }

    public static String stringFromDateForQuestionList(Date date){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy, HH:mm");
        return format.format(date);
    }
}
