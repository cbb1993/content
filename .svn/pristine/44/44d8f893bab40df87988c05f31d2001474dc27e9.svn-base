package com.huanhong.content.view.activity;

import android.content.Intent;
import android.text.TextUtils;

import com.huanhong.content.R;
import com.huanhong.content.model.api.ApiConstans;
import com.huanhong.content.model.info.ClientInfo;
import com.zyn.lib.activity.BaseActivity;
import com.zyn.lib.util.SharedPreferencesUtils;

/**
 * Created by Administrator on 2017/1/12.
 */

public class StartActivity extends BaseActivity{
    @Override
    protected int getContentViewId() {
        return R.layout.activity_start_layout;
    }

    @Override
    protected void initView() {
        super.initView();
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(0);
                    if(ClientInfo.getInstance().isLogined()){
                        startActivity(new Intent(StartActivity.this,SettingActivity.class));
                    }else {
                        String acount= SharedPreferencesUtils.readData(ApiConstans.LOGIN_ID);
                        if(TextUtils.isEmpty(acount)){
                            startActivity(new Intent(StartActivity.this,LoginActivity.class));
                        }else {
                            startActivity(new Intent(StartActivity.this,SettingActivity.class));
                        }
                    }
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected boolean isColorStatusBar()
    {
        return false;
    }
}
