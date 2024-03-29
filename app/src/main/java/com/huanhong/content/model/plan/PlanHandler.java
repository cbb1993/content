package com.huanhong.content.model.plan;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.huanhong.content.model.api.ApiClient;
import com.huanhong.content.model.api.ApiMethod;
import com.huanhong.content.model.api.ApiProtocol;
import com.huanhong.content.model.info.ClientInfo;
import com.zyn.lib.constant.CommonConstants;
import com.zyn.lib.util.GsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class PlanHandler implements ApiClient.OnRequestListener {
    private static PlanHandler mInstance;
    private PlanHandlerListener mPlanHandlerListener;
    private Gson mGson = new GsonBuilder()
            .registerTypeAdapter(Plan.class, new JsonDeserializer<Plan>() {
                @Override
                public Plan deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    if (json == null || json.isJsonNull() || !json.isJsonObject()) {
                        return null;
                    }

                    Plan plan = new Gson().fromJson(json, Plan.class);
                    JsonObject data = null;
                    try {
                        data = json
                                .getAsJsonObject()
                                .getAsJsonObject(CommonConstants.DATAMAP);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    plan.setPlanContent(data);
                    return plan;
                }
            })
            .create();
    ;

    private PlanHandler() {
        ApiClient
                .getInstance()
                .addOnRequestListener(ApiMethod.METHOD_PLAN, this);
    }

    public static synchronized PlanHandler getInstance() {
        if (mInstance == null) {
            mInstance = new PlanHandler();
        }

        return mInstance;
    }

    @Override
    public void onRequest(@NonNull ApiProtocol apiProtocol) {
        //如果正在同步中，拒绝这次同步请求
        if (ClientInfo
                .getInstance()
                .isSyncing()) {
            ApiClient
                    .getInstance()
                    .send(apiProtocol.getErrorResponse("正在同步中，不应该再次同步"));
            return;
        } else {
            ClientInfo
                    .getInstance()
                    .setSyncing(true);
        }
        String json = apiProtocol.getData().toString();

        try {
            Type type = new TypeToken<ArrayList<Plan>>() {
            }.getType();
            List<Plan> planList = GsonUtils
                    .getGson()
                    .fromJson((apiProtocol.getData())
                            .toString(), type);

            //反馈
            ApiClient
                    .getInstance()
                    .send(apiProtocol.getResponse(null));

            if (mPlanHandlerListener != null) {
                mPlanHandlerListener.onPlan(planList,json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiClient
                    .getInstance()
                    .send(apiProtocol.getErrorResponse("数据解析异常"));
            ClientInfo
                    .getInstance()
                    .setSyncing(false);
        }
    }

    public PlanHandlerListener getPlanHandlerListener() {
        return mPlanHandlerListener;
    }

    public void setPlanHandlerListener(PlanHandlerListener planHandlerListener) {
        mPlanHandlerListener = planHandlerListener;
    }

    public interface PlanHandlerListener {
        void onPlan(List<Plan> list,String data);
    }
}
