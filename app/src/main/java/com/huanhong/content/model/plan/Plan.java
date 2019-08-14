package com.huanhong.content.model.plan;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Plan implements Serializable
{
    private String planName, createTime, planType;
    private long startTime,endTime;
    private int ver = 0;
    private boolean isNeedSwitch = true;

    @SerializedName("dataMap")
    private JsonObject mPlanContent;

    public String getPlanName()
    {
        return planName;
    }

    public void setPlanName(String planName)
    {
        this.planName = planName;
    }

    public String getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }

    public String getPlanType()
    {
        return planType;
    }

    public void setPlanType(String planType)
    {
        this.planType = planType;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }

    public JsonObject getPlanContent()
    {
        return mPlanContent;
    }

    public void setPlanContent(JsonObject planContent)
    {
        this.mPlanContent = planContent;
    }

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public boolean isNeedSwitch() {
        return isNeedSwitch;
    }

    public void setNeedSwitch(boolean needSwitch) {
        isNeedSwitch = needSwitch;
    }
}
