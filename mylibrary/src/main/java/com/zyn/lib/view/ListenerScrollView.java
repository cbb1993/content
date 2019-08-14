package com.zyn.lib.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class ListenerScrollView extends NestedScrollView
{
    private boolean mIsNeedAutoScroll = false;
    private int mScrollHeight = 0;
    private View mHeader, mContent;
    private int mScrollDirection = 0;
    private OnScrollListener mOnScrollListener;
    private int mHeaderHeight;

    public ListenerScrollView(Context context)
    {
        super(context);
    }

    public ListenerScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ListenerScrollView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取children
        mHeader = ((ViewGroup) getChildAt(0)).getChildAt(0);
        mContent = ((ViewGroup) getChildAt(0)).getChildAt(1);
//        //设置初始可滚动高度
//        if (mScrollHeight == 0)
//        {
//            mScrollHeight = ABViewUtil.getViewMeasuredHeight(mHeader);
//        }
        setContentHeight();
    }

    private void setContentHeight()
    {
        //设置content
        if (mContent != null)
        {
            ViewGroup.LayoutParams params = mContent.getLayoutParams();
            if (params != null)
            {
                params.height = getHeight() - (mHeaderHeight - mScrollHeight);
                mContent.setLayoutParams(params);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        boolean result = super.onTouchEvent(ev);
        if (ev.getActionMasked() == MotionEvent.ACTION_UP || ev.getActionMasked() == MotionEvent.ACTION_CANCEL)
        {
            if (ifNeedAutoScroll())
            {
                return true;
            }
        }

        return result;
    }

    public boolean ifNeedAutoScroll()
    {
        if (getScrollY() > 0 && getScrollY() < mScrollHeight)
        {
            if (mScrollDirection > 0)
            {
                smoothScrollTo(0, mScrollHeight);
                postInvalidateDelayed(10);
            } else if (mScrollDirection <= 0)
            {
                smoothScrollTo(0, 0);
                postInvalidateDelayed(10);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);

        if (Math.abs(t - oldt) > 5)
        {
            mScrollDirection = t - oldt;
        }

        if (mOnScrollListener != null)
        {
            float percent = (float) t / mScrollHeight;
            if (percent > mScrollHeight)
            {
                percent = mScrollHeight;
            }
            if (percent < 0)
            {
                percent = 0;
            }
            mOnScrollListener.onScrolling(percent);
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes)
    {
        mIsNeedAutoScroll = false;
        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed)
    {
        mIsNeedAutoScroll = true;
        boolean hiddenTop = dy > 0 && getScrollY() < mScrollHeight;
        boolean showTop = dy < 0 && getScrollY() > 0 && !ViewCompat.canScrollVertically(target, -1);

        if (hiddenTop || showTop)
        {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public void onStopNestedScroll(View target)
    {
        super.onStopNestedScroll(target);
        Log.d("ListenerScrollView", mIsNeedAutoScroll + "");
        if (mIsNeedAutoScroll)
        {
            mIsNeedAutoScroll = false;
            ifNeedAutoScroll();
        }
    }


//        @Override
//    public boolean onNestedPreFling(View target, float velocityX, float velocityY)
//    {
//        if (getScrollY() >= mScrollHeight)
//        {
//            return false;
//        } else
//        {
//            if (ViewCompat.canScrollVertically(target, -1) && velocityY < 0)
//            {
//                return false;
//            }
//            else
//            {
//                return true;
//            }
//        }
//    }


    @Override
    public void fling(int velocityY)
    {

    }

    public int getScrollHeight()
    {
        return mScrollHeight;
    }

    public void setScrollHeight(int scrollHeight, int headerHeight)
    {
        mScrollHeight = scrollHeight;
        mHeaderHeight = headerHeight;
        setContentHeight();
    }

    public OnScrollListener getOnScrollListener()
    {
        return mOnScrollListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener)
    {
        mOnScrollListener = onScrollListener;
    }

    public interface OnScrollListener
    {
        void onScrolling(float percent);
    }
}
