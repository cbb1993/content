package com.huanhong.content.view.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huanhong.content.R;
import com.huanhong.content.model.PlayMsgModel;
import com.huanhong.content.model.api.ApiConstans;
import com.zyn.lib.util.HttpUtils;

import java.util.Timer;
import java.util.TimerTask;


public class TimerWebView extends RelativeLayout
{
    private final long DEFAULT_KEEP_TIME = ApiConstans.DEFAULT_WEB_KEEP_TIME;
    private long mKeepTime = DEFAULT_KEEP_TIME;
    private Timer mKeepTimer;
    private TimerTask mKeepTimerTask;
    private Handler mMainHandler;
    private String mUri;
    private TimerWebViewListener mTimerWebViewListener;
    private View mRoot;
    private WebView mWebView;
    private ProgressBar mLoading;
    public TextView tv_status;

    public TimerWebView(Context context)
    {
        super(context);
        init();
    }

    public WebView getmWebView() {
        return mWebView;
    }

    public void init()
    {
        mRoot = LayoutInflater
                .from(getContext())
                .inflate(R.layout.view_timer_web, this, true);
        mWebView = (WebView) mRoot.findViewById(R.id.view_timer_web);
        mWebView.setBackgroundColor(Color.BLACK);
        mLoading = (ProgressBar) mRoot.findViewById(R.id.view_timer_web_loading);
        mLoading.setVisibility(View.GONE);

        tv_status= (TextView) mRoot.findViewById(R.id.tv_status);
        tv_status.setVisibility(GONE);

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
                    mKeepTime = keepTime;
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

//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon)
//            {
//                super.onPageStarted(view, url, favicon);
//                mLoading.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url)
//            {
//                super.onPageFinished(view, url);
//                mLoading.setVisibility(View.GONE);
//            }
        });
        mWebView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {

                if (newProgress >= 100) {
                    mLoading.setVisibility(View.GONE);
                } else {
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
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
    }

    public void load()
    {
        if (TextUtils.isEmpty(mUri))
        {
            Log.e(getClass().getSimpleName(), "异常: 网址为空");
            return;
        }

        unload();
        mWebView.loadUrl(HttpUtils.addHttpScheme(mUri));
        resetKeepTimer();
    }

    public void unload()
    {
        mWebView.clearCache(false);
        mWebView.clearHistory();
        mWebView.clearFormData();
        mWebView.loadUrl("");
        if (mKeepTimerTask != null)
        {
            mKeepTimerTask.cancel();
            mKeepTimerTask = null;
        }
    }

    public void resetKeepTimer()
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
                        if (mTimerWebViewListener != null)
                        {
                            mTimerWebViewListener.outTime();
                        }
                    }
                });
            }
        };
        mKeepTimer.schedule(mKeepTimerTask, mKeepTime);
    }

    public void deinit()
    {
        unload();
        mWebView.destroy();
        mWebView = null;
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

    public long getKeepTime()
    {
        return mKeepTime;
    }

    public void setKeepTime(long keepTime)
    {
        if (keepTime > 0)
        {
            mKeepTime = keepTime;
        }
    }

    public String getUri()
    {
        return mUri;
    }

    public void setUri(String uri)
    {
        if (!TextUtils.isEmpty(uri))
        {
            mUri = uri;
        }
    }

    public TimerWebViewListener getTimerWebViewListener()
    {
        return mTimerWebViewListener;
    }

    public void setTimerWebViewListener(TimerWebViewListener timerWebViewListener)
    {
        mTimerWebViewListener = timerWebViewListener;
    }

    public interface TimerWebViewListener
    {
        void outTime();
    }
}
