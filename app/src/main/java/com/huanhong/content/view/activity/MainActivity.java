package com.huanhong.content.view.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huanhong.content.R;
import com.huanhong.content.presenter.MainPresenter;
import com.huanhong.content.view.i.MainView;
import com.huanhong.content.view.view.LockLayout;
import com.zyn.lib.activity.BaseActivity;
import com.zyn.lib.util.ViewUtils;

public class MainActivity extends BaseActivity implements MainView
{

    private LockLayout mLLyLock;
    private ViewGroup mLockLyFront, mLockLyBack;
    private View mPwd, mBtnConfirm, mBtnCancel;
    private EditText mEtPwd;
    private WindowManager.LayoutParams mLayoutParams;
    private MainPresenter mMainPresenter;
    private TextView mTextView;

    @Override
    protected int getContentViewId()
    {
        return 0;
    }


    @Override
    protected void initView()
    {
        super.initView();
        ViewUtils.setSystemUiGone(getWindow().getDecorView());

        //root view
        mLLyLock = (LockLayout) LayoutInflater
                .from(this)
                .inflate(R.layout.layout_main_lock, null);
        mLockLyFront = (ViewGroup) mLLyLock.findViewById(R.id.layout_main_lock_ly_front);
        mLockLyBack = (ViewGroup) mLLyLock.findViewById(R.id.layout_main_lock_ly_back);

        //后台入口
        mPwd = LayoutInflater
                .from(this)
                .inflate(R.layout.view_main_pwd, null);
        mEtPwd = (EditText) mPwd.findViewById(R.id.view_main_pwd_et);
        mBtnConfirm = mPwd.findViewById(R.id.view_main_pwd_btn_confirm);
        mBtnCancel = mPwd.findViewById(R.id.view_main_pwd_btn_cancel);

        //layoutparams
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;//关键,可以屏蔽状态栏和导航栏
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        mTextView = new TextView(this);
        mTextView.setText(R.string.no_plan);
        mTextView.setTextColor(getResources().getColor(R.color.lib_white));
        mTextView.setGravity(Gravity.CENTER);
    }

    @Override
    protected void initListener()
    {
        super.initListener();

        mLLyLock.setLockistener(new LockLayout.LockListener()
        {
            @Override
            public void longClick()
            {
                showPassword();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismissPassword();
            }
        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String pwd = mEtPwd
                        .getText()
                        .toString();
                if (pwd.equals("3280"))
                {
                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void initOther()
    {
        super.initOther();
        //添加view进window
        mMainPresenter = new MainPresenter(this, this);
    }

    @Override
    public void lock()
    {
        if (mLLyLock != null && mLLyLock.getParent() == null)
        {
            getWindowManager().addView(mLLyLock, mLayoutParams);
            ViewUtils.setSystemUiGone(mLLyLock);
        }
    }

    @Override
    public void unlock()
    {
        if (mLLyLock != null && mLLyLock.getParent() != null)
        {
            getWindowManager().removeView(mLLyLock);
        }
    }

    @Override
    public void showBack(View view)
    {
        removeAllFromLockBackView();
        if (view == null)
        {
            mTextView.setText(getString(R.string.no_plan));
            addToLockBackView(mTextView);
        } else
        {
            addToLockBackView(view);
        }
    }

    @Override
    public void showFront(View view) {
        removeAllFromLockFrontView();
        if (view != null)
        {
            addToLockFrontView(view);
        }
    }

    @Override
    public void setShowText(String str) {
        mTextView.setText(str);
    }

    public void showPassword()
    {
        if (mLLyLock.indexOfChild(mPwd) != -1)
        {
            return;
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mLLyLock.addView(mPwd, params);

        //focus
        mEtPwd.requestFocus();
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
        {
            imm.showSoftInput(mEtPwd, 0);
        }
    }

    public void dismissPassword()
    {
        mEtPwd.clearFocus();
        mEtPwd.setText(null);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
        {
            imm.hideSoftInputFromWindow(mEtPwd.getWindowToken(), 0);
        }

        mLLyLock.removeView(mPwd);
    }

    private void addToLockBackView(View view)
    {
        mLockLyBack.addView(view, new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    private void removeAllFromLockBackView()
    {
        mLockLyBack.removeAllViews();
    }

    private void addToLockFrontView(View view)
    {
        mLockLyFront.addView(view, new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void removeAllFromLockFrontView()
    {
        mLockLyFront.removeAllViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.onResume();
        Log.e(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainPresenter.onPause();
        Log.e(TAG,"onPause");
    }

    @Override
    protected void onDestroy()
    {
        unlock();
        mMainPresenter.onDestory();
        mMainPresenter = null;
        super.onDestroy();
        Log.e(TAG,"onDestroy");
    }

    @Override
    public void finish()
    {
        //避免window洩漏
        unlock();
        super.finish();
    }

    @Override
    protected boolean isStatusBar()
    {
        return false;
    }
}
