package com.huanhong.content.model.monitor;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.huanhong.content.model.api.ApiClient;
import com.huanhong.content.model.api.ApiMethod;
import com.huanhong.content.model.api.ApiProtocol;
import com.huanhong.content.model.info.ClientInfo;
import com.huanhong.content.model.plan.server.ServerAction;
import com.huanhong.content.model.plan.server.ServerContent;
import com.huanhong.content.model.uploadfile.UpLoadListener;
import com.huanhong.content.model.uploadfile.UpLoadUtil;
import com.huanhong.content.presenter.MainPresenter;
import com.huanhong.content.view.activity.MainActivity;
import com.zyn.lib.app.AppUtils;
import com.zyn.lib.presenter.BasePresenter;
import com.zyn.lib.util.GsonUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by tuka2401 on 2017/1/17.
 */
public class MonitorHandler implements ApiClient.OnRequestListener
{
    private static MonitorHandler mInstance;

    private MonitorHandler()
    {
        ApiClient
                .getInstance()
                .addOnRequestListener(ApiMethod.METHOD_MONITOR, this);
    }

    public static synchronized MonitorHandler getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new MonitorHandler();
        }
        return mInstance;
    }

    @Override
    public void onRequest(@NonNull final ApiProtocol apiProtocol)
    {
        Activity topActivity = AppUtils
                .getBaseApplication()
                .getTopActivity();
        if (topActivity.getClass() == MainActivity.class)
        {
            BasePresenter mainPresenter = ClientInfo
                    .getInstance()
                    .getMainPresenter();
            if (mainPresenter == null)
            {
                error(apiProtocol);
            }

            try
            {
                JsonObject data = (JsonObject) apiProtocol.getData();
                String type = data
                        .getAsJsonPrimitive("type")
                        .getAsString();

                //0截全屏 分屏id 截分屏
                String windowingid = data
                        .getAsJsonPrimitive("windowingid")
                        .getAsString();

                // 1截屏2播放详情
                switch (type)
                {
                    case "1":
                    {
                        mainPresenter.getShot(windowingid, new MainPresenter.ShotListener()
                        {
                            @Override
                            public void onFinish(String path)
                            {
                                if (TextUtils.isEmpty(path))
                                {
                                    ApiClient
                                            .getInstance()
                                            .send(apiProtocol.getErrorResponse("空闲中"));
                                    return;
                                }

                                File file = new File(path);
                                if (file.exists())
                                {
                                    UpLoadUtil.upLoad(file, new UpLoadListener()
                                    {
                                        @Override
                                        public void success(String path)
                                        {
                                            ApiProtocol response = apiProtocol.getResponse("成功");
                                            response.setData(path);
                                            ApiClient
                                                    .getInstance()
                                                    .send(response);
                                        }

                                        @Override
                                        public void fail(String msg)
                                        {
                                            error(apiProtocol);
                                        }
                                    });
                                }
                            }
                        });
                    }
                    break;
                    case "2":
                    {
                        Map<String, Object> map = mainPresenter.getCurrContent();
                        if (map == null || map.isEmpty())
                        {
                            ApiClient
                                    .getInstance()
                                    .send(apiProtocol.getErrorResponse("空闲中"));
                        } else
                        {
                            JsonArray jsonArray = new JsonArray();
                            for (Map.Entry<String, Object> entry : map.entrySet())
                            {
                                Object obj = entry.getValue();
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("windowingid", entry.getKey());
                                JsonObject content = new JsonObject();
                                if (obj == null)
                                {
                                    content.addProperty("type", "3");
                                } else if (obj.getClass() == ServerAction.class)
                                {
                                    content.addProperty("type", "2");
                                    content.add("obj", GsonUtils
                                            .getGson()
                                            .toJsonTree(obj));
                                } else if (obj.getClass() == ServerContent.class)
                                {
                                    ServerContent c = (ServerContent) obj;
                                    if (c.isLocal())
                                    {
                                        content.addProperty("type", "4");
                                    } else
                                    {
                                        content.addProperty("type", "1");
                                        content.add("obj", GsonUtils
                                                .getGson()
                                                .toJsonTree(obj));
                                    }
                                }
                                jsonObject.add("content", content);
                                jsonArray.add(jsonObject);
                            }
                            ApiProtocol response = apiProtocol.getResponse("成功");
                            response.setData(jsonArray);
                            ApiClient
                                    .getInstance()
                                    .send(response);
                        }
                    }
                    break;
                    default:
                    {
                        ApiClient
                                .getInstance()
                                .send(apiProtocol.getErrorResponse("type类型错误"));
                    }
                    break;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                error(apiProtocol);
            }
        } else
        {
            ApiClient
                    .getInstance()
                    .send(apiProtocol.getErrorResponse("空闲中"));
        }
    }

    private void error(@NonNull ApiProtocol apiProtocol)
    {
        ApiClient
                .getInstance()
                .send(apiProtocol.getErrorResponse("系统异常"));
    }
}
