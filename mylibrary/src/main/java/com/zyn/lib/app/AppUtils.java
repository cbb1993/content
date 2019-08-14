package com.zyn.lib.app;

import android.app.ActivityManager;
import android.content.Context;

import com.zyn.lib.app.BaseApplication;


public class AppUtils
{
    private static BaseApplication mApplication;

    public static void initBaseApplication(BaseApplication application)
    {
        mApplication = application;
    }

    public static BaseApplication getBaseApplication()
    {
        return mApplication;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }
}
