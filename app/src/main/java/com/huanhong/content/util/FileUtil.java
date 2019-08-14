package com.huanhong.content.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.huanhong.content.model.FileModel;
import com.zyn.lib.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件操作工具类
 */
public class FileUtil {
    final static String FILE_NAME_RESERVED = "|\\?*<\":>+[]/'";
    private static boolean isValidFileName(String name) {

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (-1 != FILE_NAME_RESERVED.indexOf(c)) {
                return false;
            }
        }
        return true;
    }
/*
* 创建文件（夹）
* fatherFile 当前父文件
* name 文件（夹）名称
* isFile true文件 false 文件夹
* */
public interface CreatStatusCallback {
    void success();
}
    public static File mkDir(File fatherFile, String name, boolean isFile, CreatStatusCallback creatStatusCallback,boolean isToast) {
        if (isValidFileName(name)) {
            File file = new File(fatherFile, name);
            if (file.exists()) {
                if(isToast){
                    NormalUtil.showToast("文件（夹）已存在");
                }
            } else {
                if (isFile) {
                    try {
                        file.createNewFile();
                        if(creatStatusCallback!=null){
                            creatStatusCallback.success();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    file.mkdir();
                    if(creatStatusCallback!=null){
                        creatStatusCallback.success();
                    }
                }
            }
            return file;
        } else {
            if(isToast){
                NormalUtil.showToast("不能包含如下字符" + FILE_NAME_RESERVED);
            }
        }
        return null;
    }
    /*
* 文件（夹）重命名
* fatherFile  当前父文件
* currentFile 当前需修改的文件（夹）
* name 修改后的名称
* */
    public interface EditStatusCallback {
        void success();
    }
    public static void editFileName(File fatherFile, File currentFile, String name, EditStatusCallback callback) {
        if (isValidFileName(name)) {
            File file = new File(fatherFile, name);
            if (file.exists()) {
                NormalUtil.showToast("文件（夹）已存在");
            } else {
                currentFile.renameTo(file);
                callback.success();
            }
        } else {
            NormalUtil.showToast("不能包含如下字符" + FILE_NAME_RESERVED);
        }
    }
/*
* 删除文件（夹）
* */
    public static void delete(List<File> files) {
        for (File f : files) {
            deleteFile(f);
        }
    }
/*
* 递归删除
* */
public static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            if(files!=null&&files.length>0){
                for (File f : files) {
                    if(f!=null){
                        deleteFile(f);
                    }
                }
            }
            file.delete();
        }
    }

    public static void openFile(Activity activity , File file){
        Intent intent = IntentUtils.getInstance(activity).openFile(file);
        if (null != intent) {
            activity.startActivity(intent);
        } else {
            NormalUtil.showToast("无法识别的文件");
        }
    }


private static Map<File,String> map;
    public static String getLocalFile(File file){
        String mapstr=SharedPreferencesUtils.readData("fileMap");
        map=new Gson().fromJson(mapstr, HashMap.class);
        getFile(file);
        JSONObject o1=new JSONObject();
        try {
            o1.put("data",modle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String str= new Gson().toJson(o1);

        try {
            JSONObject jsonObject= new JSONObject(str);
            if(str!=null&&str.contains(NormalUtil.DATABASE)){
                str=jsonObject.getString("nameValuePairs");
                return str;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "error";
    };
    private static String url="http://120.26.103.175/image/upload/20170101/201701011943206115.jpg";
    private static FileModel modle;
    private static void getFile(File file) {
        JSONObject jsonObject=new JSONObject();
        FileModel info=new FileModel();
        info.fileName=file.getName();
        String id="-1";
        if(map!=null&&map.get(file.getPath())!=null){
            id=map.get(file.getPath());
        }
        info.id=id;
        info.showPath=file.getPath();
        info.filePath=url;
        if (file.isFile()) {
            info.type=NormalUtil.FILE;
            info.fileList=new FileModel[0];
            try {
                jsonObject.put(info.fileName,info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            info.type=NormalUtil.DIR;
            info.fileList=getArray( file.listFiles());
            try {
                jsonObject.put(info.fileName,info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            File[] files = file.listFiles();
            if(files!=null){
                for (File f : files) {
                    getFile(f);
                }
            }
        }
        modle=info;
    }

    private static FileModel[] getArray(File[] files ){
        int length=0;
        if(files!=null){
            length=files.length;
        }
        FileModel[] fileModles=new FileModel[length];
        for (int i=0;i<length;i++) {
            String id="-1";
            if(map!=null&&map.get(files[i].getPath())!=null){
                id=map.get(files[i].getPath());
            }
            if(files[i].isFile()){
                fileModles[i]=new FileModel(id,url,files[i].getName(),NormalUtil.FILE,null,files[i].getPath());
            }else
            fileModles[i]=new FileModel(id,"",files[i].getName(),NormalUtil.DIR,getArray( files[i].listFiles()),files[i].getPath());
        }
        return fileModles;
    }


    public static String getPathByShowPath(String path){
        File rootFile=new File(Environment.getExternalStorageDirectory(),NormalUtil.DATABASE);
        if(!path.startsWith(rootFile.getPath()))
        {
            File file=new File(rootFile,"/"+path);
            path = file.getPath();
        }
        return path;
    }

}