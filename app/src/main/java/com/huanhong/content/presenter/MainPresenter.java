package com.huanhong.content.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;
import com.huanhong.content.model.PlayMsgModel;
import com.huanhong.content.model.info.ClientInfo;
import com.huanhong.content.model.info.HistoryInfo;
import com.huanhong.content.model.plan.Plan;
import com.huanhong.content.model.plan.PlanManagerDKN;
import com.huanhong.content.model.plan.PlanSwitch;
import com.huanhong.content.model.plan.ScheduleManager;
import com.huanhong.content.model.plan.server.ServerContent;
import com.huanhong.content.model.plan.server.ServerPlanContent;
import com.huanhong.content.model.plan.server.ServerSchedule;
import com.huanhong.content.model.plan.server.ServerSplit;
import com.huanhong.content.model.plan.server.ServerSplitSchedule;
import com.huanhong.content.model.plan.server.ServerTask;
import com.huanhong.content.model.split.SplitItem;
import com.huanhong.content.model.split.SplitLayout;
import com.huanhong.content.model.split.adaptive.AdaptiveSplitItem;
import com.huanhong.content.view.i.MainView;
import com.zyn.lib.presenter.BasePresenter;
import com.zyn.lib.util.FileUtils;
import com.zyn.lib.util.GsonUtils;
import com.zyn.lib.util.HttpUtils;
import com.zyn.lib.util.ThreadUtils;
import com.zyn.lib.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.huanhong.content.view.view.robot.RobotLayout;


public class MainPresenter extends BasePresenter<MainView> implements PlanManagerDKN.TotalPlanListener {
    private RelativeLayout mRootLayout;
    private SplitLayout mSplitLayout;
    private PlanManagerDKN mPlanManager;
    private ServerPlanContent planContent;
//    private CameraFaceManager mCameraFaceManager;
    private Map<String, ScheduleManager> mScheduleManagerMap = new HashMap<>();
//    private RobotLayout mRobotLayout;

    public MainPresenter(Context context, MainView view) {
        super(context, view);
        init();
        //开启摄像头
//        mCameraFaceManager = new CameraFaceManager((Activity) getContext());
        ClientInfo
                .getInstance()
                .setMainPresenter(this);
    }

    private void init() {
        //lock锁死屏幕
        getView().lock();

        //绑定plan switch监听
        PlanSwitch.setPlanSwitchListener(new PlanSwitch.PlanSwitchListener() {
            String mStrSplitSchedule;
            String mStrSchedule;

            @Override
            public void onSwitchSplitSchedule(int total, int curr) {
                getView().showBack(null);
                mStrSplitSchedule = "正在检查各分屏计划：" + curr + "/" + total;
                getView().setShowText(mStrSplitSchedule);
            }

            @Override
            public void onSwitchSchedule(int total, int curr) {
                mStrSchedule = "\n正在检查分屏各时段任务：" + curr + "/" + total;
                getView().setShowText(mStrSplitSchedule + mStrSchedule);
            }

            @Override
            public void onSwitchFinished(ServerPlanContent planContent) {
                try {
                    Plan plan = new Plan();
                    plan.setPlanContent((JsonObject) GsonUtils
                            .getGson()
                            .toJsonTree(planContent));
                    plan.setNeedSwitch(false);
                    startPlan(plan);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //创建分屏layout
        mSplitLayout = new SplitLayout(getContext());

        //创建root
        mRootLayout = new RelativeLayout(getContext());
        mRootLayout.addView(mSplitLayout,new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //计划管理者
        mPlanManager = new PlanManagerDKN(this);

        //添加语音机器人
//        mRobotLayout = new RobotLayout(getContext());
//        getView().showFront(mRobotLayout);

//        Log.e("======","===="+HistoryInfo.hasPlan());
//        if (!HistoryInfo.hasPlan()) {
//            ApiClient.getInstance().onDataReceive("{\"id\":\"PLP25693508756099\",\"data\":[{\"startTime\":1490063448000,\"endTime\":1521599448000,\"dataMap\":{\"split\":[{\"height\":1,\"id\":45,\"isDel\":1,\"name\":\"haha\",\"platformId\":1,\"templateId\":21,\"width\":1,\"x\":0,\"y\":0,\"action\":[{\"actionCode\":\"click\",\"actionName\":\"666\",\"id\":72,\"objId\":45,\"objType\":1,\"resultContent\":\"http://vzcouture.com/shoppingGuide/index.html\",\"resultType\":1,\"taskId\":56,\"waitTime\":10000,\"waitUnit\":\"ms\"}]}],\"splitTasks\":[{\"planId\":58,\"windowingId\":45,\"tasks\":[{\"name\":\"测试\",\"type\":1,\"typeRelated\":\"1\",\"startTime\":1490063448000,\"schedule\":[{\"count\":100000,\"playTime\":0,\"start\":\"00:00:00\",\"end\":\"23:55:00\",\"fileType\":2,\"files\":[{\"count\":1,\"playTime\":0,\"adminId\":1,\"basePath\":\"1/audio/art.mp4\",\"createTime\":1488346378000,\"fileFormat\":\"mp4\",\"fileMd5\":\"5b4529270f4ffbb64c05e3ddd822386f\",\"fileMemory\":2700624,\"fileName\":\"art\",\"fileNo\":\"nr25537459701487\",\"filePath\":\"/contentFile/1/audio/art.mp4\",\"fileUrl\":\"\",\"id\":92,\"isDel\":1,\"platformId\":1,\"rank\":2,\"showPath\":\"audio/art.mp4\",\"state\":1,\"supId\":61,\"supIds\":\",0,,61,\",\"type\":2,\"uploadTime\":1488346378000,\"url\":\"http://47.100.1.173/contentFile/upload/1/36/hj.mp4\"}]}]}]}]}}],\"method\":\"plan\",\"type\":\"request\"}");
//        }


//        ApiClient.getInstance().onDataReceive("{\"id\":\"PLP25693508756099\",\"data\":[{\"createTime\":1511260817000,\"creatorType\":1,\"dataMap\":{\"split\":[{\"action\":[{\"actionCode\":\"click\",\"actionName\":\"点击\",\"id\":81,\"objId\":123,\"objType\":1,\"resultContent\":\"www.baidu.com\",\"resultType\":1,\"taskId\":103,\"waitTime\":5000,\"waitUnit\":\"ms\"}],\"height\":1.0,\"id\":123,\"isDel\":1,\"name\":\"\",\"platformId\":1,\"templateId\":45,\"width\":1.0,\"x\":0.0,\"y\":0.0}],\"tasks\":[{\"executionMode\":1,\"id\":330,\"planId\":103,\"schedule\":[{\"end\":\"23:55:59\",\"endTime\":57359000,\"fileType\":4,\"files\":[ {\"count\": 1, \"playTime\": 0, \"adminId\": 1, \"basePath\": \"1/audio/art.mp4\", \"createTime\": 1488346378000,\"fileFormat\": \"mp4\", \"fileMd5\": \"5b4529270f4ffbb64c05e3ddd822386f\", \"fileMemory\": 2700624, \"fileName\": \"art\", \"fileNo\": \"nr25537459701487\", \n" +
//                "\"filePath\": \"/contentFile/1/audio/art.mp4\", \"fileUrl\": \"\", \"id\": 92, \"isDel\": 1, \"platformId\": 1, \"rank\": 2,  \"showPath\": \"audio/art.mp4\", \"state\": 1, \"supId\": 61,  \"supIds\": \",0,,61,\",\"type\": 2, \"uploadTime\": 1488346378000,  \"url\": \"http://47.100.1.173/contentFile/upload/1/36/hj.mp4\"                          }],\"id\":2,\"isDel\":1,\"planId\":103,\"playOrder\":2,\"start\":\"00:00:00\",\"startTime\":-28800000,\"taskDay\":1,\"taskName\":\"1\",\"taskNo\":\"TN28470521678471\",\"windowingId\":123}],\"windowingId\":123}]},\"deviceids\":\"69,70,71,72\",\"endTime\":1512057599000,\"id\":103,\"isDel\":1,\"isInherit\":2,\"isPriority\":2,\"lcdevicePlanId\":280,\"planLevel\":1,\"planName\":\"11\",\"planNo\":\"PN28470507899910\",\"planObj\":1,\"planType\":2,\"platformId\":1,\"sendResponse\":3,\"startTime\":1511193600000,\"state\":1,\"templateId\":45}],\"method\":\"plan\",\"type\":\"request\"}");

        //检查当前设备的设置，判断按计划或本地播放
        checkConfig();
    }

    private void deinit() {
        stopPlan();
        mPlanManager.stop();
        PlanSwitch.setPlanSwitchListener(null);
//        mRobotLayout.onDestroy();
        getView().showFront(null);
        getView().unlock();
    }

    private void checkConfig() {
        PlayMsgModel playMsgModel = PlayMsgModel.getPlayMsgInfo();

        if (playMsgModel == null) {
            onPlan();
        } else {
            if (playMsgModel
                    .getType()
                    .equals(PlayMsgModel.TYPE_PLAN)) {
                onPlan();
            } else {
                onLocal(playMsgModel);
            }
        }
    }

    private void onPlan() {
        if (HistoryInfo.hasPlan()) {
            getView().showBack(mRootLayout);
            play(HistoryInfo.getPlanList());
        } else {
            getView().showBack(null);
        }
    }

    private void onLocal(@NonNull PlayMsgModel msgModel) {
        //web
        if (msgModel
                .getType()
                .equals(PlayMsgModel.TYPE_LOCAL_WEB)) {
            String url = msgModel.getWebUrl();
            if (TextUtils.isEmpty(url)) {
                getView().showBack(null);
            } else {
                List<String> list = new ArrayList<>();
                list.add(HttpUtils.addHttpScheme(url));
                play(getFullScreenPlanByType(ServerSchedule.TYPE_H5, list));
            }
        }
        //video
        else if (msgModel
                .getType()
                .equals(PlayMsgModel.TYPE_LOCAL_VIDEO)) {
            List<String> pathList = msgModel.getPlayList();
            if (Utils.isEmptyList(pathList)) {
                getView().showBack(null);
            } else {
                play(getFullScreenPlanByType(ServerSchedule.TYPE_VIDEO, pathList));
            }
        }
    }

    private List<Plan> getFullScreenPlanByType(String type, List<String> list) {
        String id = "0";

        ServerSplit split = new ServerSplit();
        split.setId(id);
        split.setX(0);
        split.setY(0);
        split.setWidth(1);
        split.setHeight(1);
        List<ServerSplit> splitList = new ArrayList<>();
        splitList.add(split);

        List<ServerContent> serverContentList = new ArrayList<>();
        for (String s : list) {
            ServerContent serverContent = new ServerContent();
            serverContent.setLocal(true);
            serverContent.setUrl(s);
            serverContent.setShowPath(s);
            serverContent.setType(type);
            serverContentList.add(serverContent);
        }

        List<ServerSchedule> serverScheduleList = new ArrayList<>();
        ServerSchedule serverSchedule = new ServerSchedule();
        serverSchedule.setContentList(serverContentList);
        serverScheduleList.add(serverSchedule);

        List<ServerTask> serverTaskList = new ArrayList<>();
        ServerTask serverTask = new ServerTask();
        serverTask.setType(type);
        serverTask.setStartTime(0);
        serverTask.setSchedule(serverScheduleList);
        serverTaskList.add(serverTask);

        List<ServerSplitSchedule> ServerSplitScheduleList = new ArrayList<>();
        ServerSplitSchedule serverSplitSchedule = new ServerSplitSchedule();
        serverSplitSchedule.setId(id);
        serverSplitSchedule.setTasks(serverTaskList);
        ServerSplitScheduleList.add(serverSplitSchedule);

        //开始plan
        ServerPlanContent planContent = new ServerPlanContent();
        planContent.setSplit(splitList);
        planContent.setSplitTasks(ServerSplitScheduleList);

        Plan plan = new Plan();
        plan.setPlanName("前端播放");
        plan.setNeedSwitch(false);
        plan.setPlanContent(GsonUtils
                .getGson()
                .toJsonTree(planContent)
                .getAsJsonObject());
        plan.setStartTime(System.currentTimeMillis());
        plan.setEndTime(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

        List<Plan> planList = new ArrayList<>();
        planList.add(plan);
        return planList;
    }

    private void play(List<Plan> list) {
        mPlanManager.setPlanList(list);
        mPlanManager.start();
    }


    @Override
    public void startPlan(final Plan plan) {
        if (plan == null) {
            getView().showBack(null);
            return;
        }

        //先暂停之前的plan
        stopPlan();


        try {
            planContent = GsonUtils
                    .getGson()
                    .fromJson(plan.getPlanContent(), ServerPlanContent.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //若转换完成为null 空闲
        if (planContent == null) {
            return;
        }
        List<ServerSplitSchedule> splitScheduleList=null;
        List<ServerSplit> splitList = planContent.getSplit();
        if(planContent.getSplitTasks()!=null&&planContent.getSplitTasks().size()>0){
            splitScheduleList = planContent.getSplitTasks();
            if (splitList == null || splitScheduleList == null) {
                getView().showBack(null);
                return;
            }
        }


        List<SplitItem> splitItemList = new ArrayList<>();
        for (int i = 0; i < splitList.size(); i++) {
            //分屏
            ServerSplit split = splitList.get(i);
            if (split == null) {
                continue;
            }
            AdaptiveSplitItem splitItem = new AdaptiveSplitItem(getContext(), split);
            splitItem.setActionList(split.getAction());
            splitItemList.add(splitItem);

            //寻找对应的total schedule
            ServerSplitSchedule splitSchedule = new ServerSplitSchedule();
            if(splitScheduleList==null){return;}
            for (ServerSplitSchedule t : splitScheduleList) {
                if (t != null
                        && split.getId() != null
                        && split
                        .getId()
                        .equals(t.getId())) {
                    splitSchedule .setId(t.getId());
                    splitSchedule.setTasks(t.getTasks());
                    break;
                }
            }

            //创建schedule manager管理total schedule
            if (splitSchedule != null) {
                ScheduleManager scheduleManager = new ScheduleManager(splitItem);
                scheduleManager.setSplitSchedule(splitSchedule);
                mScheduleManagerMap.put(split.getId(), scheduleManager);
            }
        }

        mSplitLayout.split(splitItemList);
        getView().showBack(mRootLayout);

        //开启所有schedule manager
        for (ScheduleManager scheduleManager : mScheduleManagerMap.values()) {
            scheduleManager.start();
        }
    }


    @Override
    public void stopPlan() {
        if (mScheduleManagerMap != null) {
            for (ScheduleManager scheduleManager : mScheduleManagerMap.values()) {
                if (scheduleManager != null) {
                    scheduleManager.stop();
                }
            }
            mScheduleManagerMap.clear();
        }

        if (mSplitLayout != null) {
            mSplitLayout.reset();
        }

        getView().showBack(null);
    }

    public Map<String, Object> getCurrContent() {
        Map<String, Object> map = new HashMap<>();
        if (mSplitLayout != null) {
            List<SplitItem> splitItemList = mSplitLayout.getSplitItemList();
            if (splitItemList != null) {
                for (SplitItem splitItem : splitItemList) {
                    ServerSplit split = splitItem.getSplit();
                    if (split != null) {
                        map.put(split.getId(), splitItem.getCurrContent());
                    }
                }
            }
        }
        return map;
    }

    public void getShot(final String id, final ShotListener listener) {
        ThreadUtils.runOnWorkThread(new Runnable() {
            String path;

            @Override
            public void run() {
                if (mSplitLayout != null) {
                    if (id.equals("0")) {
                        Bitmap bitmap = mSplitLayout.getViewShot();
                        if (bitmap != null) {
                            String path = FileUtils.getDiskCacheDirPath(getContext()) + "/cache.jpg";
                            Log.e(getClass().getSimpleName(), "截屏保存成功");
                            FileUtils.saveBitmapToFile(bitmap, path);
                            this.path = path;
                        }
                    } else {
                        List<SplitItem> splitItemList = mSplitLayout.getSplitItemList();
                        if (splitItemList != null) {
                            for (SplitItem splitItem : splitItemList) {
                                ServerSplit split = splitItem.getSplit();
                                if (split != null) {
                                    if (split.getId() != null && split
                                            .getId()
                                            .equals(id)) {
                                        Bitmap bitmap = splitItem.getBitmapShot();
                                        if (bitmap != null) {
                                            String path = FileUtils.getDiskCacheDirPath(getContext()) + "/cache.jpg";
                                            Log.e(getClass().getSimpleName(), "截屏保存成功");
                                            FileUtils.saveBitmapToFile(bitmap, path);
                                            this.path = path;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (listener != null) {
                    listener.onFinish(path);
                }
            }
        }, 0);
    }

    @Override
    public void onResume() {
//        mCameraFaceManager.onResume();
    }

    @Override
    public void onPause() {
//        mCameraFaceManager.onPause();
    }

    @Override
    public void onDestory() {
        super.onDestory();
        ClientInfo
                .getInstance()
                .setMainPresenter(null);
//        mCameraFaceManager.onDestroy();
        deinit();
    }
}
