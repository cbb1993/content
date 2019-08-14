package com.huanhong.content.model;

import com.huanhong.content.model.plan.server.ServerContent;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/12/23.
 */

public class DownLoadModel {
    private ServerContent mContent;
    private String path;
    private String name;
    private String url;
    private String md5;

    public DownLoadModel(ServerContent content)
    {
        mContent = content;
        if (mContent != null)
        {
            path = mContent.getShowPath();
            url = mContent.getUrl();
            md5 = mContent.getFileMd5();
        }
    }

    public DownLoadModel(String path, String url,String md5) {
        this.path = path;
        this.url = url;
        this.md5=md5;
    }

    public String getMd5() {
        return md5;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
