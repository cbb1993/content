package com.huanhong.content.model.plan.server;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 每个分屏对应一个total schedule
 */
public class TaskModel implements Serializable {
    @SerializedName("windowingId")
    private String id;

//    @SerializedName("schedule")
    private List<ServerSplitSchedule> tasks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ServerSplitSchedule> getTasks() {
        return tasks;
    }
}
