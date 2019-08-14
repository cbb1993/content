package com.huanhong.content.model.login;


import android.text.TextUtils;

import com.huanhong.content.model.api.ApiConstans;
import com.zyn.lib.util.SharedPreferencesUtils;

public class Login
{
    private String id, password;

    public static void saveInfo(String id, String password)
    {
        SharedPreferencesUtils.addData(ApiConstans.LOGIN_ID, id);
        SharedPreferencesUtils.addData(ApiConstans.LOGIN_PASSWORD, password);
    }

    public static Login getLogin()
    {
        if(TextUtils.isEmpty(SharedPreferencesUtils.readData(ApiConstans.LOGIN_ID))||
                TextUtils.isEmpty(SharedPreferencesUtils.readData(ApiConstans.LOGIN_PASSWORD))){
            return null;
        }else {
            Login login = new Login();
            login.setId(SharedPreferencesUtils.readData(ApiConstans.LOGIN_ID));
            login.setPassword(SharedPreferencesUtils.readData(ApiConstans.LOGIN_PASSWORD));
            return login;
        }

    }
    // 是否在已有登录的情况下切换账号
    public static boolean isCheckWithNoLogin =false;

    public static Login getLogin(String account, String password)
    {
        isCheckWithNoLogin=false;
        if(TextUtils.isEmpty(account)||TextUtils.isEmpty(password)){
            return null;
        }else {
            Login login = new Login();
            login.setId(account);
            login.setPassword(password);
            return login;

        }
    }

    public static boolean hasInfo()
    {
        Login login = getLogin();
        if(login!=null){
            return  !(TextUtils.isEmpty(login.getId()) || TextUtils.isEmpty(login.getPassword()));
        }
        return false;

    }
    public static void clearLoginMsg()
    {
        SharedPreferencesUtils.addData(ApiConstans.LOGIN_ID, null);
        SharedPreferencesUtils.addData(ApiConstans.LOGIN_PASSWORD, null);
    }


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }



    // 网络原因导致 登录失败 连接上后需重新尝试登录

    private static void setTryLoginMsg(String account,String password)
    {
        SharedPreferencesUtils.addData(ApiConstans.TRY_LOGIN_ID, account);
        SharedPreferencesUtils.addData(ApiConstans.TRY_LOGIN_PASSWORD, password);
    }

    public static void clearTryLoginMsg()
    {
        SharedPreferencesUtils.addData(ApiConstans.TRY_LOGIN_ID, null);
        SharedPreferencesUtils.addData(ApiConstans.TRY_LOGIN_PASSWORD, null);
    }

    public static Login getTryLogin()
    {
        if(TextUtils.isEmpty(SharedPreferencesUtils.readData(ApiConstans.TRY_LOGIN_ID))||
                TextUtils.isEmpty(SharedPreferencesUtils.readData(ApiConstans.TRY_LOGIN_PASSWORD))){
            return null;
        }else {
            Login login = new Login();
            login.setId(SharedPreferencesUtils.readData(ApiConstans.TRY_LOGIN_ID));
            login.setPassword(SharedPreferencesUtils.readData(ApiConstans.TRY_LOGIN_PASSWORD));
            return login;
        }
    }

    public static boolean hasTryInfo()
    {
        Login login = getTryLogin();
        if(login!=null){
            return  !(TextUtils.isEmpty(login.getId()) || TextUtils.isEmpty(login.getPassword()));
        }
        return false;

    }

    public static  String tryAcount ,tryPassword;
    public static void requsetLogin(int type,String acount,String password){
        tryAcount=acount;
        tryPassword=password;
        //1 登录信息发送
        // 2 如果收到   表示 已经登录成功
        if(type==1){
            setTryLoginMsg(tryAcount,tryPassword);
        }else if(type==2){
            clearTryLoginMsg();
        }
    }
}
