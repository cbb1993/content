package com.huanhong.content.model;

import android.support.annotation.DimenRes;

import java.io.File;
import java.net.URI;
import java.util.Arrays;

/**
 * Created by Administrator on 2016/12/23.
 */
public class FileModel {
    public FileModel(){}
    public FileModel(String id, String url, String name, String type, FileModel[] files,String path) {
        this.id = id;
        this.filePath = url;
        this.fileName = name;
        this.type = type;
        this.fileList = files;
        this.showPath=path;
    }

    public String getShowPath() {
        return "/"+showPath;
    }

    public String id;
    public String filePath;
    public String fileName ;
    public String type; // 1 文件夹  2 文件
    public FileModel[] fileList;
    public String showPath;


//    @Override
//    public String toString() {
//        if(files==null||files.length==0){
//            return "{\"name\":\""+name+"\","+"\"type\":\""+type+"\",\"files\":null}";
//        }else
//        {
//            String modelStr="";
//            for(int i=0;i<fileModleList.length;i++){
//                if(i==0){
//                    modelStr=fileModleList[0].toString();
//                }else
//                    modelStr=modelStr+","+fileModleList[i].toString();
//            }
//            return "{\"name\":\""+name+"\","+"\"isFile\":"+isFile+",\"fileModleList\":["+modelStr+"]}";
//        }
//    }


    @Override
    public String toString() {
        return "FileModel{" +
                "id='" + id + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", type='" + type + '\'' +
                ", fileList=" + Arrays.toString(fileList) +
                ", showPath='" + showPath + '\'' +
                '}';
    }
}

