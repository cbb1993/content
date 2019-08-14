package com.zyn.lib.view.recycleview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zyn.lib.R;

public class FooterLayout extends FrameLayout
{
    private TextView mDescText;
    private boolean hasMoreData = true;

    public FooterLayout(Context context)
    {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.lib_view_refresh_footer, this);
        mDescText = (TextView) view.findViewById(R.id.view_refresh_footer_tv);

        hasMoreData = true;
    }


    public void setHasData()
    {
        mDescText.setText("正在加载更多");
        hasMoreData = true;
    }

    public void setNoData()
    {
        hasMoreData = false;
        mDescText.setText("已经看到最后啦");
    }

    public boolean isHasMoreData()
    {
        return hasMoreData;
    }
}
