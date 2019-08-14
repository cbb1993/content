package com.huanhong.content.util;

import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by admin on 2017/8/10.
 */

public class TimeUtil {

    private static String default_date = "1970-01-01";
    private static String default_timestamp = "1970-01-01 00:00:00";

    private static SimpleDateFormat formatter_timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");

    public static String getStrTimestamp(long timestamp) {
        try {
            return formatter_timestamp.format(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return default_timestamp;
        }
    }

    public static String getStrDate(long timestamp) {
        try {
            return formatter_date.format(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return default_date;
        }
    }

    public static long getTimestamp(String time) {
        try {
            return formatter_timestamp.parse(time).getTime();
        } catch (Exception e) {
            try {
                return formatter_date.parse(time).getTime();
            } catch (ParseException e1) {
                e1.printStackTrace();
                return 0;
            }
        }
    }

    public static long getTimestamp(String date, String time) {
        String s = date + " " + time;
        return getTimestamp(s);
    }

    public static long getCurrTimestamp() {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * 获取当前是星期几  根据设备的时区判断
     *
     * @return 从周一开始1-7
     */
    public static int getCurrWeek() {
        return getChineseWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }

    public static int getChineseWeek(int week) {
        int result = week;
        if (result == 1) {
            result = 7;
        } else if (result >= 2 && result <= 7) {
            result--;
        }
        return result;
    }

    /**
     * @param week 从周一开始1-7
     * @return
     */
    public static long getWeekTimestamp(int week) {
        if (week <= 0) {
            return 0;
        }

        int curr = getCurrWeek();
        int count = week - curr;
        if (count < 0) {
            count += 7;
        }
        long timestamp = getCurrTimestamp();
        timestamp = moreTimestampDay(timestamp, count);
        return timestamp;
    }


    public static long moreTimestampDay(long timestamp, int day) {
        return timestamp + (long) day * 24 * 60 * 60 * 1000;
    }

    public static boolean isToday(long time) {
        return getStrDate(getCurrTimestamp()).equals(getStrDate(time));
    }

    public static String dateFormat(String time,String format) {
        long timeL=0;
        if (time != null && !time.equals("null")) {
            timeL = Long.parseLong(time);
            return dateFormat(timeL, format);
        }
        return null;
    }
    public static String dateFormat(long time,String format) {
        SimpleDateFormat simpleDateFormat =new  SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return simpleDateFormat.format(new Date(time));
    }

}
