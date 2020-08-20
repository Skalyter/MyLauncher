package com.tiberiugaspar.mylauncher.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public abstract class CalendarUtil {
    public static String getDurationFromCalendar(Calendar c1) {
        Calendar c2 = Calendar.getInstance();
        int year1 = c1.get(Calendar.YEAR), year2 = c2.get(Calendar.YEAR);
        String duration;
        duration = getDifference(year1, year2, Calendar.YEAR);
        if (duration!= null){
            return duration;
        }

        int m1 = c1.get(Calendar.MONTH), m2 = c2.get(Calendar.MONTH);
        duration = getDifference(m1, m2, Calendar.MONTH);
        if (duration != null){
            return duration;
        }

        int d1 = c1.get(Calendar.DAY_OF_YEAR), d2 = c2.get(Calendar.DAY_OF_YEAR);
        duration = getDifference(d1, d2, Calendar.DAY_OF_MONTH);
        if (duration != null){
            return duration;
        }

        int h1 = c1.get(Calendar.HOUR_OF_DAY), h2 = c2.get(Calendar.HOUR_OF_DAY);
        duration = getDifference(h1, h2, Calendar.HOUR_OF_DAY);
        if (duration != null) {
            return duration;
        }

        int min1 = c1.get(Calendar.MINUTE), min2 = c2.get(Calendar.MINUTE);
        duration = getDifference(min1, min2, Calendar.MINUTE);
        if (duration != null){
            return duration;
        }

        int s1 = c1.get(Calendar.SECOND), s2 = c2.get(Calendar.SECOND);
        duration = getDifference(s1, s2, Calendar.SECOND);
        if (duration!= null){
            return duration;
        }

        return "Right now";

    }

    private static String getDifference(int value1, int value2, int calendarField) {
        String difference = null;
        if (value2 != value1) {
            boolean isSingular = value2 - value1 == 1; //if v2-v1 = 1, we'll have only one unit of time
            difference = String.format("1 %s", getCalendarField(calendarField, isSingular));
        }
        return difference;

    }

    private static String getCalendarField(int calendarField, boolean isSingular) {
        String field;
        switch (calendarField) {
            case Calendar.YEAR:
                field = "year";
                break;
            case Calendar.MONTH:
                field = "month";
                break;
            case Calendar.DAY_OF_MONTH:
                field = "day";
                break;
            case Calendar.HOUR_OF_DAY:
                field = "hour";
                break;
            case Calendar.MINUTE:
                field = "minute";
                break;
            case Calendar.SECOND:
                field = "second";
                break;
            default:
                field = "Right now";
                break;
        }
        if (!isSingular) {
            field += "s";
        }
        return field;
    }

    public static Calendar getCalendarFromString(String string){
        SimpleDateFormat format = new SimpleDateFormat("EE, dd MM yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }
}
