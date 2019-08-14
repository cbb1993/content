package com.zyn.lib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by tuka2401 on 2016/10/20.
 */
public class FileUtils
{

    public static String getFileNameFromUrl(String url)
    {
        if (url != null)
        {
            return url.substring(url.lastIndexOf("/") + 1);
        }

        return "";
    }

    public static void inputToFile(InputStream ins, File file)
    {
        OutputStream os = null;
        try
        {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1)
            {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void saveBitmapToFile(Bitmap bitmap, String path)
    {
        if (bitmap == null)
        {
            return;
        }

        File f = new File(path);
        if (f.exists())
        {
            f.delete();
        } else
        {
            if (!f
                    .getParentFile()
                    .exists())
            {
                f
                        .getParentFile()
                        .mkdirs();
            }
        }
        try
        {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Bitmap inputToBitmap(InputStream ins)
    {
        Bitmap bitmap = BitmapFactory.decodeStream(ins);
        return bitmap;
    }

    public static String readStream(String path)
    {
        String mImage = "";
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            baos.close();
            byte[] buffer = baos.toByteArray();
            mImage = Base64.encodeToString(buffer, 0, buffer.length, Base64.DEFAULT);
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally
        {
            if (fis != null)
                try
                {
                    fis.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
        }

        return mImage;
    }

    public static File getDiskCacheDir(Context context)
    {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
        {
            file = context.getExternalCacheDir();
        } else
        {
            file = context.getCacheDir();
        }
        return file;
    }

    public static String getDiskCacheDirPath(Context context)
    {
        return getDiskCacheDir(context).getPath();
    }

    public static File getDiskDir(Context context, String type)
    {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
        {
            file = context.getExternalFilesDir(type);
        } else
        {
            file = context.getFilesDir();
        }
        return file;
    }

    public static String getDiskDirPath(Context context, String type)
    {
        return getDiskDir(context, type).getPath();
    }
}
