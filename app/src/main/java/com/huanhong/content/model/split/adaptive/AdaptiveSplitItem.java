package com.huanhong.content.model.split.adaptive;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.widget.RelativeLayout;

import com.huanhong.content.R;
import com.huanhong.content.model.face.CameraFaceManager;
import com.huanhong.content.model.plan.ScheduleManager;
import com.huanhong.content.model.plan.server.ServerAction;
import com.huanhong.content.model.plan.server.ServerContent;
import com.huanhong.content.model.plan.server.ServerSplit;
import com.huanhong.content.model.robot.JsonParser;
import com.huanhong.content.model.split.SplitItem;
import com.huanhong.content.view.view.DispatchLayout;
import com.huanhong.content.view.view.TimerWebView;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.zyn.lib.util.Utils;
import com.zyn.lib.util.ViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


public class AdaptiveSplitItem extends SplitItem implements ScheduleManager.TotalScheduleListener, TimerWebView.TimerWebViewListener
{
    private final RelativeLayout.LayoutParams mParams;
    private DispatchLayout mRootView;
    private TimerWebView mWebView; //用作触摸弹出
    private Adapter mAdapter;
    private List<ServerAction> mActionList; //分屏action列表
    private ServerAction mAction; //当前action
    private Context context;
    private SpeechRecognizer mSpeechRecognizer;
    private String[] goods={"EE","芦荟胶","叶子","抽纸","防晒霜","面膜","口红","眼霜","卸妆棉"};
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            log("InitListener init() code = " + code);
        }
    };

    public AdaptiveSplitItem(Context context, ServerSplit split)
    {
        super(context, split);

        this.context=context;
        mSpeechRecognizer = SpeechRecognizer.createRecognizer(context, mInitListener);
        if (mSpeechRecognizer != null) {
            // 设置听写引擎
            mSpeechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            //设置领域
            mSpeechRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
            // 设置返回结果格式
            mSpeechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
            //设置语言
            mSpeechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
            mSpeechRecognizer.setParameter(SpeechConstant.VAD_BOS, Long.MAX_VALUE + "");
            // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//          mSpeechRecognizer.setParameter(SpeechConstant.VAD_EOS, Long.MAX_VALUE+"");
        }

        //root
        mRootView = new DispatchLayout(context);
        mRootView.setBackgroundColor(getContext()
                .getResources()
                .getColor(R.color.lib_black));
        mParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        //弹出h5
        mWebView = new TimerWebView(getContext());
        mWebView.setTimerWebViewListener(this);
        mWebView.setVisibility(View.GONE);
        mRootView.addView(mWebView, mParams);
        mRootView.setDispatchListener(new DispatchLayout.DispatchListener()
        {
            @Override
            public void onDispatchTouchEvent(MotionEvent ev)
            {
                if (ev.getActionMasked() == MotionEvent.ACTION_DOWN
                        && !isDoActionIng())
                {
                    doAction(getDoActionList());
                }
            }
        });

        EventBus.getDefault().register(this);

        //空闲状态
        playNull();
    }


    private void initAdapter()
    {
        if (mAdapter != null)
        {
            mAdapter.init();
            View view = mAdapter.getView();
            if (view != null)
            {
                mRootView.addView(view, mParams);
            }
        }
    }

    private void deinitAdapter()
    {
        if (mAdapter != null)
        {
            mRootView.removeView(mAdapter.getView());
            mAdapter.deinit();
        }
    }

    @Override
    public View getView()
    {
        return mRootView;
    }

    @Override
    public void recycle()
    {
        EventBus.getDefault().unregister(this);
        mWebView.deinit();
        mWebView = null;
        deinitAdapter();
        mRootView.removeAllViews();
        mRootView = null;
        mAction = null;
    }

    @Override
    public void playWeb(List<ServerContent> url)
    {
        play(new WebAdapter(getContext(), url));
    }

    @Override
    public void playVideo(List<ServerContent> url, long startTime, Boolean isNeedSeekTo)
    {
        play(new VideoAdapter(getContext(), url, startTime, isNeedSeekTo));
    }

    @Override
    public void playVideoPic(List<ServerContent> url, long startTime, Boolean isNeedSeekTo) {
        play(new VideoPicAdapter(getContext(), url, startTime, isNeedSeekTo));
    }

    @Override
    public void playImages(List<ServerContent> url)
    {
        play(new ImageAdapter(getContext(), url));
    }

    @Override
    public void playNull()
    {
        play(new TextAdapter(getContext(), getContext().getString(R.string.no_plan)));
    }

    private void play(Adapter adapter)
    {
        deinitAdapter();
        mAdapter = adapter;
        if (!isDoActionIng())
        {
            initAdapter();
        }
    }

    private List<ServerAction> getDoActionList() {
        List<ServerAction> actionList = null;
        //从adapter中获得当前动作列表
        if(mAdapter!=null)
        {
            actionList = mAdapter.getCurrActionList();
        }

        //若adapter中没有则获得分屏的动作列表
        if(Utils.isEmptyList(actionList))
        {
            actionList = mActionList;
        }
        return actionList;
    }

    private void doAction(List<ServerAction> actionList) {
        if(isDoActionIng())
        {
            return;
        }

        //若动作列表不为空
        if (!Utils.isEmptyList(actionList)
                && !TextUtils.isEmpty(actionList
                .get(0)
                .getResultContent())) {
            mAction = actionList
                    .get(0);
            deinitAdapter();
            mWebView.setVisibility(View.VISIBLE);
            mWebView.bringToFront();
            mWebView.setUri(actionList
                    .get(0)
                    .getResultContent());
            Log.e("KeepTime===","==="+actionList
                    .get(0)
                    .getWaitTime());
//            mWebView.setKeepTime(actionList
//                    .get(0)
//                    .getWaitTime());

            mWebView.setKeepTime(15*1000);
            mWebView.load();

            mWebView.getmWebView().setWebChromeClient(new WebChromeClient(){
                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    if("print".equals(consoleMessage.message())){
//                        PrintUtil.print(context);
                    }
                    if("speak".equals(consoleMessage.message())){
                        startListening();
                    }
                    return super.onConsoleMessage(consoleMessage);
                }

            });


        }
    }

    private boolean isDoActionIng()
    {
        return mWebView != null && mWebView.getVisibility() == View.VISIBLE;
    }

    @Override
    public Bitmap getBitmapShot()
    {
        Bitmap bitmap = null;
        if (isDoActionIng())
        {
            bitmap = ViewUtils.getViewShot(mWebView);
        } else if (mAdapter != null)
        {
            bitmap = mAdapter.getBitmapShot();
        }
        return bitmap;
    }

    @Override
    public Object getCurrContent()
    {
        if (isDoActionIng())
        {
            return mAction;
        } else
        {
            if (mAdapter != null)
            {
                return mAdapter.getCurrContent();
            }
        }
        return null;
    }

    @Override
    public void outTime()
    {
        mAction = null;
        if (mWebView != null)
        {
            mWebView.setVisibility(View.GONE);
            mWebView.unload();
        }
        if (mAdapter == null)
        {
            playNull();
        }
        play(mAdapter);
    }

    public List<ServerAction> getActionList()
    {
        return mActionList;
    }

    public void setActionList(List<ServerAction> actionList)
    {
        mActionList = actionList;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleCameraFaceEvent(CameraFaceManager.CameraFaceEvent event)
    {
        doAction(getDoActionList());
    }

    public void log(String msg) {
        Log.e(getClass().getSimpleName(), msg);
    }

    /**
     * 开始监听
     */
    public void startListening() {
        mWebView.tv_status.setVisibility(View.VISIBLE);
        mWebView.tv_status.setText("请说出你要搜索的商品");
//        Utils.toastShort("请说出你要搜索的商品");
        if (mSpeechRecognizer == null) {
            log("听写失败: mSpeechRecognizer == null");
            return;
        }
        mSpeechRecognizer.stopListening();
//        stopSpeaking();
        int ret = mSpeechRecognizer.startListening(new RecognizerListener() {

            StringBuilder mStringBuilder = new StringBuilder();

            @Override
            public void onVolumeChanged(int i, byte[] bytes) {
                mWebView.resetKeepTimer();
//                Utils.toastShort("正在倾听...");
            }
            @Override
            public void onBeginOfSpeech() {
                log("听写开始");
                mWebView.tv_status.setVisibility(View.VISIBLE);
                mWebView.tv_status.setText("正在倾听...");
//                Utils.toastLong("开始倾听...");
                mWebView.resetKeepTimer();
            }

            @Override
            public void onEndOfSpeech() {
                mWebView.resetKeepTimer();
                mWebView.tv_status.setVisibility(View.VISIBLE);
                mWebView.tv_status.setText("结束倾听，正在搜索...");
//                Utils.toastLong("结束倾听，正在搜索...");
                log("听写结束");
            }

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                mStringBuilder.append(JsonParser.parseIatResult(recognizerResult.getResultString()));
                if (b) {
                    String text = mStringBuilder.toString();
                    mStringBuilder.delete(0, mStringBuilder.length());
                    log("听写结果：" + text);
                    boolean isHas=false;
                    for (int i=0;i<goods.length;i++){
                        if(text.contains(goods[i])){
                            isHas=true;
                           String url ="http://vzcouture.com/shoppingGuide/shop.html?name=0";
                            log("加载url：" +url+(i+1));
                            mWebView.setUri(url+(i+1));
                            mWebView.load();
                            mWebView.tv_status.setVisibility(View.VISIBLE);
                            mWebView.tv_status.setText("输入结果："+ text+"\n正在加载商品...");
                            mWebView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mWebView.tv_status.setVisibility(View.GONE);
                                }
                            },2000);
                            break;
                        }
                    }
                    if(!isHas){
                        mWebView.tv_status.setVisibility(View.VISIBLE);
                        mWebView.tv_status.setText("输入结果："+ text+"\n未搜索到正确商品...");
                        mWebView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mWebView.tv_status.setVisibility(View.GONE);
                            }
                        },2000);
                    }
                }
            }
            @Override
            public void onError(SpeechError speechError) {
                mWebView.tv_status.setVisibility(View.VISIBLE);
                mWebView.tv_status.setText("未搜索到正确商品...");
                mWebView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.tv_status.setVisibility(View.GONE);
                    }
                },2000);
                log(speechError.toString());
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
        if (ret != ErrorCode.SUCCESS) {
            log("听写失败,错误码：" + ret);
        }
    }
}
