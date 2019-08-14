package com.zyn.lib.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.umeng.analytics.MobclickAgent;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class BaseApplication extends MultiDexApplication
{
    private final Object mActivityLock = new Object();
    private List<Activity> mList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);

        //分包
        MultiDex.install(this);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        FileDownloader.init(getApplicationContext(),
                new FileDownloadHelper.OkHttpClientCustomMaker() { // is not has to provide.
                    @Override
                    public OkHttpClient customMake() {
                        // just for OkHttpClient customize.
                        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        // you can set the connection timeout.
                        builder.connectTimeout(10, TimeUnit.SECONDS);
                        // you can set the HTTP proxy.
                        builder.proxy(Proxy.NO_PROXY);
                        // etc.
                        return builder.build();
                    }
                });
        if (AppUtils
                .getCurProcessName(getApplicationContext())
                .equals(getPackageName()))
        {
//            初始化
            initApplication();

        }
    }

    protected void initApplication()
    {
        //捕获全局异常
     /*   Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread thread, Throwable ex)
            {
                //退出程序
                ex.printStackTrace();
//                Log.e("app", ex.getMessage(), ex);
//                Utils.toastShort(getApplicationContext(), getString(R.string.lib_uncaught_exception_hint));
                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                exit();
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(handler);*/

        //设置全局applicaiton
        AppUtils.initBaseApplication(this);

        //初始化友盟统计分析
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setDebugMode(true);

        //初始化图片加载
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig
                .newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, imagePipelineConfig);


    }

    public void startSingleActivity(Intent intent)
    {
        synchronized (mActivityLock)
        {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finishActivities();
            startActivity(intent);
        }
    }

    public Activity getTopActivity()
    {
        synchronized (mActivityLock)
        {
            return mList.get(mList.size() - 1);
        }
    }

    public void addActivity(Activity activity)
    {
        synchronized (mActivityLock)
        {
            mList.add(activity);
        }
    }

    public void removeActivity(Activity activity)
    {
        synchronized (mActivityLock)
        {
            mList.remove(activity);
        }
    }

    public void finishActivity(Class<? extends Activity> clazz)
    {
        synchronized (mActivityLock)
        {
            Activity activity = getActivity(clazz);
            if(activity!=null)
            {
                activity.finish();
            }
        }
    }

    public Activity getActivity(Class<? extends Activity> clazz)
    {
        synchronized (mActivityLock)
        {
            for (Activity activity : mList)
            {
                if (activity != null && activity
                        .getClass()
                        .equals(clazz))
                {
                    return activity;
                }
            }
        }
        return null;
    }

    public void finishActivities()
    {
        synchronized (mActivityLock)
        {
            for (Activity activity : mList)
            {
                if (activity != null && !activity.isFinishing())
                {
                    activity.finish();
                }
            }
            reset();
        }
    }


    public void reset()
    {
        synchronized (mActivityLock)
        {
            if (mList != null)
            {
                mList.clear();
            }
        }

        System.gc();
    }


    public void exit()
    {
        synchronized (mActivityLock)
        {

            finishActivities();
            reset();
            mList = null;
            MobclickAgent.onKillProcess(this);
            System.exit(0);
        }
    }

    public void onLowMemory()
    {
        super.onLowMemory();
        System.gc();
    }
}
