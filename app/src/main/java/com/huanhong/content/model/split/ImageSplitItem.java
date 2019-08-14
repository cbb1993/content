package com.huanhong.content.model.split;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.huanhong.content.model.plan.server.ServerSplit;

public class ImageSplitItem extends SplitItem
{
    private ImageView mImageView;
    private String mUrl;

    public ImageSplitItem(Context context, ServerSplit split, String url)
    {
        super(context, split);
        mUrl = url;
        mImageView = new ImageView(context);
        mImageView.setImageResource(com.zyn.lib.R.drawable.loading);
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImageView.setBackgroundColor(Color.YELLOW);
    }

    @Override
    public View getView()
    {
        return mImageView;
    }

    @Override
    public void recycle()
    {
        mImageView = null;
    }
}
