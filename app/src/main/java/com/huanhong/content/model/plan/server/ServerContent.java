package com.huanhong.content.model.plan.server;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServerContent {
    private String fileName, rank, fileFormat, fileMemory, url, showPath, fileMd5;
    @SerializedName("duration")
    private long playTime;

    @SerializedName("frequency")
    private int count;

    //1.文件夹2.视频3.音频4.图片5.PPT6.文件7.链接8.h5
    private String type;
    private List<ServerAction> action;

    //是否是本地资源
    private Boolean isLocal = false;

    public Boolean isLocal() {
        return isLocal;
    }

    public void setLocal(Boolean local) {
        isLocal = local;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getFileMemory() {
        return fileMemory;
    }

    public void setFileMemory(String fileMemory) {
        this.fileMemory = fileMemory;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShowPath() {
        return showPath;
    }

    public void setShowPath(String showPath) {
        this.showPath = showPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ServerAction> getActionList() {
        return action;
    }

    @Override
    public String toString() {
        return url + ":" + showPath;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public List<ServerAction> getAction() {
        return action;
    }

    public void setAction(List<ServerAction> action) {
        this.action = action;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
