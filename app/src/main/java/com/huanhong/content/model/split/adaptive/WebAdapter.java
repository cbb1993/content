package com.huanhong.content.model.split.adaptive;

import android.content.Context;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huanhong.content.model.PlayMsgModel;
import com.huanhong.content.model.api.ApiConstans;
import com.huanhong.content.model.plan.server.ServerContent;
import com.zyn.lib.util.HttpUtils;
import com.zyn.lib.util.Utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tuka2401 on 2017/1/9.
 */

public class WebAdapter extends Adapter
{
    private final long DEFAULT_KEEP_TIME = ApiConstans.DEFAULT_WEB_KEEP_TIME;
    private WebView mWebView;
    private List<ServerContent> mUrlList;
    private long mKeepTime = DEFAULT_KEEP_TIME;
    private Timer mKeepTimer;
    private TimerTask mKeepTimerTask;
    private Handler mMainHandler;

    public WebAdapter(Context context, List<ServerContent> urlList)
    {
        super(context);
        mUrlList = urlList;
    }

    @Override
    public void init()
    {
        if (Utils.isEmptyList(mUrlList))
        {
            return;
        }

        //初始化计时器
        mKeepTimer = new Timer();
        mMainHandler = new Handler(Looper.getMainLooper());

        //设置静默时间
        mKeepTime = DEFAULT_KEEP_TIME;
        PlayMsgModel playMsgModel = PlayMsgModel.getPlayMsgInfo();
        if (playMsgModel != null)
        {
            String s = playMsgModel.getWebKeepTime();
            try
            {
                long keepTime = Long.parseLong(s);
                if (keepTime > 0)
                {
                    mKeepTime = keepTime * 1000;
                } else
                {
                    mKeepTime = DEFAULT_KEEP_TIME;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        //web
        mWebView = new WebView(getContext());
        mWebView
                .getSettings()
                .setJavaScriptEnabled(true);
        mWebView
                .getSettings()
                .setDomStorageEnabled(true);
        mWebView
                .getSettings()
                .setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error)
            {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if (url.startsWith("http:") || url.startsWith("https:"))
                {
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }
        });
        mWebView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_UP)
                {
                    resetKeepTimer();
                }
                return false;
            }
        });
        load();
    }

    private void load()
    {
        if (Utils.isEmptyList(mUrlList) || mWebView == null)
        {
            return;
        }

        String url = mUrlList
                .get(0)
                .getUrl();
        mWebView.loadUrl(HttpUtils.addHttpScheme(url));
    }

    private void resetKeepTimer()
    {
        if (mKeepTimerTask != null)
        {
            mKeepTimerTask.cancel();
        }

        mKeepTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                mMainHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        load();
                    }
                });
            }
        };
        mKeepTimer.schedule(mKeepTimerTask, mKeepTime);
    }

    @Override
    public View getView()
    {
        return mWebView;
    }

    @Override
    public ServerContent getCurrContent()
    {
        try
        {
            return mUrlList.get(0);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deinit()
    {
        if (mWebView != null)
        {
            mWebView.clearCache(false);
            mWebView.clearHistory();
            mWebView.clearFormData();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }

        if (mKeepTimerTask != null)
        {
            mKeepTimerTask.cancel();
            mKeepTimerTask = null;
        }

        if (mKeepTimer != null)
        {
            mKeepTimer.cancel();
            mKeepTimer = null;
        }

        mMainHandler = null;
    }
}
