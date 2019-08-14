package com.huanhong.content.util;

import android.content.Context;
import android.widget.Toast;

import com.zyn.lib.app.AppUtils;

/**
 * Created by Administrator on 2016/12/14.
 */
public class NormalUtil {
    public static final String DIR="1";
    public static final String FILE="2";
    public static final String DATABASE="BASE";
    public static final String PLAYMSG="PlayMsg";
    private static Context mApplicationContext;

    public static void initApplicationContext(Context context)
    {
        mApplicationContext = context.getApplicationContext();
    }

    public static Context getApplicationContext()
    {
        return mApplicationContext;
    }


    static Toast toast = null;

    public static void showToast(String hint) {
        if (toast == null) {
            toast = Toast.makeText(AppUtils.getBaseApplication(), hint, Toast.LENGTH_SHORT);
        } else {
            toast.setText(hint);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }


}
