package com.zyn.lib.manager;


import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RadioManager implements View.OnClickListener
{
    private List<View> mViewList = new ArrayList<>();
    private OnCheckedListener mOnCheckedChangeListener;
    private int checked = -1; //选中项索引

    public RadioManager(int checked, View... views)
    {
        for (View view : views)
        {
            mViewList.add(view);
            if (view != null)
            {
                view.setOnClickListener(this);
            }
        }
        setCheck(checked);
    }

    public void setOnCheckedChangeListener(OnCheckedListener onCheckedChangeListener)
    {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void setCheck(int position)
    {
        if (!isValid(position))
        {
            return;
        }

        reset();

        View view = mViewList.get(position);
        if (view != null)
        {
            view.setActivated(true);
            checked = position;
            if(mOnCheckedChangeListener!=null)
            {
                mOnCheckedChangeListener.onChecked(position);
            }
        }
    }

    public boolean isValid(int position)
    {
        return mViewList != null && position <= mViewList.size() && position >= 0;
    }

    public void reset()
    {
        if (mViewList == null || mViewList.isEmpty())
        {
            return;
        }

        for (View view : mViewList)
        {
            if (view != null)
            {
                view.setActivated(false);
            }
        }

        checked = -1;
    }

    @Override
    public void onClick(View v)
    {
        int index = mViewList.indexOf(v);
        setCheck(index);
    }

    public int getChecked()
    {
        return checked;
    }

    public interface OnCheckedListener
    {
        void onChecked(int position);
    }



}
