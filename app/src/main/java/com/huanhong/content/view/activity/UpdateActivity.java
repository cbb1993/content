package com.huanhong.content.view.activity;

import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanhong.content.R;
import com.huanhong.content.presenter.UpdatePresenter;
import com.huanhong.content.view.i.UpdateView;
import com.zyn.lib.activity.BaseActivity;
import com.zyn.lib.util.ViewUtils;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

public class UpdateActivity extends BaseActivity implements UpdateView
{
    private TextView mTextView;
    private ImageView mImageView;
    private UpdatePresenter mUpdatePresenter;

    @Override
    protected int getContentViewId()
    {
        return R.layout.activity_update;
    }

    @Override
    protected void initView()
    {
        super.initView();
        ViewUtils.setSystemUiGone(getWindow().getDecorView());

        mTextView = (TextView) findViewById(R.id.update_tv);
        mImageView = (ImageView) findViewById(R.id.update_iv);

        getWindow().addFlags(FLAG_KEEP_SCREEN_ON|FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
    }

    @Override
    protected void initOther()
    {
        super.initOther();
        mUpdatePresenter = new UpdatePresenter(this, this);
    }

    @Override
    public void startSyncAnim()
    {
        stopAnim();
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageResource(R.drawable.list_sync);
        AnimationDrawable drawable = (AnimationDrawable) mImageView.getDrawable();
        if(drawable!=null)
        {
            drawable.start();
        }
    }

    @Override
    public void stopAnim()
    {
        AnimationDrawable drawable = (AnimationDrawable) mImageView.getDrawable();
        if(drawable!=null)
        {
            drawable.stop();
        }
    }

    @Override
    public void hiddenImageView()
    {
        mImageView.setVisibility(View.INVISIBLE);
        stopAnim();
    }

    @Override
    public void showMessage(String msg)
    {
        mTextView.setText(msg);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
        stopAnim();
        mUpdatePresenter.onDestory();
        mUpdatePresenter = null;
    }

    @Override
    protected boolean isStatusBar()
    {
        return true;
    }

    @Override
    protected boolean isColorStatusBar()
    {
        return false;
    }
}