package com.huanhong.content.model.sync;

import com.huanhong.content.model.DownLoadModel;
import com.huanhong.content.model.info.ClientInfo;
import com.huanhong.content.model.info.HistoryInfo;
import com.huanhong.content.model.plan.Plan;
import com.huanhong.content.model.plan.PlanSwitch;
import com.huanhong.content.model.plan.server.ServerContent;
import com.huanhong.content.util.FileDownLoadUtil;
import com.huanhong.content.util.FileJsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理内容同步、检查计划所需内容是否存在，不存在就用指定url下载到指定位置
 * 内部已经处理 ClientInfo.getInstance().setSyncing(true|false);
 */
public class SyncManager {
    public static final String TYPE_PLAN = "plan";
    public static final String TYPE_SYNC = "sync";
    public static SyncManager mSyncManager;

    private SyncManager() {
    }

    public synchronized static SyncManager getInstance() {
        if (mSyncManager == null) {
            mSyncManager = new SyncManager();
        }
        return mSyncManager;
    }

    /**
     * 开始同步内容或检查计划
     * @param type
     * @param data
     */
    public void sync(String type, String data) {

//        if (ClientInfo.getInstance().isSyncing()) {
//            EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_BUSING, "同步进行中，拒绝再次同步"));
//            return;
//        }

        //开始同步
        ClientInfo
                .getInstance()
                .setSyncing(true);
        EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_START, "同步开始"));

        if (type != null) {
            switch (type) {
                case TYPE_PLAN: {
                    checkAndPlay();
                }
                break;
                case TYPE_SYNC: {
                    EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_SYNC_START, "同步文件开始..."));
                    FileJsonUtil.compare(data, new FileDownLoadUtil.DownLoadCallback() {
                        @Override
                        public void downLoadIng(int index, int size, int percent) {
                            EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_MSG, "同步文件中" + "(" + index + "/" + size + ")" + " 当前:" + percent + "%..."));
                        }

                        @Override
                        public void downLoadStart() {

                        }

                        @Override
                        public void downLoadComplete() {

                        }

                        @Override
                        public void synchronizeComplete() {
                            EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_SYNC_FINISH, "同步文件完成"));
                            SyncHandler
                                    .getInstance()
                                    .response(true, true, "同步文件完成");
                            checkAndPlay();
                        }

                        @Override
                        public void error(String error) {
                            EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_ERROR, "同步文件异常"));
                            SyncHandler
                                    .getInstance()
                                    .response(false, false, "同步文件异常");
                            checkAndPlay();
                        }
                    });
                }
                break;
                default: {
                    EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_ERROR, "同步参数错误"));
                    checkAndPlay();
                }
            }
        } else {
            checkAndPlay();
        }
    }

    private void checkAndPlay() {
        if (HistoryInfo.hasPlan()) {
            List<DownLoadModel> downLoadModelList = new ArrayList<>();
            List<Plan> planList = HistoryInfo.getPlanList();
            if (planList == null) {
                finish();
                return;
            }

            for (Plan plan : planList) {
                List<ServerContent> list = PlanSwitch.getContentList(plan.getPlanContent());
                if (list != null) {
                    for (ServerContent content : list) {
                        downLoadModelList.add(new DownLoadModel(content));
                    }
                }
            }

            EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_CHECK_START, "检查播放计划开始.."));
            FileJsonUtil.DownLoadFiles(downLoadModelList, new FileDownLoadUtil.DownLoadCallback() {
                @Override
                public void downLoadIng(int index, int size, int percent) {
                    EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_MSG, "同步文件中" + "(" + index + "/" + size + ")" + " 当前:" + percent + "%..."));
                }

                @Override
                public void downLoadStart() {

                }

                @Override
                public void downLoadComplete() {

                }

                @Override
                public void synchronizeComplete() {
                    EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_CHECK_FINISH, "检查播放计划完成"));
                    finish();
                }

                @Override
                public void error(String error) {
                    EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_ERROR, "检查播放计划失败，无法播放计划" + "\n\n" + error));
                    ClientInfo.getInstance().setSyncing(false);
                }
            });
        } else {
            finish();
        }
    }

    private void finish() {
        //结束同步
        ClientInfo
                .getInstance()
                .setSyncing(false);
        EventBus.getDefault().post(new SyncEvent(SyncEvent.TYPE_FINISH, "同步完成，正在准备播放..."));
    }


    public static class SyncEvent {
        public static final int TYPE_START = 1;
        public static final int TYPE_MSG = 2;
        public static final int TYPE_ERROR = 0;
        public static final int TYPE_SYNC_START = 3;
        public static final int TYPE_SYNC_FINISH = 4;
        public static final int TYPE_CHECK_START = 5;
        public static final int TYPE_CHECK_FINISH = 6;
        public static final int TYPE_FINISH = 7;
        public static final int TYPE_BUSING = 8;

        private int type;
        private String msg;

        public SyncEvent(int type, String msg) {
            this.type = type;
            this.msg = msg;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
