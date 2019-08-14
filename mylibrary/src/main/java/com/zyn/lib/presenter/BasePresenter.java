package com.zyn.lib.presenter;

import android.content.Context;

import java.util.Map;


public class BasePresenter<T>
{
    private Context mContext;
    private T mView;

    public BasePresenter(Context context, T view)
    {
        mContext = context;
        mView = view;
    }

    public Context getContext()
    {
        return mContext;
    }

    public T getView()
    {
        return mView;
    }

    public void onResume()
    {

    }

    public void onPause()
    {

    }

    public void onDestory()
    {

    }

    public void getShot(String windowingid,ShotListener shotListener) {
    }

    public Map<String, Object> getCurrContent() {
        return  null;
    }

    public interface ShotListener {
        void onFinish(String path);
    }
}
