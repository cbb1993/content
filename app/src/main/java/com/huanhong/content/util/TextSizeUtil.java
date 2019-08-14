package com.huanhong.content.util;

import android.content.Context;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by 坎坎 on 2017/3/21.
 */

public class TextSizeUtil {
    public static void setTextSize(Context context, TextView textView ,float percent){
        if(textView!=null){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,ScreenUtil.getScreenHeight(context)*percent);
        }
    }

}
