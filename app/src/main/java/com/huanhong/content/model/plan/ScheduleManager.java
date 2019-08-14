package com.huanhong.content.model.plan;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.huanhong.content.model.plan.server.ServerContent;
import com.huanhong.content.model.plan.server.ServerSchedule;
import com.huanhong.content.model.plan.server.ServerSplitSchedule;
import com.zyn.lib.util.Utils;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduleManager {
    private static String TAG = ScheduleManager.class.getSimpleName();
    private ServerSplitSchedule mSplitSchedule;
    private ServerSchedule mSchedule;
    private TotalScheduleListener mTotalScheduleListener;
    private TimerTask mEndTimerTask, mNextTimerTask;
    private Timer mEndTimer, mNextTimer;
    private Handler mHandler;
    private String id;

    public ScheduleManager(TotalScheduleListener totalScheduleListener) {
        mTotalScheduleListener = totalScheduleListener;
    }

    private void onNext() {
        Log.e(TAG, id + ":" + "onNext()");

        if (mSplitSchedule == null) {
            Log.e(TAG, id + ":" + "异常: mTotalSchedule为空");
            playNull();
            return;
        }

        //获取下一计划
        if (mSchedule != null) {
            mSchedule = getNextSchedule(mSchedule);
        }
        //若获取不到下一计划，获取当前计划
        if (mSchedule == null) {
            mSchedule = getCurrSchedule();
        }

        //多次搜索还是没搜到说明确实没有schedule
        if (mSchedule == null) {
            Log.e(TAG, id + ":" + "schedule为空");
            playNull();
            return;
        }

        long currTime = System.currentTimeMillis();
        long startTime = Utils.getTimestamp(Utils.getCurrDate(), mSchedule.getStart());
        long endTime = Utils.getTimestamp(Utils.getCurrDate(), mSchedule.getEnd());
        Log.e(TAG, id + ":" + "当前计划:");
        Log.e(TAG, id + ":" + "当前时间:" + Utils.getStrTimestamp(currTime));
        Log.e(TAG, id + ":" + "开始时间:" + Utils.getStrTimestamp(startTime));
        Log.e(TAG, id + ":" + "结束时间:" + Utils.getStrTimestamp(endTime));

        //多种情况的判断
        if (startTime <= currTime) {
            play();
            ServerSchedule next = getNextSchedule(mSchedule);
            if (next == null) {
                Log.e(TAG, id + ":" + "没有下一计划");
                runEndTimer(endTime);
            } else {
                long nextStartTime = Utils.getTimestamp(Utils.getCurrDate(), next.getStart());
                if (nextStartTime > endTime) {
                    runEndTimer(endTime);
                }
                Log.e(TAG, id + ":" + "下一计划开始时间:" + Utils.getStrTimestamp(nextStartTime));
                runNextTimer(nextStartTime);
            }
        } else {
            mSchedule = null;
            Log.e(TAG, id + ":" + "当前没有计划，下一计划开始时间:" + Utils.getStrTimestamp(startTime));
            runNextTimer(startTime);
        }
    }

    private void runNextTimer(long nextStartTime) {
        if (mNextTimerTask != null) {
            mNextTimerTask.cancel();
        }
        mNextTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onNext();
                    }
                });
            }
        };
        mNextTimer.schedule(mNextTimerTask, new Date(nextStartTime));
    }

    private void runEndTimer(long endTime) {
        if (mEndTimerTask != null) {
            mEndTimerTask.cancel();
        }
        mEndTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onEnd();
                    }
                });
            }
        };
        mEndTimer.schedule(mEndTimerTask, new Date(endTime));
    }

    private void onEnd() {
        Log.e(TAG, id + ":" + "onEnd()");
        playNull();
    }

    private void play() {
        if (mSchedule == null) {
            Log.e(TAG, id + ":" + "异常: 没有当前计划");
            playNull();
            return;
        }

        List<ServerContent> urlList = mSchedule.getContentList();
        if (Utils.isEmptyList(urlList)) {
            Log.e(TAG, id + ":" + "异常: 没有content");
            playNull();
        }

        String type = mSchedule.getFileType();
        switch (type) {
            case ServerSchedule.TYPE_H5: {
                if (mTotalScheduleListener != null) {
                    Log.e(TAG, id + ":" + "开始展示网页:" + urlList.toString());
                    mTotalScheduleListener.playWeb(urlList);
                }
            }
            break;
            case ServerSchedule.TYPE_IMAGE: {
                if (mTotalScheduleListener != null) {
                    mTotalScheduleListener.playImages(urlList);
                    Log.e(TAG, id + ":" + "开始播放轮播图:" + urlList.toString());
                }
            }
            break;
            case ServerSchedule.TYPE_VIDEO: {
                if (mTotalScheduleListener != null) {
                    mTotalScheduleListener.playVideoPic(urlList, Utils.getTimestamp(Utils.getCurrDate(), mSchedule.getStart()), false);
                    Log.e(TAG, id + ":" + "开始播放视频和图片:" + urlList.toString());
                }
            }
//            case ScheduleMedia.TYPE_VIDEO: {
//                if (mTotalScheduleListener != null) {
//                    mTotalScheduleListener.playVideo(urlList, Utils.getTimestamp(Utils.getCurrDate(), mSchedule.getStartTime()), false);
//                    Log.e(TAG, id + ":" + "开始播放视频:" + urlList.toString());
//                }
//            }
            break;
            default: {
                Log.e(TAG, id + ":" + "异常: 计划类别未识别");
                playNull();
            }
            break;
        }
    }

    private void playNull() {
        if (mTotalScheduleListener != null) {
            mTotalScheduleListener.playNull();
        }
    }

    public void setSplitSchedule(ServerSplitSchedule splitSchedule) {
        mSplitSchedule = splitSchedule;
        id = mSplitSchedule.getId();
    }

    private ServerSchedule getCurrSchedule() {
        if (mSplitSchedule == null) {
            return null;
        }

        List<ServerSchedule> scheduleList = mSplitSchedule.getTasks().get(0).getSchedule();
        if (Utils.isEmptyList(scheduleList)) {
            return null;
        }

        for (int i = 0; i < scheduleList.size(); i++) {
            ServerSchedule schedule = scheduleList.get(i);
            long time = System.currentTimeMillis();
            long start = Utils.getTimestamp(Utils.getCurrDate(), schedule.getStart());
            long end = Utils.getTimestamp(Utils.getCurrDate(), schedule.getEnd());

            //如果现在正在开始时间和结束时间之间或开始时间在之后
            if (time >= start && time <= end || start > time) {
                return schedule;
            }
        }

        return null;
    }

    private ServerSchedule getNextSchedule(ServerSchedule curr) {
        if (mSplitSchedule == null) {
            return null;
        }

        List<ServerSchedule> scheduleList = mSplitSchedule.getTasks().get(0).getSchedule();
        if (Utils.isEmptyList(scheduleList)) {
            return null;
        }

        int now = scheduleList.indexOf(curr);
        if (now + 1 < scheduleList.size()) {
            return scheduleList.get(now + 1);
        }
        return null;
    }

    public long getTimestamp(String time) {
        Date date = new Date(time);
        return date.getTime();
    }

    public void start() {
        //计时器
        mEndTimer = new Timer();
        mNextTimer = new Timer();

        // 主线程handler
        mHandler = new Handler(Looper.getMainLooper());

        //开始执行
        onNext();
    }

    public void stop() {
        onEnd();

        if (mEndTimerTask != null) {
            mEndTimerTask.cancel();
            mEndTimerTask = null;
        }

        if (mEndTimer != null) {
            mEndTimer.cancel();
            mEndTimer = null;
        }

        if (mNextTimerTask != null) {
            mNextTimerTask.cancel();
            mNextTimerTask = null;
        }

        if (mNextTimer != null) {
            mNextTimer.cancel();
            mNextTimer = null;
        }

        mHandler = null;
        mSchedule = null;
    }

    public TotalScheduleListener getTotalScheduleListener() {
        return mTotalScheduleListener;
    }

    public void setTotalScheduleListener(TotalScheduleListener totalScheduleListener) {
        mTotalScheduleListener = totalScheduleListener;
    }

    public interface TotalScheduleListener {
        void playWeb(List<ServerContent> url);

        void playVideo(List<ServerContent> url, long startTime, Boolean isNeedSeekTo);

        void playVideoPic(List<ServerContent> url, long startTime, Boolean isNeedSeekTo);

        void playImages(List<ServerContent> url);

        void playNull();
    }

}