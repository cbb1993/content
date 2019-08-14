package com.huanhong.content.model.split;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.huanhong.content.model.plan.server.ServerSplit;

public abstract class SplitItem
{
    private Context mContext;
    private ServerSplit mSplit;

    public SplitItem(Context context, ServerSplit split)
    {
        this.mContext = context;
        mSplit = split;
    }

    public Context getContext()
    {
        return mContext;
    }

    public void setContext(Context context)
    {
        mContext = context;
    }

    public ServerSplit getSplit()
    {
        return mSplit;
    }

    public void setSplit(ServerSplit split)
    {
        mSplit = split;
    }

    public Bitmap getBitmapShot() {return null;};

    public Object getCurrContent(){return null;};

    public abstract View getView();

    public abstract void recycle();
}
