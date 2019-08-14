package com.zyn.lib.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zyn.lib.activity.BaseActivity;

import java.lang.reflect.Field;

public abstract class BaseFrag extends Fragment implements View.OnClickListener
{
    private boolean mIsCreated = false;
    private boolean mIsInited = false;
    private boolean mIsPreLoad = false;
    private View contentview;
    private BaseActivity mBaseActivity;

    public abstract int setContentView();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        contentview = inflater.inflate(setContentView(), null);
        return contentview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mBaseActivity = (BaseActivity) getActivity();
        initView();
        initListener();
        initData();
        mIsCreated = true;
    }

    /**
     * 尝试初始化，只能初始化一次
     */
    public void tryToInit()
    {
        if (!mIsInited)
        {
            if (init())
            {
                mIsInited = true;
            }
        }
    }

    /**
     * 由子类重写init,自定义初始化
     *
     * @return
     */
    protected boolean init()
    {
        return false;
    }

    protected void doRefresh()
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);

        if (isCreated() && !hidden)
        {
            if (isInited())
            {
                doRefresh();
            } else
            {
                tryToInit();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && !isPreLoad() && isCreated())
        {
            if (isInited())
            {
                doRefresh();
            } else
            {
                tryToInit();
            }
        }
    }


    protected void initView()
    {

    }

    protected void initListener()
    {

    }

    protected void initData()
    {
        //若为预加载模式，在initData()中尝试初始化
        if (isPreLoad())
        {
            tryToInit();
        }
    }

    public boolean isCreated()
    {
        return mIsCreated;
    }

    public View findViewById(int id)
    {
        return contentview.findViewById(id);
    }

    @Override
    public void onDetach()
    {
        mBaseActivity = null;

        try
        {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");

            childFragmentManager.setAccessible(true);

            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e)
        {

            throw new RuntimeException(e);

        } catch (IllegalAccessException e)
        {

            throw new RuntimeException(e);

        }
        super.onDetach();
    }


    public Context getApplicationContext()
    {
        return getContext().getApplicationContext();
    }

    public boolean isPreLoad()
    {
        return mIsPreLoad;
    }

    public void setPreLoad(boolean preLoad)
    {
        mIsPreLoad = preLoad;
    }

    public boolean isInited()
    {
        return mIsInited;
    }

    public void setIsInited(boolean isInited)
    {
        mIsInited = isInited;
    }

    @Override
    public void onClick(View v)
    {

    }

    @ColorInt
    public int getColorInt(@ColorRes int color)
    {
        if (mBaseActivity != null)
        {
            return mBaseActivity.getColorInt(color);
        }
        return getResources().getColor(color);
    }
}
