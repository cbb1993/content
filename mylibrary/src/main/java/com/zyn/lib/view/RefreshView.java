package com.zyn.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.zyn.lib.view.recycleview.HeaderLayout;

public class RefreshView extends PullToRefreshBase<LinearLayout> implements PullToRefreshBase.OnRefreshListener<LinearLayout>
{
    private LinearLayout mLinearLayout;

    private RefreshViewListener mRefreshViewListener;

    public RefreshView(Context context)
    {
        this(context, null);
    }

    public RefreshView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setHeaderLayout(new HeaderLayout(getContext()));
        setMode(Mode.PULL_FROM_START);
        setOnRefreshListener(this);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection()
    {
        return Orientation.VERTICAL;
    }

    @Override
    protected LinearLayout createRefreshableView(Context context, AttributeSet attrs)
    {
        mLinearLayout = new LinearLayout(context);
        return mLinearLayout;
    }

    public void setContent(View view)
    {
        mLinearLayout.addView(view);
    }

    @Override
    protected boolean isReadyForPullEnd()
    {
        return false;
    }

    @Override
    protected boolean isReadyForPullStart()
    {
        if (mRefreshViewListener != null)
        {
            return mRefreshViewListener.isReadyForPullStart();
        } else if (mLinearLayout
                .getChildAt(0)
                .getScrollY() == 0)
        {
            return true;
        }
        return false;
    }

    @Override
    public void onRefresh(PullToRefreshBase<LinearLayout> refreshView)
    {
        if (mRefreshViewListener != null)
        {
            mRefreshViewListener.onRefresh();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mRefreshViewListener != null)
        {
            float percent = ((float) -getScrollY()) / getHeaderLayout().getContentSize();
            mRefreshViewListener.onPull(percent);
        }
    }

    public RefreshViewListener getRefreshViewListener()
    {
        return mRefreshViewListener;
    }

    public void setRefreshViewListener(RefreshViewListener refreshViewListener)
    {
        mRefreshViewListener = refreshViewListener;
    }

    public interface RefreshViewListener
    {
        boolean isReadyForPullStart();

        void onRefresh();

        void onPull(float percent);
    }
}
