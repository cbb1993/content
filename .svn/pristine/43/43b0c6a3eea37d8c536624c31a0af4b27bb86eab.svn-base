package com.zyn.lib.view.recycleview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.LoadingLayoutBase;
import com.zyn.lib.R;
import com.zyn.lib.util.ViewUtils;


public class HeaderLayoutWithStatusBar extends LoadingLayoutBase
{

    private View mView;
    //    private TextView mHeaderText;
    private ImageView progressBar;
    private CharSequence mPullLabel;
    private CharSequence mRefreshingLabel;
    private CharSequence mReleaseLabel;

    private AnimationDrawable mAnimationDrawable;


    public HeaderLayoutWithStatusBar(Context context)
    {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.lib_view_refresh_header, this);
        mView = findViewById(R.id.view_refresh_header);
//        mHeaderText = (TextView) mView.findViewById(R.id.view_refresh_footer_tv);
        progressBar = (ImageView) mView.findViewById(R.id.pro_loading);

        // Load in labels
        mPullLabel = context.getString(R.string.lib_pull_to_refresh_pull_label);
        mRefreshingLabel = context.getString(R.string.lib_pull_to_refresh_refreshing_label);
        mReleaseLabel = context.getString(R.string.lib_pull_to_refresh_release_label);

        reset();
    }

    // 获取"加载头部"高度
    @Override
    public int getContentSize()
    {
        // 设置未完全显示的时候就促发刷新动作
//        return mInnerLayout.getHeight() * 7 / 10;
        return mView.getHeight() + ViewUtils.getStatusBarHeightWhenTranslucentStatusBar(getContext());
    }

    // 开始下拉时的回调
    @Override
    public void pullToRefresh()
    {
        progressBar.setImageResource(R.drawable.ldprogress);
        mAnimationDrawable = (AnimationDrawable) progressBar.getDrawable();
        mAnimationDrawable.stop();
    }

    // "加载头部"完全显示时的回调
    @Override
    public void releaseToRefresh()
    {
        mAnimationDrawable.stop();
    }

    // 下拉拖动时的回调
    @Override
    public void onPull(float scaleOfLayout)
    {
        mAnimationDrawable.stop();
    }

    // 释放后刷新时的回调
    @Override
    public void refreshing()
    {
        mAnimationDrawable.start();
    }

    // 初始化到未刷新状态
    @Override
    public void reset()
    {
        progressBar.setImageResource(R.drawable.ldprogress);
        mAnimationDrawable = (AnimationDrawable) progressBar.getDrawable();
        mAnimationDrawable.stop();
    }

    @Override
    public void setPullLabel(CharSequence pullLabel)
    {
        mPullLabel = pullLabel;
    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel)
    {
        mRefreshingLabel = refreshingLabel;
    }

    @Override
    public void setReleaseLabel(CharSequence releaseLabel)
    {
        mReleaseLabel = releaseLabel;
    }
}
