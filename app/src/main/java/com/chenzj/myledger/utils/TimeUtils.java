package com.chenzj.myledger.utils;

import android.content.Intent;
import android.text.GetChars;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/2/20 20:24
 */
public class TimeUtils {
    public static final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM");
    public static final SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String YEAR_SPACER = "年";
    public static final String MONTH_SPACER = "月";
    public static final String DAY_SPACER = "日";
    public static final String HORIZONTAL_SPACER = "-";
    public static final String SLASH_SPACER = "/";

    public static String getCurrentTime(){
        return ymdhms.format(new Date());
    }

    public static String getCurrentDate(){
        return ymd.format(new Date());
    }

    public static String date2string(Date date){
        return ymdhms.format(date);
    }

    public static String date2stringday(Date date){
        return ymd.format(date);
    }

    public static String date2stringMonth(Date date){
        return ym.format(date);
    }


    public static int getDayNumOfMonth(Date date){
        int MaxDay = 0;
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
            MaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }else {
            MaxDay = Integer.parseInt(ymd.format(calendar.getTime()).split("-")[2]);
        }
        return MaxDay;
    }



    public static Date string2date(String datestr) throws ParseException {
        return ymdhms.parse(datestr);
    }

    public static Date dayStr2date(String datestr) throws ParseException {
        return ymd.parse(datestr);
    }

    public static Date monthStr2date(String datestr) throws ParseException {
        return ym.parse(datestr);
    }
}
