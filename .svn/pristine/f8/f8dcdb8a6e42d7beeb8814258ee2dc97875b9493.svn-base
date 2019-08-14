package com.huanhong.content.model.login;

import android.util.Log;

import com.huanhong.content.model.api.ApiClient;
import com.huanhong.content.model.api.ApiConstans;
import com.huanhong.content.model.api.ApiMethod;
import com.huanhong.content.model.api.ApiProtocol;
import com.huanhong.content.model.info.ClientInfo;
import com.zyn.lib.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class LoginHandler implements ApiClient.OnResponseListener
{
    private static LoginHandler mInstance;
    private List<LoginHandlerListener> mLoginHandlerListenerList = new ArrayList<>();

    private String account ="";
    private String password ="";

    private LoginHandler()
    {
        ApiClient
                .getInstance()
                .addOnResponseListener(ApiMethod.METHOD_LOGIN, this);
    }

    public static synchronized LoginHandler getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new LoginHandler();
        }

        return mInstance;
    }

    public static void setInstance(LoginHandler instance)
    {
        mInstance = instance;
    }

    public void login()
    {
        Login login= Login.getLogin();
        if(login!=null){
            login(login);
        }
    }


    public void login(Login login)
    {

        // 保存临时账号信息
        Login.requsetLogin(1,login.getId(),login.getPassword());


        ApiProtocol request = new ApiProtocol();
        request.setId(ApiProtocol.getAutoID());
        request.setType(ApiProtocol.TYPE_REQUEST);
        request.setMethod(ApiMethod.METHOD_LOGIN);
        request.setData(login);
        account=login.getId();
        password=login.getPassword();
        ApiClient.getInstance().send(request);
    }

    public void addLoginHandlerListener(LoginHandler.LoginHandlerListener loginHandlerListener)
    {
        mLoginHandlerListenerList.add(loginHandlerListener);
    }

    public void removeLoginHandlerListenerList(LoginHandler.LoginHandlerListener loginHandlerListener)
    {
        mLoginHandlerListenerList.remove(loginHandlerListener);
    }

    @Override
    public void onResponse(ApiProtocol apiProtocol)
    {
        if (mLoginHandlerListenerList != null)
        {
            for(LoginHandlerListener loginHandlerListener:mLoginHandlerListenerList)
            {
                if(loginHandlerListener!=null)
                {
                    //登录成功 将临时信息保存为账户信息 并清空临时信息

                    Login.saveInfo(account,password);
                    Login.requsetLogin(2,null,null);


                    loginHandlerListener.onLoginSucceed();


                }
            }
        }
    }

    @Override
    public void onError(ApiProtocol apiProtocol)
    {
        if (mLoginHandlerListenerList != null)
        {
            for(LoginHandlerListener loginHandlerListener:mLoginHandlerListenerList)
            {
                if(loginHandlerListener!=null)
                {
                    loginHandlerListener.onLoginFailed(apiProtocol.getMessage());

                    // 登录失败 清空临时信息
                    Login.requsetLogin(2,null,null);
                    // 判断之前是否有账号处于登录状态
                    if(!Login.isCheckWithNoLogin){
                        //  先判断有没有账户信息
                        if(Login.hasInfo()){
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
            }
        }
    }

    public interface LoginHandlerListener
    {
        void onLoginSucceed();

        void onLoginFailed(String msg);
    }
}
