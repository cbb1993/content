package com.huanhong.content.model.info;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanhong.content.model.api.ApiConstans;
import com.huanhong.content.model.plan.Plan;
import com.zyn.lib.util.GsonUtils;
import com.zyn.lib.util.SharedPreferencesUtils;
import com.zyn.lib.util.Utils;

import java.lang.reflect.Type;
import java.util.List;

public class HistoryInfo
{
    public static void savePlanList(List<Plan> plan)
    {
        try
        {
            SharedPreferencesUtils.addData(ApiConstans.PLAN_HISTORY, GsonUtils.getGson().toJson(plan));
        }catch (Exception ignore)
        {

        }
    }

    public static List<Plan> getPlanList()
    {
        try
        {
            Type type = new TypeToken<List<Plan>>(){}.getType();
            return  GsonUtils.getGson().fromJson(SharedPreferencesUtils.readData(ApiConstans.PLAN_HISTORY),type);
        }catch (Exception ignore)
        {
           return null;
        }
    }

    public static boolean hasPlan()
    {
        return !Utils.isEmptyList(getPlanList());
    }
}
