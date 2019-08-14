package com.huanhong.content.model.plan;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huanhong.content.model.plan.server.ServerAction;
import com.huanhong.content.model.plan.server.ServerContent;
import com.huanhong.content.model.plan.server.ServerPlanContent;
import com.huanhong.content.model.plan.server.ServerSchedule;
import com.huanhong.content.model.plan.server.ServerSplit;
import com.huanhong.content.model.plan.server.ServerSplitSchedule;
import com.huanhong.content.model.plan.server.ServerTask;
import com.huanhong.content.util.FileUtil;
import com.zyn.lib.util.MediaUtils;
import com.zyn.lib.util.ThreadUtils;
import com.zyn.lib.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tuka2401 on 2017/3/20.
 */

//负责plan的转换工作，从服务器提供的plan转换为我们支持格式的plan
public class PlanSwitch {
    public static PlanSwitchListener mPlanSwitchListener;

    public static void switchPlanContent(final JsonObject jPlanContent) {

        ThreadUtils.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                if (jPlanContent != null) {
                    final ServerPlanContent planContent = new ServerPlanContent();
                    try {
                        ServerPlanContent serverPlanContent = new Gson().fromJson(jPlanContent, ServerPlanContent.class);
                        planContent.setSplit(serverPlanContent.getSplit());
                        planContent.setSplitTasks(serverPlanContent.getSplitTasks());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (mPlanSwitchListener != null) {
                            ThreadUtils.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    mPlanSwitchListener.onSwitchFinished(planContent);
                                }
                            }, 0);
                        }
                    }
                }
            }
        }, 0);
    }

    @NonNull
    private static List<ServerSplit> switchSplit(List<ServerSplit> serverSplitList) {
        List<ServerSplit> splitList = new ArrayList<>();
        //转换split
        if (serverSplitList != null) {
            for (ServerSplit serverSplit : serverSplitList) {
                ServerSplit split = new ServerSplit();
                split.setId(serverSplit.getId());
                split.setX(serverSplit.getX());
                split.setY(serverSplit.getY());
                split.setWidth(serverSplit.getWidth());
                split.setHeight(serverSplit.getHeight());
                split.setAction(switchAction(serverSplit.getAction()));
                splitList.add(split);
            }
        }
        return splitList;
    }


    @NonNull
    private static List<ServerAction> switchAction(List<ServerAction> serverActionList) {
        List<ServerAction> actionList = new ArrayList<>();
        if (serverActionList != null) {
            for (ServerAction serverAction : serverActionList) {
                ServerAction action = new ServerAction();
                action.setId(serverAction.getId());
                action.setResultContent(serverAction.getResultContent());
                action.setActionCode(serverAction.getActionCode());
                action.setActionName(serverAction.getActionName());
                action.setObjType(serverAction.getObjType());
                action.setResultType(serverAction.getResultType());
                action.setWaitTime(serverAction.getWaitTime());
                action.setWaitUnit(serverAction.getWaitUnit());
                actionList.add(action);
            }
        }
        return actionList;
    }


    @NonNull
    public static List<ServerContent> getContentList(JsonObject jPlanContent) {
        List<ServerContent> list = new ArrayList<>();
        if (jPlanContent != null) {
            ServerPlanContent serverPlanContent = new Gson().fromJson(jPlanContent, ServerPlanContent.class);
            List<ServerSplitSchedule> serverSplitScheduleList = serverPlanContent.getSplitTasks();
            if (serverSplitScheduleList != null) {
                for (ServerSplitSchedule serverSplitSchedule : serverSplitScheduleList) {
                    List<ServerTask> serverTaskList = serverSplitSchedule.getTasks();
                    if (serverTaskList != null) {
                        for (ServerTask serverTask : serverTaskList) {
                            List<ServerSchedule> serverScheduleList = serverTask.getSchedule();
                            if (serverScheduleList != null) {
                                for (ServerSchedule serverSchedule : serverScheduleList) {
                                    return  serverSchedule.getContentList();
//                                    List<Content> contentList = switchContent(serverSchedule.getContentList());
//                                    if(contentList!=null){
//                                        for (Content content : contentList) {
//                                            list.addAll(content.getLcFiles());
//                                        }
//                                    }

                                }
                            }
                        }
                    }

                }
            }
        }
        return list;
    }

//    private static long getPlayTime(Content content) {
//        if (content != null) {
//            return content.getPlayTime() > 0 ? content.getPlayTime() : MediaUtils.getMediaDuration(FileUtil.getPathByShowPath(content
//                    .getShowPath()));
//        }
//        return 0;
//    }

    public static PlanSwitchListener getPlanSwitchListener() {
        return mPlanSwitchListener;
    }

    public static void setPlanSwitchListener(PlanSwitchListener planSwitchListener) {
        mPlanSwitchListener = planSwitchListener;
    }

    public interface PlanSwitchListener {
        void onSwitchSplitSchedule(int total, int curr);

        void onSwitchSchedule(int total, int curr);

        void onSwitchFinished(ServerPlanContent planContent);
    }
}
