package com.zyn.lib.util;

import android.text.TextUtils;

/**
 * Created by tuka2401 on 2017/1/10.
 */

public class HttpUtils
{
    public static final String SCHEME_HTTP = "http://";
    public static final String SCHEME_HTTPS = "https://";

    public static String addHttpScheme(String s)
    {
        if (TextUtils.isEmpty(s))
        {
            return "";
        }

        if (s.startsWith(SCHEME_HTTP)||s.startsWith(SCHEME_HTTPS))
        {
            return s;
        } else
        {
            return SCHEME_HTTP + s;
        }
    }
}
