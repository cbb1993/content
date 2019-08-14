package com.huanhong.content.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huanhong.content.R;
import com.huanhong.content.model.robot.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.zyn.lib.activity.BaseActivity;

public class SearchActivity extends BaseActivity {
    @Override
    protected int getContentViewId() {
        return R.layout.activity_search;
    }
    // 语音听写对象
    private SpeechRecognizer mSpeechRecognizer;

    //监听按钮
    private View mBtnListening;

    //显示结果
    private TextView tv_result;

    private Toast mToast;

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            log("InitListener init() code = " + code);
        }
    };

    @Override
    protected void initView() {
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        //初始化view
        tv_result = (TextView) findViewById(R.id.tv_result);
        mBtnListening = findViewById(R.id.tv_input);

        mBtnListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });

        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mSpeechRecognizer = SpeechRecognizer.createRecognizer(this, mInitListener);
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
    }

    /**
     * 开始监听
     */
    public void startListening() {
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
                mToast.setText("正在倾听...,音量: " + i);
                mToast.show();
            }
            @Override
            public void onBeginOfSpeech() {
                log("听写开始");
            }

            @Override
            public void onEndOfSpeech() {
                log("听写结束");
            }

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                mStringBuilder.append(JsonParser.parseIatResult(recognizerResult.getResultString()));
                if (b) {
                    String text = mStringBuilder.toString();
                    mStringBuilder.delete(0, mStringBuilder.length());
                    log("听写结果：" + text);
                    tv_result.setText("听写结果：" + text);
                }
            }
            @Override
            public void onError(SpeechError speechError) {
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


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.cancel();
            log("听写取消");
        }
    }

    @Override

    protected void onDestroy() {
        super.onDestroy();

        if (null != mSpeechRecognizer) {
            // 退出时释放连接
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }
    }

    public void log(String msg) {
        Log.e(getClass().getSimpleName(), msg);
    }


}
