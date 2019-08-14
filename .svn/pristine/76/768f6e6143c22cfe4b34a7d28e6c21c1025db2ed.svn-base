package com.huanhong.content.model.sync;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.huanhong.content.model.api.ApiClient;
import com.huanhong.content.model.api.ApiMethod;
import com.huanhong.content.model.api.ApiProtocol;
import com.huanhong.content.model.info.ClientInfo;
import com.zyn.lib.util.GsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class SyncHandler implements ApiClient.OnRequestListener
{
    private static SyncHandler mInstance;
    private SyncHandlerListener mSyncHandlerListener;
    private ApiProtocol mApiProtocol;

    private SyncHandler()
    {
        ApiClient
                .getInstance()
                .addOnRequestListener(ApiMethod.METHOD_SYNC_CONTENT_TREE, this);
    }

    public static synchronized SyncHandler getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new SyncHandler();
        }
        return mInstance;
    }

    public SyncHandlerListener getSyncHandlerListener()
    {
        return mSyncHandlerListener;
    }

    public void setSyncHandlerListener(SyncHandlerListener syncHandlerListener)
    {
        mSyncHandlerListener = syncHandlerListener;
    }

    @Override
    public void onRequest(@NonNull ApiProtocol apiProtocol)
    {
        //如果正在同步中，拒绝这次同步请求
        if(ClientInfo.getInstance().isSyncing())
        {
            response(false,false,"正在同步中，不应该再次同步");
            return;
        }
        else
        {
            ClientInfo.getInstance().setSyncing(true);
        }

        //记录protocol
        mApiProtocol = apiProtocol;

        if(apiProtocol.getData()!=null)
        {
            String json = (apiProtocol.getData()).toString();

            if (mSyncHandlerListener != null)
            {
                mSyncHandlerListener.onSync(json);
                response(true,false,"开始同步文件");
            }
            else
            {
                response(false,false,"客户端异常");
                ClientInfo.getInstance().setSyncing(false);
            }
        }
        else
        {
            response(false,false,"data数据异常");
            ClientInfo.getInstance().setSyncing(false);
        }
    }

    public void response(boolean ok,boolean isComplete,String msg)
    {
        if(mApiProtocol==null)
        {
            return;
        }

        ApiProtocol response = mApiProtocol.getResponse(msg);
        response.setData(mApiProtocol.getData());
        response.setOk(ok);

        if(ok)
        {
            JsonObject data = (JsonObject) mApiProtocol.getData();
            if(data!=null)
            {
                String status = "";
                if(isComplete)
                {
                    status = "enddownload";
                }
                else
                {
                    status = "begindownload";
                }
                data.addProperty("schedule",status);
                response.setData(data);
            }
        }

        ApiClient
                .getInstance().send(response);
    }

    public interface SyncHandlerListener
    {
        void onSync(String json);
    }
}
