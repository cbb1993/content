package com.huanhong.content.model.plan;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.zyn.lib.util.Utils;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class PlanManager {
    private static String TAG = PlanManager.class.getSimpleName();
    private List<Plan> mPlanList;
    private Plan mPlan;
    private TotalPlanListener mTotalPlanListener;
    private TimerTask mResetTimerTask, mEndTimerTask, mNextTimerTask;
    private Timer mResetTimer, mEndTimer, mNextTimer;
    private Handler mHandler;

    public PlanManager(TotalPlanListener listener) {
        mTotalPlanListener = listener;
    }

    private void onNext() {
        Log.e(TAG, "onNext()");

        if (Utils.isEmptyList(mPlanList)) {
            Log.e(TAG, "异常: mTotalPlanList为空");
            playNull();
            return;
        }

        if (mPlan != null) {
            mPlan = getNextSchedule(mPlan);
        }

        if (mPlan == null) {
            mPlan = getCurrTotalPlan();
        }

        if (mPlan == null) {
            Log.e(TAG, "schedule为空");
            playNull();
            return;
        }

        long currTime = System.currentTimeMillis();
        long startTime = mPlan.getStartTime();
        long endTime = mPlan.getEndTime();
        Log.e(TAG, "当前计划:");
        Log.e(TAG, "当前时间:" + Utils.getStrTimestamp(currTime));
        Log.e(TAG, "开始时间:" + Utils.getStrTimestamp(startTime));
        Log.e(TAG, "结束时间:" + Utils.getStrTimestamp(endTime));

        //多种情况的判断
        if (startTime <= currTime) {
            play();
            Plan next = getNextSchedule(mPlan);
            if (next == null) {
                Log.e(TAG, "没有下一计划");
                runEndTimer(endTime);
            } else {
                long nextStartTime = next.getStartTime();
                if (nextStartTime > endTime) {
                    runEndTimer(endTime);
                }
                Log.e(TAG, "下一计划开始时间:" + Utils.getStrTimestamp(nextStartTime));
                runNextTimer(nextStartTime);
            }
        } else {
            mPlan = null;
            Log.e(TAG, "当前没有计划，下一计划开始时间:" + Utils.getStrTimestamp(startTime));
            playNull();
            runNextTimer(startTime);
        }
    }

    private void runResetTimer() {
        if (mResetTimerTask != null) {
            mResetTimerTask.cancel();
        }
        mResetTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        stop();
                        start();
                    }
                });
            }
        };
        long nextDay = Utils.getNextDayTimestamp();
        mResetTimer.schedule(mResetTimerTask, new Date(nextDay));
        Log.e(TAG, "下次刷新计划时间:" + Utils.getStrTimestamp(nextDay));
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
        Log.e(TAG, "onEnd()");
        playNull();
    }

    private void play() {
        if (mPlan == null) {
            Log.e(TAG, "异常: 没有当前计划");
            playNull();
            return;
        }

        play(mPlan);
    }

    private void play(Plan plan) {
        Log.e(TAG, "开始播放计划:" + plan.getPlanName());
        if (mTotalPlanListener != null) {
            mTotalPlanListener.startPlan(plan);
        }
    }

    private void playNull() {
        if (mTotalPlanListener != null) {
            mTotalPlanListener.stopPlan();
        }
    }

    public void setPlanList(List<Plan> planList) {
        mPlanList = planList;
    }

    private Plan getCurrTotalPlan() {
        if (Utils.isEmptyList(mPlanList)) {
            return null;
        }

        for (Plan plan : mPlanList) {
            long time = System.currentTimeMillis();
            long start = plan.getStartTime();
            long end = plan.getEndTime();

            //如果现在正在开始时间和结束时间之间或开始时间在之后
            if (time >= start && time <= end || start > time) {
                return plan;
            }
        }

//        //迪卡侬需求 返回最后一个计划
//        return mPlanList.get(mPlanList.size()-1);

        return null;
    }

    private Plan getNextSchedule(Plan curr) {
        if (Utils.isEmptyList(mPlanList)) {
            return null;
        }

        int now = mPlanList.indexOf(curr);
        if (now + 1 < mPlanList.size()) {
            return mPlanList.get(now + 1);
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
        mResetTimer = new Timer();

        // 主线程handler
        mHandler = new Handler(Looper.getMainLooper());

        //零点刷新
        runResetTimer();

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
        if (mResetTimerTask != null) {
            mResetTimerTask.cancel();
            mResetTimerTask = null;
        }
        if (mResetTimer != null) {
            mResetTimer.cancel();
            mResetTimer = null;
        }

        mHandler = null;
        mPlan = null;
    }

    public TotalPlanListener getTotalPlanListener() {
        return mTotalPlanListener;
    }

    public interface TotalPlanListener {
        void startPlan(Plan plan);

        void stopPlan();
    }
}
