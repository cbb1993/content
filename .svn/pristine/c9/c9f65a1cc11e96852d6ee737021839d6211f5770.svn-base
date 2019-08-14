package com.huanhong.content.view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class LockLayout extends RelativeLayout
{

    private LockListener mLockListener = null;
    private long mLastTime;

    public LockLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public LockLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LockLayout(Context context)
    {
        this(context, null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            mLastTime = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - mLastTime > 1.8f * 1000 && isClickOn(event.getX(),event.getY()))
        {
            if (mLockListener != null)
            {
                mLockListener.longClick();
                return true;
            }
        }

        return super.dispatchTouchEvent(event);
    }


    private boolean isClickOn(float x, float y)
    {
        return (x <= 180 && y <= 180);

    }

    public void setLockistener(LockListener lockListener)
    {
        this.mLockListener = lockListener;
    }

    public interface LockListener
    {
        void longClick();
    }
}
