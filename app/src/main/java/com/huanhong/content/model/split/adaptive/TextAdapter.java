package com.huanhong.content.model.split.adaptive;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.huanhong.content.R;
import com.huanhong.content.model.plan.server.ServerContent;

/**
 * Created by tuka2401 on 2017/1/9.
 */

public class TextAdapter extends Adapter
{
    private TextView mTextView;
    private String mString;

    public TextAdapter(Context context,String str)
    {
        super(context);
        mString = str;
    }

    @Override
    public void init()
    {
        mTextView = new TextView(getContext());
        mTextView.setText(mString);
        mTextView.setTextColor(getContext().getResources().getColor(R.color.lib_white));
        mTextView.setBackgroundColor(getContext().getResources().getColor(R.color.lib_blue_4c98ea));
        mTextView.setGravity(Gravity.CENTER);
    }

    @Override
    public View getView()
    {
        return mTextView;
    }

    @Override
    public ServerContent getCurrContent()
    {
        return new ServerContent();
    }

    @Override
    public void deinit()
    {
        mTextView = null;
    }
}
