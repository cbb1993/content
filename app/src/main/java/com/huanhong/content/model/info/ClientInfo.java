package com.huanhong.content.model.info;

import android.os.Handler;
import android.os.Message;

import com.huanhong.content.model.api.ApiClient;
import com.huanhong.content.model.api.ApiConstans;
import com.huanhong.content.model.login.Login;
import com.huanhong.content.model.login.LoginHandler;
import com.huanhong.content.presenter.MainPresenter;
import com.zyn.lib.presenter.BasePresenter;
import com.zyn.lib.util.SharedPreferencesUtils;


public class ClientInfo implements ApiClient.ApiClientListener, LoginHandler.LoginHandlerListener
{
    private static ClientInfo mInstance;
    private boolean mIsConnected = false;
    private boolean mIsLogined = false;
    private boolean mIsSyncing = false;
    private BasePresenter mMainPresenter;

    private ClientInfo()
    {

    }

    public static synchronized ClientInfo getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new ClientInfo();
        }
        return mInstance;
    }

    public void init()
    {
        ApiClient
                .getInstance()
                .addApiClientListener(this);

        LoginHandler
                .getInstance()
                .addLoginHandlerListener(this);
    }

    @Override
    public void onInited()
    {
        mIsConnected = false;
        mIsLogined = false;
    }

    @Override
    public void onConnected()
    {
        mIsConnected = true;
        handler.removeMessages(1);
        handler.sendEmptyMessageDelayed(1,20000);

    }

 private static  Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 先判断有没有新账号
            if(Login.hasTryInfo()){
                LoginHandler
                        .getInstance()
                        .login(Login.getLogin(SharedPreferencesUtils.readData(ApiConstans.TRY_LOGIN_ID),SharedPreferencesUtils.readData(ApiConstans.TRY_LOGIN_PASSWORD)));
            }else {
                if (Login.hasInfo())
                {
                    // 有
                    String account =SharedPreferencesUtils.readData(ApiConstans.LOGIN_ID);
                    String password =SharedPreferencesUtils.readData(ApiConstans.LOGIN_PASSWORD);
                    // 清空账户信息   把账户信息当作临时信息来请求
                    Login.clearLoginMsg();
                    LoginHandler
                            .getInstance()
                            .login(Login.getLogin(account,password));
                }
            }
        }
    };

    @Override
    public void onConnectFailed(Exception e)
    {
        mIsConnected = false;
    }

    @Override
    public void onSent(String message)
    {

    }

    @Override
    public void onSendFailed(String message, Exception e)
    {

    }

    @Override
    public void onError(Exception e)
    {

    }

    @Override
    public void onDisconnected()
    {
        mIsConnected = false;
        mIsLogined = false;
    }

    @Override
    public void onLoginSucceed()
    {
        mIsLogined = true;
    }

    @Override
    public void onLoginFailed(String msg)
    {
        mIsLogined = false;
    }

    public boolean isConnected()
    {
        return mIsConnected;
    }

    public boolean isLogined()
    {
        return mIsLogined;
    }

    public boolean isSyncing()
    {
        return mIsSyncing;
    }

    public void setSyncing(boolean syncing)
    {
        mIsSyncing = syncing;
    }

    public BasePresenter getMainPresenter()
    {
        return mMainPresenter;
    }

    public void setMainPresenter(BasePresenter mainPresenter)
    {
        mMainPresenter = mainPresenter;
    }
}
