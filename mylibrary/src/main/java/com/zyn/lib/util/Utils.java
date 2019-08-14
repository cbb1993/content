package com.zyn.lib.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.zyn.lib.R;
import com.zyn.lib.app.AppUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class Utils {

    static SimpleDateFormat formatter = null;

    public static <T> boolean isEmptyList(List<T> list) {
        if (null != list && !list.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 获取当前是星期几  根据设备的时区判断
     *
     * @return 从周一开始1-7
     */
    public static int getCurrWeek() {
        final Calendar c = Calendar.getInstance();
//        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int result = c.get(Calendar.DAY_OF_WEEK);
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
    public static String getWeekDate(int week) {
        if (week <= 0) {
            return "1970-01-01";
        }

        int curr = getCurrWeek();
        int count = week - curr;
        if (count < 0) {
            count += 7;
        }
        long timestamp = System.currentTimeMillis();
        timestamp = moreTimestampDay(timestamp, count);
        return getStrDate(timestamp);
    }

    public static String getCurrDate() {
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(c.getTime());
    }

    public static long getNextDayTimestamp() {
        return moreTimestampDay(getTimestamp(getCurrDate(), "00:00:00"), 1);
    }

    public static long getTimestamp(String date, String time) {
        String s = date + " " + time;
        long result = 0;
        try {
            result = getTimestamp(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getStrDate(long time) {
//        System.setProperty("user.timezone", "Asia/Shanghai");
//        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
//        TimeZone.setDefault(tz);
        String times = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            times = format.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

    public static String getStrDateHM(String times) {
        long time = 0;
        try {
            time = Long.parseLong(times);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String t = format.format(new Date(time));
        return t;
    }

//    public static String getStrDate(long times)
//    {
//
//        try
//        {
//            Timestamp timestamp = new Timestamp(times);
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            return format.format(timestamp);
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return "";
//    }

    public static String getStrDate(String times) {
        try {
            long t = Long.parseLong(times);
            return getStrDate(t);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "";
    }

    public static String getStrTimeHM(String times) {
        try {
            long t = Long.parseLong(times);
            String s = new SimpleDateFormat("HH:mm").format(t);
            return s;
        } catch (Exception ignore) {

        }

        return "";
    }

    /**
     * 比较日期
     *
     * @param date1
     * @param date2
     * @return 若date1>date2返回true，
     */

    public static boolean compareDate(String date1, String date2) {
        try {
            Date _date1 = (new SimpleDateFormat("yyyy-MM-dd")).parse(date1);
            Date _date2 = (new SimpleDateFormat("yyyy-MM-dd")).parse(date2);

            if (_date1.compareTo(_date2) > 0)
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }

    public static String getStrTime(int h, int m, int s) {
        String hh = "00", mm = "00", ss = "00";
        if (h < 10) {
            hh = "0" + h;
        } else {
            hh = "" + h;
        }
        if (m < 10) {
            mm = "0" + m;
        } else {
            mm = "" + m;
        }
        if (s < 10 && s > 0) {
            ss = "0" + s;
        } else if (s <= 0) {
            ss = "00";
        } else {
            ss = "" + s;
        }
        return hh + ":" + mm + ":" + ss;
    }

    public static String getStrRemainingTime(String s1) {
        String result = "00:00:00";
        try {
            long l1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                    .parse(s1)
                    .getTime();
            long l2 = System.currentTimeMillis();

            result = getStrDate(l1 - l2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getStrTimestamp(long time) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0000-00-00 00:00:00";

    }

    public static String getStrTimestamp(String time) {
        try {
            long t = Long.parseLong(time);
            return getStrTimestamp(t);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0000-00-00 00:00:00";

    }

    public static long moreTimestampDay(long timestamp, int day) {
        return timestamp + day * 24 * 60 * 60 * 1000;
    }

    public static String getStrTime(long time) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            return simpleDateFormat.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "00:00:00";
    }

    public static void toastShort(Context context, String text) {
        Toast
                .makeText(context, text, Toast.LENGTH_SHORT)
                .show();
    }

    public static void toastShort(String text) {
        Toast
                .makeText(AppUtils.getBaseApplication(), text, Toast.LENGTH_SHORT)
                .show();
    }

    public static void toastLong(String text) {
        Toast
                .makeText(AppUtils.getBaseApplication(), text, Toast.LENGTH_LONG)
                .show();
    }

    public static boolean toastStrEmpty(Context context, String text, String msg) {
        boolean isEmpty = TextUtils.isEmpty(text);
        if (isEmpty) {
            toastShort(context, msg);
        }
        return isEmpty;
    }

    public static boolean toastIsPhoneNum(Context context, String text) {
        boolean isEmpty = TextUtils.isEmpty(text);
        if (!isEmpty && text.length() == 11) {
            return true;
        } else {
            toastShort(context, context.getString(R.string.lib_toastIsPhoneNum));
            return false;
        }

    }

    public static boolean toastIsEmail(Context context, String text) {
        String regex_email = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        if (!text.matches(regex_email)) {
            toastShort(context, "请输入正确格式的邮箱！");
            return false;
        } else {
            return true;
        }
    }

    public static boolean toastIsFax(Context context, String text, int type) {
        String regex_fax = "^(\\(\\d{3,4}\\)|\\d{3,4}-)?\\d{7,8}$";
        if (!text.matches(regex_fax)) {
            if (type == 0)
                toastShort(context, "请输入正确格式的电话！");
            else
                toastShort(context, "请输入正确格式的传真！");

            return false;
        } else {
            return true;
        }
    }


    public static void toastShort(Context context, int strId) {
        Toast
                .makeText(context, strId, Toast.LENGTH_SHORT)
                .show();
    }

    public static String getStrDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(System.currentTimeMillis());
        return dateString;

    }

    public static String getStrTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(System.currentTimeMillis());
        return dateString;

    }

    public static long getTimestamp(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter
                    .parse(date)
                    .getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isExpirationDate(String Date) {
        try {
            java.util.Date _date = (new SimpleDateFormat("yyyy-MM-dd")).parse(Date);
            //当天晚上23:599:5
            if (_date.getTime() + (24 * 60 * 60 - 1) * 1000 < System.currentTimeMillis())
                return true;
            else
                return false;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void callPhone(Context context, String phone) {
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        context.startActivity(intent);
    }

    public static void showMessage(Context context, String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            String message = jsonObject.getString("message");
            toastShort(context, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void killProcess(Context mAct) {

        String packageName = mAct.getPackageName();

        String processId = "";

        try {

            Runtime r = Runtime.getRuntime();

            Process p = r.exec("ps");

            BufferedReader br = new BufferedReader(new InputStreamReader(p

                    .getInputStream()));

            String inline;

            while ((inline = br.readLine()) != null) {

                if (inline.contains(packageName)) {

                    break;

                }

            }

            br.close();

            StringTokenizer processInfoTokenizer = new StringTokenizer(inline);

            int count = 0;

            while (processInfoTokenizer.hasMoreTokens()) {

                count++;

                processId = processInfoTokenizer.nextToken();

                if (count == 2) {

                    break;

                }

            }

            r.exec("kill -15 " + processId);

        } catch (IOException ex) {

        }
    }
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager
                .PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Utils.showMessage(context,"没有手机权限，无法收到通知！");
            return "";
        }
        return tm.getDeviceId();
    }
}
