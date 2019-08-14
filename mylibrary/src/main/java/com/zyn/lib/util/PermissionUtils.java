package com.zyn.lib.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by tuka2401 on 2017/4/7.
 */

public class PermissionUtils {
    public static boolean checkSelfPermission(Activity activity, String permission, int requestCode) {
        Log.e(PermissionUtils.class.getSimpleName(), "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(activity,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    requestCode);
            return false;
        }

        return true;
    }
}
