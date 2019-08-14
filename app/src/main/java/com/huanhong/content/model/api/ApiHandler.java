package com.huanhong.content.model.api;


import android.app.Activity;
import android.content.Intent;

import com.huanhong.content.model.PlayMsgModel;
import com.huanhong.content.model.info.ClientInfo;
import com.huanhong.content.model.info.HistoryInfo;
import com.huanhong.content.model.monitor.MonitorHandler;
import com.huanhong.content.model.plan.Plan;
import com.huanhong.content.model.plan.PlanHandler;
import com.huanhong.content.model.sync.SyncHandler;
import com.huanhong.content.presenter.UpdatePresenter;
import com.huanhong.content.view.activity.UpdateActivity;
import com.zyn.lib.app.AppUtils;
import com.zyn.lib.app.BaseApplication;

import java.util.List;

public class ApiHandler implements PlanHandler.PlanHandlerListener, SyncHandler.SyncHandlerListener {
    private static ApiHandler ourInstance = new ApiHandler();

    private ApiHandler() {

    }

    public static ApiHandler getInstance() {
        return ourInstance;
    }

    public void init() {
        //初始化用户状态监听
        ClientInfo
                .getInstance()
                .init();

        //添加计划监听
        PlanHandler
                .getInstance()
                .setPlanHandlerListener(this);

        //添加同步监听
        SyncHandler
                .getInstance()
                .setSyncHandlerListener(this);

        //监听监听
        MonitorHandler.getInstance();

        //尝试连接服务器
        ApiClient
                .getInstance()
                .connect();
    }

    @Override
    public void onPlan(List<Plan> list,String json) {
        if (list == null) {
            return;
        }
        HistoryInfo.savePlanList(list);

        //更改本地播放设置
        try {
            PlayMsgModel msgModel = PlayMsgModel.getPlayMsgInfo();
            msgModel.setType(PlayMsgModel.TYPE_PLAN);
            PlayMsgModel.save(msgModel);
        } catch (Exception e) {

        }

        //准备跳转到更新页面
        final BaseApplication baseApplication = AppUtils.getBaseApplication();
        Activity activity = baseApplication.getTopActivity();

        //根据当前活动，配置intent
        Intent intent = new Intent(baseApplication, UpdateActivity.class);
        intent.putExtra(UpdatePresenter.TPYE, UpdatePresenter.TYPE_PLAN);
        intent.putExtra(UpdatePresenter.DATA, json);
        // 跳转到update页面
        baseApplication.startSingleActivity(intent);
    }

    @Override
    public void onSync(String json) {
        //准备跳转到更新页面
        final BaseApplication baseApplication = AppUtils.getBaseApplication();
        Activity activity = baseApplication.getTopActivity();

        //根据当前活动，配置intent
        Intent intent = new Intent(baseApplication, UpdateActivity.class);
        intent.putExtra(UpdatePresenter.TPYE, UpdatePresenter.TYPE_SYNC);
        intent.putExtra(UpdatePresenter.DATA, json);

        // 跳转到update页面
        baseApplication.startSingleActivity(intent);
    }
}
