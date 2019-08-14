package com.zyn.lib.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadUtils
{
    public static File downLoadFile(Context context, String name, String httpUrl)
    {
        // TODO Auto-generated method stub
        String fileName = name;
        if (TextUtils.isEmpty(fileName))
        {
            if (httpUrl != null)
            {
                fileName = FileUtils.getFileNameFromUrl(httpUrl);
            }
        }

        File tmpFile = FileUtils.getDiskCacheDir(context);
        final File file = new File(tmpFile, fileName);
//        if(!file.exists())
//        {
//            try
//            {
//                file.createNewFile();
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
        try
        {
            URL url = new URL(httpUrl);
            try
            {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream is = new BufferedInputStream(conn.getInputStream());
                BufferedOutputStream  fos = new BufferedOutputStream(new FileOutputStream(file));
//                InputStream is =  conn.getInputStream();
//                FileOutputStream  fos = new FileOutputStream(file);
                byte[] buf = new byte[1024];

                conn.connect();
                if (conn.getResponseCode() >= 400)
                {

                } else
                {
                    long start = System.currentTimeMillis();
                    int numRead = 0;
                    while ((numRead = is.read(buf)) != -1)
                    {
                        fos.write(buf, 0, numRead);
                    }
                    fos.flush();
                    long end = System.currentTimeMillis();
                    Log.e("下载时间",end-start+"");
                }

                conn.disconnect();
                fos.close();
                is.close();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }
        } catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }

        return file;
    }
//打开APK程序代码

    public static void openFile(Context context, File file)
    {
        // TODO Auto-generated method stub
        Log.e("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


}
