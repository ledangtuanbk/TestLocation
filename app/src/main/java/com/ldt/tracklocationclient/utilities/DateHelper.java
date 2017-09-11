package com.ldt.tracklocationclient.utilities;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ldt on 9/11/2017.
 */

public class DateHelper {

    /**
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String dateToString(Date date, String dateFormat){
        return DateFormat.format(dateFormat, date).toString();
    }

    /**
     * Convert date (miliseconds) to string with dateFormat
     * @param dateValue
     * @param dateFormat
     * @return
     */
    public static String dateToString(long dateValue, String dateFormat){
        return dateToString(new Date((dateValue)), dateFormat);
    }

    /**
     * Convert Calendar to string with dateFormat
     * @param calendar
     * @param dateFormat
     * @return
     */
    public static String dateToString(Calendar calendar, String dateFormat){
        return dateToString(calendar.getTimeInMillis(),dateFormat);
    }



}
