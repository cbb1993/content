package com.huanhong.content.model.split;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huanhong.content.model.plan.server.ServerSplit;

/**
 * Created by tuka2401 on 2016/12/19.
 */

public class WebSplitItem extends SplitItem
{
    private WebView mWebView;

    public WebSplitItem(Context context, ServerSplit split, String url)
    {
        super(context, split);
        mWebView = new WebView(context);
        mWebView
                .getSettings()
                .setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.loadUrl(url);
    }

    @Override
    public View getView()
    {
        return mWebView;
    }

    @Override
    public void recycle()
    {
        mWebView = null;
    }
}
