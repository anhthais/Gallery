package com.example.gallery.helper;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DateConverter {
    public static String simpleLongToString(long input){
        Calendar myCal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        myCal.setTimeInMillis(input);
        return formatter.format(myCal.getTime());
    }

    public static String longToString(long input){
        Calendar myCal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        myCal.setTimeInMillis(input);
        return formatter.format(myCal.getTime());
    }

    public static Date stringToDate(String input){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        try{
            Date res = sdf.parse(input);
            return res;
        } catch (Exception e){
            Log.d("stringToDate", e.getMessage());
            return new Date(0);
        }
    }

    public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();

        //create the list
        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);

        //create the result map of TimeUnit and difference
        Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
        long milliesRest = diffInMillies;

        for ( TimeUnit unit : units ) {
            //calculate difference in millisecond
            long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;

            //put the result in the map
            result.put(unit,diff);
        }
        return result;
    }

    public static Long getMinutesFromComputeDiff(Date date1, Date date2){
        Map<TimeUnit, Long> res = computeDiff(date1, date2);
        return res.get(TimeUnit.MINUTES);
    }

    public static Date plusDays(Date current, int numOfDays){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, numOfDays);
        return cal.getTime();
    }

    public static Date plusHours(Date current, int numOfHours){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, numOfHours);
        return cal.getTime();
    }

    public static Date plusMinutes(Date current, int numOfMinutes){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, numOfMinutes);
        return cal.getTime();
    }
}
