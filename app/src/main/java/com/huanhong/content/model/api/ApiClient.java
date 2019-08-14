package com.huanhong.content.model.api;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.huanhong.content.model.api.netty.NettyClient;
import com.zyn.lib.constant.HostConstants;
import com.zyn.lib.util.GsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient implements NettyClient.NettyClientListener
{
    private static ApiClient mInstance;
    private static NettyClient mNettyClient;
    private final String TAG = getClass().getSimpleName();
    private final int DELAY_CONNECT = ApiConstans.DELAY_CONNECT_TIME;
    private Map<String, List<OnResponseListener>> mOnResponseListenerMap = new HashMap<>();
    private Map<String, List<OnRequestListener>> mOnRequestListenerMap = new HashMap<>();
    private List<ApiClientListener> mApiClientListenerList = new ArrayList<>();
    private Gson mGson;

    private ApiClient()
    {
        //把接收到的data转换成jsonObject
        mGson = new GsonBuilder()
                .registerTypeAdapter(ApiProtocol.class, new JsonDeserializer<ApiProtocol>()
                {
                    @Override
                    public ApiProtocol deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
                    {
                        if (json == null || json.isJsonNull() || !json.isJsonObject())
                        {
                            return null;
                        }

                        ApiProtocol apiProtocol = GsonUtils
                                .getGson()
                                .fromJson(json, ApiProtocol.class);
                        JsonElement data = null;
                        try
                        {
                            data = json
                                    .getAsJsonObject()
                                    .getAsJsonObject(ApiConstans.PROTICOL_DATA);
                        } catch (Exception e)
                        {
                            try {
                                data =  json
                                        .getAsJsonObject()
                                        .getAsJsonArray(ApiConstans.PROTICOL_DATA);
                            }catch (Exception e2)
                            {
                                e.printStackTrace();
                            }
                        }
                        apiProtocol.setData(data);
                        return apiProtocol;
                    }
                })
                .create();

        mNettyClient = NettyClient.getInstance();
        mNettyClient.setNettyClientListener(this);
        mNettyClient.setHeartBeat(mGson.toJson(ApiProtocol.getHeartBeat()));
    }

    public synchronized static ApiClient getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new ApiClient();
        }
        return mInstance;
    }

    public void connect()
    {
        mNettyClient.connect(HostConstants.HOST, HostConstants.PORT);
    }

    public void send(ApiProtocol apiProtocol)
    {
        mNettyClient.sendMessage(mGson.toJson(apiProtocol), 0);
    }

    public void send(String message)
    {
        mNettyClient.sendMessage(message, 0);
    }

    public void addOnResponseListener(String type, OnResponseListener listener)
    {
        List<OnResponseListener> listenerList = mOnResponseListenerMap.get(type);
        if (listenerList == null)
        {
            listenerList = new ArrayList<>();
        }
        listenerList.add(listener);
        mOnResponseListenerMap.put(type, listenerList);
    }

    public void removeOnResponseListener(String type, OnResponseListener listener)
    {
        List<OnResponseListener> listenerList = mOnResponseListenerMap.get(type);
        if (listenerList != null)
        {
            listenerList.remove(listener);
        }
    }

    public void addOnRequestListener(String type, OnRequestListener listener)
    {
        List<OnRequestListener> listenerList = mOnRequestListenerMap.get(type);
        if (listenerList == null)
        {
            listenerList = new ArrayList<>();
        }
        listenerList.add(listener);
        mOnRequestListenerMap.put(type, listenerList);
    }

    public void removeOnRequestListener(String type, OnRequestListener listener)
    {
        List<OnRequestListener> listenerList = mOnRequestListenerMap.get(type);
        if (listenerList != null)
        {
            listenerList.remove(listener);
        }
    }

    public void addApiClientListener(ApiClientListener listener)
    {
        mApiClientListenerList.add(listener);
    }

    public void removeApiClientListener(ApiClientListener listener)
    {
        mApiClientListenerList.remove(listener);
    }

    @Override
    public void onInited()
    {
        Log.e(TAG, "初始化成功");

        for (ApiClientListener listener : mApiClientListenerList)
        {
            if (listener != null)
            {
                listener.onInited();
            }
        }
    }

    @Override
    public void onConnected()
    {
        Log.e(TAG, "连接成功");

        for (ApiClientListener listener : mApiClientListenerList)
        {
            if (listener != null)
            {
                listener.onConnected();
            }
        }
    }

    @Override
    public void onConnectFailed(Exception e)
    {
        Log.e(TAG, "连接失败: " + e.getMessage());
        for (ApiClientListener listener : mApiClientListenerList)
        {
            if (listener != null)
            {
                listener.onConnectFailed(e);
            }
        }

        reConnect();
    }

    private void reConnect()
    {
        Log.e(TAG, "尝试重连: " + DELAY_CONNECT + "ms后");
        mNettyClient.reInit(0);
        mNettyClient.reConnect(DELAY_CONNECT);
    }

    @Override
    public void onSent(String message)
    {
        Log.e(TAG, "发送成功: " + message);
        for (ApiClientListener listener : mApiClientListenerList)
        {
            if (listener != null)
            {
                listener.onSent(message);
            }
        }
    }

    @Override
    public void onSendFailed(String message, Exception e)
    {
        Log.e(TAG, "发送失败: " + message + "," + e.getMessage());
        for (ApiClientListener listener : mApiClientListenerList)
        {
            if (listener != null)
            {
                listener.onSendFailed(message, e);
            }
        }

        reConnect();
    }

    @Override
    public void onDataReceive(String data)
    {
        ApiProtocol apiProtocol = null;
        if(data.contains("101.37.116.128")){
           data =  data.replace("101.37.116.128","content.aiairy.com");
        }
        try
        {
            apiProtocol = mGson.fromJson(data, ApiProtocol.class);
        } catch (Exception e)
        {
            Log.e(TAG, "接收: protocol转换失败:" + data);
            return;
        }

        if (apiProtocol == null)
        {
            Log.e(TAG, "接收: protocol转换失败");
            return;
        }

        String type = apiProtocol.getType();
        if (TextUtils.isEmpty(type))
        {
            Log.e(TAG, "接收: type不可为空");
            return;
        }

        switch (type)
        {
            case ApiProtocol.TYPE_RESPONSE:
            {
                Log.e(TAG, "接收成功: " + data);
                List<OnResponseListener> listenerList = mOnResponseListenerMap.get(apiProtocol.getMethod());
                if (listenerList != null)
                {
                    if (apiProtocol.isOk())
                    {
                        for (OnResponseListener listener : listenerList)
                        {
                            if (listener != null)
                            {
                                listener.onResponse(apiProtocol);
                            }
                        }
                    } else
                    {
                        for (OnResponseListener listener : listenerList)
                        {
                            if (listener != null)
                            {
                                listener.onError(apiProtocol);
                            }
                        }
                    }
                }
            }
            break;
            case ApiProtocol.TYPE_REQUEST:
            {
                Log.e(TAG, "接收成功: " + data);

                List<OnRequestListener> listenerList = mOnRequestListenerMap.get(apiProtocol.getMethod());
                if (listenerList != null)
                {
                    for (OnRequestListener listener : listenerList)
                    {
                        if (listener != null)
                        {
                            listener.onRequest(apiProtocol);
                        }
                    }
                }
            }
            break;
            case ApiProtocol.TYPE_HEARTBEAT:
            {
                Log.e(TAG, "接收: 心跳");
            }
            break;
            default:
            {
                Log.e(TAG, "接收: type无法识别");
            }
            break;
        }
    }

    @Override
    public void onError(Exception e)
    {
        Log.e(TAG, "异常: " + e.getMessage());
        for (ApiClientListener listener : mApiClientListenerList)
        {
            if (listener != null)
            {
                listener.onError(e);
            }
        }

//        reConnect();
    }

    @Override
    public void onDisconnected()
    {
        Log.e(TAG, "连接关闭");
        for (ApiClientListener listener : mApiClientListenerList)
        {
            if (listener != null)
            {
                listener.onDisconnected();
            }
        }

        reConnect();
    }


    public interface ApiClientListener
    {
        void onInited();

        void onConnected();

        void onConnectFailed(Exception e);

        void onSent(String message);

        void onSendFailed(String message, Exception e);

        void onError(Exception e);

        void onDisconnected();
    }

    public interface OnResponseListener
    {
        void onResponse(@NonNull ApiProtocol apiProtocol);

        void onError(@NonNull ApiProtocol apiProtocol);
    }

    public interface OnRequestListener
    {
        void onRequest(@NonNull ApiProtocol apiProtocol);
    }
}
