package com.huanhong.content.view.view;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class DispatchLayout extends RelativeLayout
{
    private DispatchListener mDispatchListener;

    public DispatchLayout(Context context)
    {
        super(context);
    }

    public DispatchListener getDispatchListener()
    {
        return mDispatchListener;
    }

    public void setDispatchListener(DispatchListener dispatchListener)
    {
        mDispatchListener = dispatchListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if(mDispatchListener!=null)
        {
            mDispatchListener.onDispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface DispatchListener
    {
        void onDispatchTouchEvent(MotionEvent ev);
    }
}
