package com.huanhong.content.model.plan.server;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 每个分屏对应一个total schedule
 */
public class ServerSplitSchedule implements Serializable {
    @SerializedName("windowingId")
    private String id;

    @SerializedName("schedule")
    private List<ServerTask> tasks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ServerTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<ServerTask> tasks) {
        this.tasks = tasks;
    }
}
