package com.zyn.lib.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

/**
 * Created by tuka2401 on 2016/12/29.
 */

public class MediaUtils
{
    public static long getMediaDuration(String path)
    {
        String duration = "0";
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();

        try
        {
            if (path != null)
            {
                mmr.setDataSource(path);
            }
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex)
        {
        } finally
        {
            mmr.release();
        }

        try
        {
            return Long.parseLong(duration);
        }catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static Bitmap getMediaKeyFrame(String path, long position)
    {
        try
        {
            MediaMetadataRetriever rev = new MediaMetadataRetriever();
            rev.setDataSource(path); //这里第一个参数需要Context，传this指针
            return rev.getFrameAtTime(position * 1000,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
