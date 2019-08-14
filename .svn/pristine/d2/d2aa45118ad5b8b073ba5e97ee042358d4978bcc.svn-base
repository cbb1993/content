package com.huanhong.content.model.face.util;

/**
 * Created by tuka2401 on 2017/4/10.
 */

public class DoubleClickUtils {
    private static final long DOUBLE_CLICK_TIME = 500;
    private static long sLastTime = 0;


    public static boolean checkIsDoubleClick() {
        boolean result = false;
        if (System.currentTimeMillis() - sLastTime <= DOUBLE_CLICK_TIME) {
            result = true;
        }
        sLastTime = System.currentTimeMillis();
        return result;
    }
}
