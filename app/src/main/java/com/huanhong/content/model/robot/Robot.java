package com.huanhong.content.model.robot;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by admin on 2017/5/11.
 */

public class Robot {
    // 语音听写对象
    private SpeechRecognizer mSpeechRecognizer;

    // 语音合成对象
    private SpeechSynthesizer mSpeechSynthesizer;

    //是否繁忙（正在播放语音）
    private boolean mBusying = false;

    private Context mContext;

    public Robot(Context context) {
        mContext = context;

        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mSpeechRecognizer = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int i) {
                log("mSpeechRecognizer init code = " + i);
            }
        });
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

        // 初始化合成对象
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
            @Override
            public void onInit(int i) {
                log("mSpeechSynthesizer init code = " + i);
            }
        });
        if (mSpeechSynthesizer != null) {
            // 根据合成引擎设置相应参数
            mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
            //设置合成语速
            mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
            //设置合成音调
            mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, "50");
            //设置合成音量
            mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "50");
            //设置播放器音频流类型
            mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");
            // 设置播放合成音频打断音乐播放，默认为true
            mSpeechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        }
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mSpeechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
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
        stopSpeaking();
        int ret = mSpeechRecognizer.startListening(new RecognizerListener() {

            StringBuilder mStringBuilder = new StringBuilder();

            @Override
            public void onVolumeChanged(int i, byte[] bytes) {
                log("机器人正在倾听...,音量: " + i);
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
                    if (!mBusying) {
                        if (!TextUtils.isEmpty(text)) {
                            mBusying = true;
                            GetResponseAsyc getResponseAsyc = new GetResponseAsyc();
                            getResponseAsyc.execute(text);
                        }
                    }
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

    public void startSpeaking(String text) {
        if (mSpeechSynthesizer == null) {
            log("播放失败: mSpeechSynthesizer == null");
            return;
        }

        stopSpeaking();
        int code = mSpeechSynthesizer.startSpeaking(text, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {
                mBusying = true;
                log("播放开始");
            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {
                log("播放暂停");
            }

            @Override
            public void onSpeakResumed() {
                log("播放继续");
            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {
                if (speechError == null) {
                    log("播放完成");
                } else {
                    log("播放失败:" + speechError.toString());
                }
                mBusying = false;
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
//			int code = mSpeechSynthesizer.synthesizeToUri(text, path, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            log("播放失败,错误码：" + code);
        }
    }

    public void stopSpeaking() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stopSpeaking();
        }
        mBusying = false;
    }

    public void onPause() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.cancel();
            log("听写取消");
        }

        if (mSpeechSynthesizer != null) {
            stopSpeaking();
        }
    }

    public void onDestroy() {
        if (null != mSpeechRecognizer) {
            // 退出时释放连接
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }

        if (null != mSpeechSynthesizer) {
            mSpeechSynthesizer.stopSpeaking();
            // 退出时释放连接
            mSpeechSynthesizer.destroy();
        }
    }

    public void log(String msg) {
        Log.e(getClass().getSimpleName(), msg);
    }

    class GetResponseAsyc extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL("http://182.254.152.39/interactive-consumer/AI/ai.do");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream outputStream = urlConnection.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream);
                printWriter.write("text=" + params[0] + "&userid=1");
                printWriter.flush();

                StringBuilder stringBuilder = new StringBuilder();
                char[] bytes = new char[1024];
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream(), Charset.forName("UTF-8"));
                while (reader.read(bytes, 0, bytes.length) != -1) {
                    stringBuilder.append(bytes);
                }

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                return jsonObject.getString("data");
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            startSpeaking(s);
        }
    }
}
