package com.huanhong.content.model.plan.server;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tuka2401 on 2017/3/21.
 */

public class ServerTask {


    public static final String TASK_TYPE_DAILY = "1";
    public static final String TASK_TYPE_WEEKLY = "3";
    public static final String TASK_TYPE_DATE = "2";

    @SerializedName("taskCycle")
    private String type;

    @SerializedName("weekDay")
    private String typeRelated;

    @SerializedName("taskName")
    private String name;

    @SerializedName("taskDate")
    private long startTime;

    @SerializedName("list")
    private List<ServerSchedule> schedule;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeRelated() {
        return typeRelated;
    }

    public void setTypeRelated(String typeRelated) {
        this.typeRelated = typeRelated;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public List<ServerSchedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<ServerSchedule> schedule) {
        this.schedule = schedule;
    }
}
