package com.tiberiugaspar.mylauncher.util;

import android.graphics.drawable.ColorDrawable;

import com.tiberiugaspar.mylauncher.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class NewsApiUtil {

    public static ColorDrawable[] vibrantLightColorList =
            {
                    new ColorDrawable(R.string.vibrant_color_1),
                    new ColorDrawable(R.string.vibrant_color_2),
                    new ColorDrawable(R.string.vibrant_color_3),
                    new ColorDrawable(R.string.vibrant_color_4),
                    new ColorDrawable(R.string.vibrant_color_5),
                    new ColorDrawable(R.string.vibrant_color_6),
                    new ColorDrawable(R.string.vibrant_color_7),
                    new ColorDrawable(R.string.vibrant_color_8)
            };

    public static ColorDrawable getRandomDrawbleColor() {
        int idx = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[idx];
    }

    public static String DateToTimeFormat(String oldstringDate){
        PrettyTime p = new PrettyTime(new Locale(getCountry()));
        String isTime = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.ENGLISH);
            Date date = sdf.parse(oldstringDate);
            isTime = p.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isTime;
    }

    public static String DateFormat(String oldstringDate){
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy", new Locale(getCountry()));
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(oldstringDate);
            newDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = oldstringDate;
        }

        return newDate;
    }

    public static String getCountry(){
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }

    public static String getLanguage(){
        Locale locale = Locale.getDefault();
        String country = locale.getLanguage();
        return country.toLowerCase();
    }
}
