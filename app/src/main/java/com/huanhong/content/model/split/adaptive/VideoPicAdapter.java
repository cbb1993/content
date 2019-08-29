package com.huanhong.content.model.split.adaptive;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.huanhong.content.R;
import com.huanhong.content.model.plan.server.ServerContent;
import com.huanhong.content.util.FileUtil;
import com.huanhong.content.util.NormalUtil;
import com.huanhong.content.util.ScreenUtil;
import com.iflytek.cloud.thirdparty.V;
import com.zyn.lib.adapter.CommonAdapter;
import com.zyn.lib.adapter.ViewHolder;
import com.zyn.lib.util.MediaUtils;
import com.zyn.lib.util.ThreadUtils;
import com.zyn.lib.util.Utils;
import com.zyn.lib.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPicAdapter extends Adapter {
    private RelativeLayout mRoot;
    private VideoView mVideoView;
    private RecyclerView recycle_pic;
    private List<ServerContent> mPathList;
    private String mCurr;
    private int mCount = -1;
    private long mStartTime = 0;
    private Boolean mIsNeedSeekTo = true;
    private View rl_right;
    private ImageView iv_open;
    private boolean open = true;
    private int webTime = 10_000; // 30没有触摸
    private WebView webview;
    private Timer mKeepTimer;
    private TimerTask mKeepTimerTask;
    private Handler mMainHandler;
    private static final long DOUBLE_TIME = 1000;
    private static long lastClickTime = 0;
    private int current ;

    private boolean pause =false;
    class ItemData{
        String name,no;
        int pic;

    public ItemData(int pic, String name, String no) {
        this.pic = pic;
        this.name = name;
        this.no = no;
    }
}

private List<ItemData> itemData =new ArrayList<>();
    public VideoPicAdapter(Context context, List<ServerContent> pathList, long startTime, boolean isNeedSeekTo) {
        super(context);
        mPathList = pathList;
        mStartTime = startTime;
        mIsNeedSeekTo = isNeedSeekTo;
        itemData.add(new ItemData(
             R.mipmap.pic_test1,
             "Dyson戴森 吹风机",
                "https://detail.tmall.com/item.htm?spm=a1z10.4-b-s.w16608505-14629593645.2.53a8f0c9ERxxHI&id=536027498869"
        ));
         itemData.add(new ItemData(
                 R.mipmap.pic_test2,
                     "Dyson戴森V11 Absolute智能无线吸尘器",
                        "https://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.1.65a22065GoeOkg&id=589345613482&skuId=4030847957373&areaId=310100&user_id=2089100916&cat_id=2&is_b=1&rn=9dd4b764e9c33fcd81690ac2e568aec9"
                ));

    }

    private void startAnim(){
        open = !open;
        float translationX = rl_right.getTranslationX();
        ObjectAnimator animator ;
        if(!open){
             animator
                    = ObjectAnimator.ofFloat(rl_right, "translationX",
                    translationX, translationX + ScreenUtil.dpToPx(getContext(),200));
        }else {
             animator
                    = ObjectAnimator.ofFloat(rl_right, "translationX",
                    translationX, translationX - ScreenUtil.dpToPx(getContext(),200));
        }
        animator.setDuration(500);
        animator.start();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(open){
                    iv_open.setImageResource(R.mipmap.dk_jt);
                }else {
                    iv_open.setImageResource(R.mipmap.sq_jt);
                }
            }
        },500);
    }

    @Override
    public void init() {
        mRoot = new RelativeLayout(getContext());

        if (mPathList == null) {
            return;
        }
        //初始化计时器
        mKeepTimer = new Timer();
        mMainHandler = new Handler(Looper.getMainLooper());

        //创建并添加 view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_pic_video, null);

        rl_right = view.findViewById(R.id.rl_right);
        iv_open = (ImageView) view.findViewById(R.id.iv_open);
        webview = (WebView) view.findViewById(R.id.webview);
        initWeb();
        mVideoView = (VideoView) view.findViewById(R.id.video);
        recycle_pic = (RecyclerView) view.findViewById(R.id.recycle_pic);
        recycle_pic.setLayoutManager(new LinearLayoutManager(getContext()));
        recycle_pic.setAdapter(new CommonAdapter<ServerContent>(getContext(), R.layout.item_video, mPathList) {
            @Override
            public void convert(final ViewHolder holder, ServerContent content) {
                TextView tv_title = holder.getView(R.id.tv_title);
                ImageView iv_ = holder.getView(R.id.iv_);
                View root = holder.getView(R.id.root);
//                View iv_left = holder.getView(R.id.view_left);
//                tv_title.setText(content.getFileName());
                tv_title.setText(itemData.get(holder.getRealPosition()).name);

                if (mCount == holder.getRealPosition()) {
                    iv_.setVisibility(View.VISIBLE);
//                    iv_left.setVisibility(View.VISIBLE);
                } else {
                    iv_.setVisibility(View.GONE);
//                    iv_left.setVisibility(View.GONE);
                }
                Glide.with(getContext())
                        .load(itemData.get(holder.getRealPosition()).pic)
                        .into(iv_);
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startP(holder.getRealPosition());
                    }
                });
            }
        });

        iv_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnim();
            }
        });

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mRoot.addView(view, layoutParams);

        //视频播放完成回调
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e(getClass().getSimpleName(), " 视频结束");
                // 播放网页
                pause=false;
                showWeb();
            }
        });

        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - lastClickTime < DOUBLE_TIME) {
                    current=mVideoView.getCurrentPosition();
                    pause=true;
                    mVideoView.pause();
                    webview.loadUrl(itemData.get(mCount).no);
                    resetPauseKeepTimer();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webview.setVisibility(View.VISIBLE);
                        }
                    },1000);
                }
                lastClickTime = currentTimeMillis;
            }
        });

        ThreadUtils.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                long duration = 0;
                if (mIsNeedSeekTo) {
                    duration = seekTo(System.currentTimeMillis() - mStartTime);
                }
                final long finalDuration = duration;
                ThreadUtils.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        startNextVideo((int) 0);
                    }
                }, 0);
            }
        }, 0);
    }

    private void stop(){
        if (mVideoView == null) {
            return;
        }
        //先暂停
        mVideoView.pause();
        mVideoView.stopPlayback();
    }

    private void showWeb(){
        stop();
        webview.loadUrl(itemData.get(mCount).no);
        resetKeepTimer();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                webview.setVisibility(View.VISIBLE);
            }
        },1000);

    }

    private void showVideo(){
        webview.setVisibility(View.GONE);
        startNextVideo(0);
    }

    private void initWeb(){
        webview
                .getSettings()
                .setJavaScriptEnabled(true);
        webview
                .getSettings()
                .setDomStorageEnabled(true);
        webview
                .getSettings()
                .setCacheMode(WebSettings.LOAD_DEFAULT);
        webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webview.setWebViewClient(new WebViewClient()
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
        webview.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_UP)
                {
                    if(pause){
                        resetPauseKeepTimer();
                    }else {
                        resetKeepTimer();
                    }
                }
                return false;
            }
        });
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
                        // 继续播放视频
                        showVideo();
                    }
                });
            }
        };
        mKeepTimer.schedule(mKeepTimerTask, webTime);
    }

    private void resetPauseKeepTimer()
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
                        // 继续播放视频
                        if(mVideoView!=null){
                            webview.setVisibility(View.GONE);
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mVideoView.start();
                                }
                            },500);
                        }
                    }
                });
            }
        };
        mKeepTimer.schedule(mKeepTimerTask, webTime);
    }

    private void startNextVideo(int seekTo) {
        if (mVideoView == null) {
            return;
        }
        //先暂停
        mVideoView.pause();
        mVideoView.stopPlayback();

        //获取下一个需要播放的视频
        mCurr = getNextUrl();

        if(mCount==0){
            mCurr = mCurr.replace("o_1cvf1eh901s2lol434utib1k13e","test1") ;
        }else if(mCount==1){
            mCurr = mCurr.replace("o_1cvf1e9md1nbuviksgkth86pb","test2") ;
        }

        mVideoView.setVideoPath(mCurr);
        mVideoView.start();
        if (seekTo > 0) {
            mVideoView.seekTo(seekTo);
        }
        recycle_pic.getAdapter().notifyDataSetChanged();
    }

    private void startP(int p) {
        if (mKeepTimerTask != null)
        {
            mKeepTimerTask.cancel();
        }
        webview.setVisibility(View.GONE);
        if (mVideoView == null) {
            return;
        }
        //先暂停
        mVideoView.pause();
        mVideoView.stopPlayback();
        mCurr = FileUtil.getPathByShowPath(mPathList
                        .get(p)
                        .getShowPath());
        mCount = p ;
        if(mCount==0){
            mCurr = mCurr.replace("o_1cvf1eh901s2lol434utib1k13e","test1") ;
        }else if(mCount==1){
            mCurr = mCurr.replace("o_1cvf1e9md1nbuviksgkth86pb","test2") ;
        }

        mVideoView.setVideoPath(mCurr);
        mVideoView.start();
        recycle_pic.getAdapter().notifyDataSetChanged();
    }

    @Override
    public View getView() {
        return mRoot;
    }

    @Override
    public ServerContent getCurrContent() {
        try {
            return mPathList.get(mCount);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deinit() {
        if (mVideoView != null) {
            mVideoView.pause();
            mVideoView.stopPlayback();
            mVideoView = null;
        }

        if (mRoot != null) {
            mRoot.removeAllViews();
            mRoot = null;
        }

        if (webview != null)
        {
            webview.clearCache(false);
            webview.clearHistory();
            webview.clearFormData();
            webview.removeAllViews();
            webview.destroy();
            webview = null;
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

    @Override
    public Bitmap getBitmapShot() {
        if (mVideoView != null && !TextUtils.isEmpty(mCurr)) {
            //获得view背景
            Bitmap bg = ViewUtils.getViewShot(getView());
            if (bg == null) {
                return null;
            }

            //获得视频帧
            Bitmap bitmap = MediaUtils.getMediaKeyFrame(FileUtil.getPathByShowPath(mCurr), mVideoView.getCurrentPosition());
            if (bitmap != null) {
                Canvas canvas = new Canvas(bg);
                int bgWidth = bg.getWidth();
                int bgHeight = bg.getHeight();
                float bgRatio = (float) bgWidth / bgHeight;
                int bmWidth = bitmap.getWidth();
                int bmHeight = bitmap.getHeight();
                float bmRatio = (float) bmWidth / bmHeight;
                Rect rect = new Rect();
                //如果view宽高比大于视频宽高比
                if (bgRatio > bmRatio) {
                    int width = (int) (bgHeight * bmRatio);
                    rect.set((bgWidth - width) / 2, 0, bgWidth - (bgWidth - width) / 2, bgHeight);
                } else {
                    int height = (int) (bgWidth / bmRatio);
                    rect.set(0, (bgHeight - height) / 2, bgWidth, bgHeight - (bgHeight - height) / 2);
                }
                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), rect, null);
            }
            return bg;
        }
        return null;
    }

    private String getNextUrl() {
        if (mPathList == null) {
            return "";
        }

        if (++mCount >= mPathList.size()) {
            if (Utils.isEmptyList(mPathList)) {
                mCount = -1;
            } else {
                mCount = 0;
            }
        }
        Log.e(getClass().getSimpleName(), "当前播放次序: " + mCount);
        try {
            return FileUtil.getPathByShowPath(mPathList
                    .get(mCount)
                    .getShowPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private long seekTo(long seekTo) {
        if (mPathList == null || seekTo < 0) {
            return 0;
        }

        //计数初始化
        mCount = -1;

        //获得播放列表总时长
        long total = getTotalMediaDuration();
        if (total == 0) {
            return 0;
        }

        //取余
        seekTo = seekTo % total;

        //计算seekTo位置
        for (int i = 0; i < mPathList.size(); i++) {
            long duration = getMediaDuration(mPathList.get(i));
            if (duration > seekTo) {
                //count这里先-1，为了之后还要+1
                mCount = i - 1;
                return seekTo;
            } else {
                seekTo = seekTo - duration;
            }
        }

        return 0;
    }

    private long getMediaDuration(ServerContent content) {
        if (content != null) {
            return content.getPlayTime() > 0 ? content.getPlayTime() : MediaUtils.getMediaDuration(FileUtil.getPathByShowPath(content
                    .getShowPath()));
        }
        return 0;
    }

    private long getTotalMediaDuration() {
        if (mPathList == null) {
            return 0;
        }

        long total = 0;
        for (ServerContent content : mPathList) {
            total += getMediaDuration(content);

        }
        return total;
    }


}
