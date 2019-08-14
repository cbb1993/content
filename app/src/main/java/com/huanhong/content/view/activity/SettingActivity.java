package com.huanhong.content.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huanhong.content.R;
import com.huanhong.content.adapter.PopAdapter;
import com.huanhong.content.model.PlayMsgModel;
import com.huanhong.content.model.api.ApiClient;
import com.huanhong.content.model.api.ApiConstans;
import com.huanhong.content.model.info.ClientInfo;
import com.huanhong.content.model.login.Login;
import com.huanhong.content.model.login.LoginHandler;
import com.huanhong.content.util.NormalUtil;
import com.huanhong.content.util.PermissionUtil;
import com.huanhong.content.util.ScreenUtil;
import com.huanhong.content.util.TextSizeUtil;
import com.huanhong.content.util.bluetooth.MyBlueToothInitUtil;
import com.huanhong.content.util.bluetooth.PrintUtil;
import com.huanhong.content.view.dialog.LoginDialog;
import com.huanhong.content.view.dialog.UrlDialog;
import com.zyn.lib.activity.BaseActivity;
import com.zyn.lib.util.SharedPreferencesUtils;
import com.zyn.lib.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/12/29.
 */

public class SettingActivity extends BaseActivity implements ApiClient.ApiClientListener, LoginHandler.LoginHandlerListener
{
    private final static String H5LIST = "h5List";
    private RadioGroup activity_setting_rg;
    private RadioButton activity_setting_rbtn_webpage;//选择展示网页
    private RadioButton activity_setting_rbtn_vedio;//选择展示视频
    private View activity_setting_ll_webpage;//网页设置页面
    private View activity_setting_ll_vedio;//视频设置页面
    private TextView activity_setting_spinner; //下拉列表
    private TextView activity_setting_tv_status;//登录状态
    private TextView activity_setting_tv_edit;//修改登录信息按钮
    private TextView activity_setting_tv_choosefile;//选择文件按钮
    private TextView activity_setting_tv_save;//保存设置按钮
    private EditText activity_setting_ev_keeptime;//网页静默时间
    private TextView activity_setting_tv_connectstatus;//连接状态
    private TextView activity_setting_tv_plan;//按计划播放
    private PopupWindow popupWindow;
    private ListView pop_listview;
//    private String acount;
//    private String password;
    private List<String> h5List;
    private PopAdapter adapter;
    private TextView tv_wait;

    private TextView activity_setting_tv_no;

    @Override
    protected int getContentViewId()
    {
        return R.layout.activity_setting_percent_layout;
    }

    @Override
    protected void initView()
    {
        super.initView();


        NormalUtil.initApplicationContext(this);
        activity_setting_rg = (RadioGroup) findViewById(R.id.activity_setting_rg);
        activity_setting_rbtn_webpage = (RadioButton) findViewById(R.id.activity_setting_rbtn_webpage);
        activity_setting_rbtn_vedio = (RadioButton) findViewById(R.id.activity_setting_rbtn_vedio);
        activity_setting_ll_webpage = findViewById(R.id.activity_setting_ll_webpage);
        activity_setting_ll_vedio = findViewById(R.id.activity_setting_ll_vedio);
        activity_setting_spinner = (TextView) findViewById(R.id.activity_setting_spinner);
        activity_setting_tv_status = (TextView) findViewById(R.id.activity_setting_tv_status);
        activity_setting_tv_edit = (TextView) findViewById(R.id.activity_setting_tv_edit);
        activity_setting_tv_choosefile = (TextView) findViewById(R.id.activity_setting_tv_choosefile);
        activity_setting_tv_save = (TextView) findViewById(R.id.activity_setting_tv_save);
        activity_setting_ev_keeptime = (EditText) findViewById(R.id.activity_setting_ev_keeptime);
        activity_setting_tv_connectstatus = (TextView) findViewById(R.id.activity_setting_tv_connectstatus);
        activity_setting_tv_plan = (TextView) findViewById(R.id.activity_setting_tv_plan);
        tv_wait = (TextView) findViewById(R.id.tv_wait);
        activity_setting_rbtn_vedio.setChecked(true);
        activity_setting_ll_vedio.setVisibility(View.VISIBLE);

        activity_setting_tv_no= (TextView) findViewById(R.id.activity_setting_tv_no);

        String account =getIntent().getStringExtra(ApiConstans.LOGIN_ID);
        String password =getIntent().getStringExtra(ApiConstans.LOGIN_PASSWORD);
        initLogin(account,password);
        initH5UrlList();

        TextSizeUtil.setTextSize(this,activity_setting_tv_no,0.03f);
        TextSizeUtil.setTextSize(this,activity_setting_tv_status,0.03f);
        TextSizeUtil.setTextSize(this,activity_setting_tv_connectstatus,0.03f);
        TextSizeUtil.setTextSize(this,activity_setting_tv_edit,0.03f);
        TextSizeUtil.setTextSize(this,activity_setting_tv_plan,0.04f);
        TextSizeUtil.setTextSize(this,activity_setting_tv_save,0.04f);
        TextSizeUtil.setTextSize(this,activity_setting_spinner,0.04f);
        TextSizeUtil.setTextSize(this,activity_setting_tv_choosefile,0.04f);
        TextSizeUtil.setTextSize(this,tv_wait,0.04f);
        TextSizeUtil.setTextSize(this,activity_setting_ev_keeptime,0.04f);

//        List<DateHandler.PlanDate> planDateList = new ArrayList<>();
//        DateHandler.PlanDate planDate = new DateHandler().new PlanDate();
//        planDate.setPlanId(1);
//        planDate.setStart(new Date(2017,1,21));
//        planDate.setEnd(new Date(2017,1,23));
//        planDateList.add(planDate);
//
//        planDate = new DateHandler().new PlanDate();
//        planDate.setPlanId(2);
//        planDate.setStart(new Date(2017,1,17));
//        planDate.setEnd(new Date(2017,1,20));
//        planDateList.add(planDate);
//
//        planDate = new DateHandler().new PlanDate();
//        planDate.setPlanId(3);
//        planDate.setStart(new Date(2017,1,14));
//        planDate.setEnd(new Date(2017,1,15));
//        planDateList.add(planDate);
//
//        DateHandler d = new DateHandler();
//        d.handle(planDateList);
        String p[] = new String[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            p = new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_SETTINGS,
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION};
        }
        ActivityCompat.requestPermissions(
                this,
                p,
                1
        );
    }

    private void initH5UrlList()
    {
        Set<String> set = SharedPreferencesUtils.readStringSerData(H5LIST);
        h5List = new ArrayList<>(set);
    }

    private void initLogin(String account ,String password)
    {
        String flag = getIntent().getStringExtra("flag");
        if (ClientInfo
                .getInstance()
                .isConnected())
        {
            activity_setting_tv_connectstatus.setText("连接状态：已连接");
        } else
        {
            activity_setting_tv_connectstatus.setText("连接状态：未连接");
        }
        if (ClientInfo
                .getInstance()
                .isLogined())
        {
            activity_setting_tv_status.setText("连接状态：已登录");
            activity_setting_tv_no.setText("登录账号："+SharedPreferencesUtils.readData(ApiConstans.LOGIN_ID));
        } else
        {
            activity_setting_tv_status.setText("连接状态：未登录");
            activity_setting_tv_no.setText("登录账号：无");
        }
        if ("1".equals(flag) && ClientInfo
                .getInstance()
                .isConnected() && !ClientInfo
                .getInstance()
                .isLogined())
        {
//            acount = SharedPreferencesUtils.readData("acount");
//            password = SharedPreferencesUtils.readData("password");
//            Login.saveInfo(acount, password);

            if(account!=null&&password!=null){
                LoginHandler.getInstance().login(Login.getLogin(account,password));
            }

//            LoginHandler
//                    .getInstance()
//                    .login();
        }
        ApiClient
                .getInstance()
                .addApiClientListener(SettingActivity.this);
        LoginHandler
                .getInstance()
                .addLoginHandlerListener(SettingActivity.this);
    }

    private void setSpinnerPop()
    {
        if (popupWindow == null)
        {
            View view = LayoutInflater
                    .from(this)
                    .inflate(R.layout.pop_spinner_layout, null);
            pop_listview = (ListView) view.findViewById(R.id.pop_listview);
            View foot = LayoutInflater
                    .from(this)
                    .inflate(R.layout.item_spinner_foot_layout, null);
            pop_listview.addFooterView(foot);
            popupWindow = new PopupWindow(view,
                    activity_setting_spinner.getWidth(), ScreenUtil.getScreenHeight(SettingActivity.this)/2, true);
            popupWindow.setTouchable(true);
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.lib_white));
        }
        if (adapter == null)
        {
            adapter = new PopAdapter(new PopAdapter.OnDeleteClickListener()
            {
                @Override
                public void onClick(int position)
                {
                    h5List.remove(position);
                    adapter.setList(h5List);
                    adapter.notifyDataSetChanged();
                    Set<String> set = new HashSet<String>();
                    set.addAll(h5List);
                    SharedPreferencesUtils.addStringSetData(H5LIST, set);
                }
            });
            adapter.setList(h5List);
            pop_listview.setAdapter(adapter);
        } else
        {
            adapter.setList(h5List);
            adapter.notifyDataSetChanged();
        }


        popupWindow.showAsDropDown(activity_setting_spinner, 0, 2);
        pop_listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (position < h5List.size())
                {
                    activity_setting_spinner.setText(h5List.get(position));
                } else
                {
                    new UrlDialog(SettingActivity.this, new UrlDialog.OnURLCreatCallback()
                    {
                        @Override
                        public void onComfirmClick(String url)
                        {
                            h5List.add(url);
                            activity_setting_spinner.setText(url);
                            Set<String> set = new HashSet<String>();
                            set.addAll(h5List);
                            SharedPreferencesUtils.addStringSetData(H5LIST, set);
                        }
                    }).show();
                }
                popupWindow.dismiss();
            }
        });
    }

    @Override
    protected void initListener()
    {
        super.initListener();
        activity_setting_tv_plan.setOnClickListener(this);
        activity_setting_tv_save.setOnClickListener(this);
        activity_setting_spinner.setOnClickListener(this);
        activity_setting_tv_edit.setOnClickListener(this);
        activity_setting_tv_choosefile.setOnClickListener(this);
        activity_setting_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if (checkedId == R.id.activity_setting_rbtn_webpage)
                {
                    activity_setting_ll_webpage.setVisibility(View.VISIBLE);
                    activity_setting_ll_vedio.setVisibility(View.GONE);
                } else if (checkedId == R.id.activity_setting_rbtn_vedio)
                {
                    activity_setting_ll_webpage.setVisibility(View.GONE);
                    activity_setting_ll_vedio.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void initOther()
    {
        super.initOther();
        PlayMsgModel info = PlayMsgModel.getPlayMsgInfo();
        if (info != null)
        {
            activity_setting_spinner.setText(info.getWebUrl());
            activity_setting_tv_choosefile.setText(info.getFilePath());
        }
        if (info == null || TextUtils.isEmpty(info.getWebKeepTime()))
        {
            activity_setting_ev_keeptime.setText(ApiConstans.DEFAULT_WEB_KEEP_TIME / 1000 + "");
        } else
        {
            activity_setting_ev_keeptime.setText(info.getWebKeepTime());
        }

        List<String> stringList = new ArrayList<>();
        stringList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        stringList.add(Manifest.permission.READ_CONTACTS);
        stringList.add(Manifest.permission.CAMERA);
        stringList.add(Manifest.permission.READ_PHONE_STATE);
        PermissionUtil.requestPermisionListIfNot(this,stringList,0);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.activity_setting_spinner:
                setSpinnerPop();
                break;
            case R.id.activity_setting_tv_edit:
                new LoginDialog(SettingActivity.this, new LoginDialog.OnLoginCallback()
                {
                    @Override
                    public void onComfirmClick(String acountt, String passwordd)
                    {
//                        acount = acountt;
//                        password = passwordd;
//                        Login.saveInfo(acount, password);
                        Login login=Login.getLogin(acountt,passwordd);
                        if(ClientInfo.getInstance().isLogined()){
                            Login.isCheckWithNoLogin=true;
                        }
                        if(login!=null){
                            LoginHandler
                                    .getInstance()
                                    .login(login);
                        }

                    }
                }).show();
                break;
            case R.id.activity_setting_tv_choosefile:
//                PrintUtil.print(this);
                startActivityForResult(new Intent(SettingActivity.this, FileActivity.class), 1);
                break;
            case R.id.activity_setting_tv_plan:
                // 获取播放信息
                PlayMsgModel info = PlayMsgModel.getPlayMsgInfo();
                if (info == null)
                {
                    info = new PlayMsgModel();
                }
                info.setType("0");
                String ss = new Gson().toJson(info);
                SharedPreferencesUtils.addData(NormalUtil.PLAYMSG, ss);
                startActivity(new Intent(SettingActivity.this, UpdateActivity.class));
                finish();
                break;
            case R.id.activity_setting_tv_save:
                ApiClient.getInstance().onDataReceive("{\"id\":\"PLP25693508756099\",\"data\":[{\"startTime\":1490063448000,\"endTime\":1521599448000,\"dataMap\":{\"split\":[{\"height\":1,\"id\":45,\"isDel\":1,\"name\":\"haha\",\"platformId\":1,\"templateId\":21,\"width\":1,\"x\":0,\"y\":0,\"action\":[{\"actionCode\":\"click\",\"actionName\":\"666\",\"id\":72,\"objId\":45,\"objType\":1,\"resultContent\":\"http://vzcouture.com/shoppingGuide/index.html\",\"resultType\":1,\"taskId\":56,\"waitTime\":10000,\"waitUnit\":\"ms\"}]}],\"splitTasks\":[{\"planId\":58,\"windowingId\":45,\"tasks\":[{\"name\":\"测试\",\"type\":1,\"typeRelated\":\"1\",\"startTime\":1490063448000,\"schedule\":[{\"count\":100000,\"playTime\":0,\"start\":\"00:00:00\",\"end\":\"23:55:00\",\"fileType\":2,\"files\":[{\"count\":1,\"playTime\":0,\"adminId\":1,\"basePath\":\"1/audio/art.mp4\",\"createTime\":1488346378000,\"fileFormat\":\"mp4\",\"fileMd5\":\"5b4529270f4ffbb64c05e3ddd822386f\",\"fileMemory\":2700624,\"fileName\":\"art\",\"fileNo\":\"nr25537459701487\",\"filePath\":\"/contentFile/1/audio/art.mp4\",\"fileUrl\":\"\",\"id\":92,\"isDel\":1,\"platformId\":1,\"rank\":2,\"showPath\":\"audio/art.mp4\",\"state\":1,\"supId\":61,\"supIds\":\",0,,61,\",\"type\":2,\"uploadTime\":1488346378000,\"url\":\"http://47.100.1.173/contentFile/upload/1/36/hj.mp4\"}]}]}]}]}}],\"method\":\"plan\",\"type\":\"request\"}");

                PlayMsgModel playMsg = PlayMsgModel.getPlayMsgInfo();
                if (playMsg == null)
                {
                    playMsg = new PlayMsgModel();
                }
                if (activity_setting_rbtn_webpage.isChecked())
                {
                    if (validate())
                    {
                        playMsg.setWebKeepTime(activity_setting_ev_keeptime
                                .getText()
                                .toString());
                        playMsg.setType("1");
                        playMsg.setWebUrl(activity_setting_spinner
                                .getText()
                                .toString());
                        String s = new Gson().toJson(playMsg);
                        SharedPreferencesUtils.addData(NormalUtil.PLAYMSG, s);
                        startActivity(new Intent(SettingActivity.this, UpdateActivity.class));
                        finish();
                    }
                } else if (activity_setting_rbtn_vedio.isChecked())
                {
                    if (playMsg.getPlayList() == null || playMsg
                            .getPlayList()
                            .size() == 0)
                    {
                        NormalUtil.showToast("请选择播放文件");
                    } else
                    {
                        playMsg.setType("2");
                        String s = new Gson().toJson(playMsg);
                        SharedPreferencesUtils.addData(NormalUtil.PLAYMSG, s);
                        startActivity(new Intent(SettingActivity.this, UpdateActivity.class));
                        finish();
                    }
                }
                break;
        }
    }

    private boolean validate()
    {
        if (activity_setting_spinner
                .getText()
                .length() == 0)
        {
            NormalUtil.showToast("请选择网址");
            return false;
        }
        if (activity_setting_ev_keeptime
                .getText()
                .length() == 0)
        {
            NormalUtil.showToast("请填写静默时间");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null&&data.getStringExtra("filePath")!=null)
        {
            activity_setting_tv_choosefile.setText(data.getStringExtra("filePath"));
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ApiClient
                .getInstance()
                .removeApiClientListener(this);
    }

    @Override
    protected boolean isColorStatusBar()
    {
        return false;
    }

    @Override
    public void onInited()
    {
    }

    @Override
    public void onConnected()
    {
        activity_setting_tv_connectstatus.setText("连接状态：已连接");
    }

    @Override
    public void onConnectFailed(Exception e)
    {
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
        activity_setting_tv_connectstatus.setText("连接状态：未连接");
        activity_setting_tv_status.setText("登录状态：未登录");
        activity_setting_tv_no.setText("登录账号：无");
    }

    @Override
    public void onLoginSucceed()
    {
        activity_setting_tv_status.setText("登录状态：已登录");
        activity_setting_tv_no.setText("登录账号："+SharedPreferencesUtils.readData(ApiConstans.LOGIN_ID));
//        if (!TextUtils.isEmpty(acount) && !TextUtils.isEmpty(password))
//        {
//            SharedPreferencesUtils.addData("acount", acount);
//            SharedPreferencesUtils.addData("password", password);
//        }

    }

    @Override
    public void onLoginFailed(String msg)
    {
        if (ClientInfo
                .getInstance()
                .isLogined())
        {
            activity_setting_tv_status.setText("登录状态：已登录");
            activity_setting_tv_no.setText("登录账号："+SharedPreferencesUtils.readData(ApiConstans.LOGIN_ID));
        } else if (!TextUtils.isEmpty(msg))
        {
            Utils.toastShort(msg);
        }
    }
}
