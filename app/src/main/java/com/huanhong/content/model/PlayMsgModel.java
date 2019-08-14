package com.huanhong.content.model;

import com.google.gson.Gson;
import com.huanhong.content.util.NormalUtil;
import com.zyn.lib.util.GsonUtils;
import com.zyn.lib.util.SharedPreferencesUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/1/6.
 */

public class PlayMsgModel {
    public static final String TYPE_PLAN = "0";
    public static final String TYPE_LOCAL_WEB = "1";
    public static final String TYPE_LOCAL_VIDEO = "2";

    private List<String> playList;
    private String type;// 0 按计划 1 web 2 video
    private String webUrl;//
    private String webKeepTime; //网页静默时间
    private String filePath;

    public static PlayMsgModel getPlayMsgInfo() {
        String s = SharedPreferencesUtils.readData(NormalUtil.PLAYMSG);
        if (s != null) {
            return new Gson().fromJson(s, PlayMsgModel.class);
        }
        return null;
    }

    public static void save(PlayMsgModel playMsgModel) {
        SharedPreferencesUtils.addData(NormalUtil.PLAYMSG, GsonUtils
                .getGson()
                .toJson(playMsgModel));
    }

    public List<String> getPlayList() {
        return playList;
    }

    public void setPlayList(List<String> playList) {
        this.playList = playList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getWebKeepTime() {
        return webKeepTime;
    }

    public void setWebKeepTime(String webKeepTime) {
        this.webKeepTime = webKeepTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
