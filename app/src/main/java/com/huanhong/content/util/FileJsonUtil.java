package com.huanhong.content.util;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanhong.content.model.DownLoadModel;
import com.huanhong.content.model.FileModel;
import com.zyn.lib.app.AppUtils;
import com.zyn.lib.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/23.
 */

public class FileJsonUtil {
    private static File ROOT=Environment.getExternalStorageDirectory();
    private static File rootFile=new File(ROOT,NormalUtil.DATABASE);
    private static FileDownLoadUtil.DownLoadCallback downLoadCallback ;
    public static void compare(final String serverJson, FileDownLoadUtil.DownLoadCallback downLoadcb){
        downLoadCallback=downLoadcb;
        new Thread(){
            @Override
            public void run() {
                super.run();
                FileUtil.mkDir(rootFile.getParentFile(),rootFile.getName(), false,null,false);
//                final String s = FileUtil.getLocalFile(rootFile);
                FileJsonUtil.JsonCompare(serverJson);
            }
        }.start();
    }

    private static Map<File,String> fileMap=new HashMap<>();
    private static Map<String,File> idMap=new HashMap<>();
        public static void JsonCompare(String serverJson){
            downLoadModelList.clear();
           /* try {
                JSONObject clientObject=new JSONObject(clientJson);
                JSONObject serverObject=new JSONObject(serverJson);
                 clientModle =new Gson().fromJson(clientObject.getString("data"),FileModel.class);
                 serverModle =new Gson().fromJson(serverObject.getString("data"),FileModel.class);
                next(file,clientModle,serverModle);

            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            try {
                JSONObject serverObject=new JSONObject(serverJson);
//                 serverModle=new Gson().fromJson(serverObject.getString("data"),FileModel.class);
                Type listType = new TypeToken<ArrayList<FileModel>>() {
                }.getType();
                List<FileModel>  serverList= new Gson().fromJson(serverObject.getString("list"),listType);
                for(FileModel s:serverList){
                    compareId(s,serverObject.getString("directive"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String str= new Gson().toJson(fileMap);
            String str2= new Gson().toJson(idMap);
            SharedPreferencesUtils.addData("fileMap",str);
            SharedPreferencesUtils.addData("idMap",str2);

//              --比较完成  开始增加下载队列 开始下载--
            if(downLoadModelList==null||downLoadModelList.size()==0){
                handler.sendEmptyMessage(2);
            }else {
                List<String> urlList=new ArrayList<>();
                List<String> pathList=new ArrayList<>();
                for(DownLoadModel model:downLoadModelList){
                    if(!pathList.contains(model.getPath())){
                        urlList.add(model.getUrl());
                        pathList.add(model.getPath());
                    }
                }
                Log.e("=======",urlList+"");
                Log.e("=======",pathList+"");
                FileDownLoadUtil.downLoad(urlList, pathList,downLoadCallback ,0,urlList.size());
            }

        }
    private static void compareId(FileModel serverModle,String type){
        File f=new File(rootFile,serverModle.getShowPath());
        if(serverModle.type.equals(NormalUtil.DIR)){
            if(!f.exists()){
                Log.e("=======", "正在创建文件夹" + f.getName()+"==路径=="+f.getPath());
                f.mkdirs();
            }
        }
          if(!type.equals("1")){
              Log.e("=======", "正在删除" + f.getName());
                  FileUtil.deleteFile(f);
          }else {
              String s = FileUtil.getLocalFile(f);
              try {
                  JSONObject clientObject=new JSONObject(s);
                  FileModel  cm =new Gson().fromJson(clientObject.getString("data"),FileModel.class);
                  next(f,cm,serverModle);
              } catch (JSONException e) {
                  e.printStackTrace();
              }
            /*  File file1=new File(Environment.getExternalStorageDirectory(),serverModle.path);
              if(serverModle.type.equals(NormalUtil.FILE)){
                  if(file1.exists()){
                      FileUtil.deleteFile(file1);
                  }
                  fileMap.put(file1,serverModle.id);
                  idMap.put(serverModle.id,file1);
                  downLoadModelList.add(new DownLoadModel(file1.getPath(),null,serverModle.url));
              }else {
                  if(file1.exists()){
                      if(clientModle.id.equals(serverModle.id)){
                          next(file,clientModle,serverModle);
                      }else {
                          if(clientModle.files!=null){
                              for(FileModel cm:clientModle.files){
                                  compareId(cf,serverModle,cm,type);
                              }
                          }
                      }
                  }else {
                        next(file,null,serverModle);
                  }
              }*/
          }
    }

   public static List<DownLoadModel> downLoadModelList=new ArrayList<>();
    private static void next(File file, FileModel clientModel, FileModel serverModel){
        //判断服务器文件夹中是否有文件
        if(serverModel!=null&&serverModel.fileList!=null) {
            //判断客户端有的文件 服务端是否有
            if (clientModel != null && clientModel.fileList != null && clientModel.fileList.length > 0) {
            for (FileModel clientFile : clientModel.fileList) {
                boolean isHas = false;//是否有
                for (FileModel serverFile : serverModel.fileList) {
                    if (serverFile.id.equals(clientFile.id)) {
                        isHas = true;
                    }
                }
                if (!isHas) {
                    Log.e("=======", "正在删除" + clientFile.fileName);
                    File f = new File(file, clientFile.fileName);
                    if (f.isFile()) {
                        f.delete();
                    } else {
                        List<File> list = new ArrayList<>();
                        list.add(f);
                        if (list.size() > 0) {
                            FileUtil.delete(list);
                        }
                    }
                }
            }
        }
            //服务器文件夹有文件 客户端是否有
            if(!serverModel.type.equals(NormalUtil.DIR)){
                dosome(file, serverModel,clientModel);
            }else {
            for(FileModel serverFile: serverModel.fileList){
                dosome(file,serverFile,clientModel);
            }
            }
        }else {
            //服务器这个文件夹中没有文件  判断客户端这个文件夹中有没有文件 如果有就删掉
            if(clientModel!=null&&clientModel.fileList!=null&& clientModel.fileList.length>0){
                List<File> list=new ArrayList<>();
                for(File f:file.listFiles()){
                    list.add(f);
                }
                if(list.size()>0){
                    FileUtil.delete(list);
                }
            }
        }
    }
private static void dosome(File file,FileModel serverFile,FileModel clientModel ){
    boolean isHas=false; //服务器文件夹中的某个文件 在客户端中是否有
    boolean isNameEques=false;//在有的情况下 名字是否相同
    FileModel clientF=null; //保存客户端当前文件
    if(clientModel !=null&& clientModel.fileList!=null){
        for(FileModel clientFile: clientModel.fileList){
            if(serverFile.id.equals(clientFile.id)){
                clientF=clientFile;
                isHas=true;
                if(serverFile.fileName.equals(clientFile.fileName)){
                    isNameEques=true;
                }
                break;
            }
        }
    }
    File f=new File(rootFile,serverFile.getShowPath());
    fileMap.put(f,serverFile.id);
    idMap.put(serverFile.id,f);
    if(isHas){//服务器文件夹中的某个文件 在客户端中有
        if(!isNameEques){
            //名字不同
            File cf=new File(file,clientF.fileName);
            cf.renameTo(f);
        }
        if(serverFile.type.equals(NormalUtil.DIR)){
            next(f,clientF,serverFile);
        }
    }else {//服务器文件夹中的某个文件 在客户端中没有n
        if(!serverFile.type.equals(NormalUtil.DIR)){
            // 添加到下载列表中
            Log.e("=======","加入下载队列"+f.getName());
//            downLoadModelList.add(new DownLoadModel(f.getParentFile().getPath(),serverFile.fileName,serverFile.filePath));
            downLoadModelList.add(new DownLoadModel(f.getPath(),serverFile.filePath,null));
            /*
            * 第三个参数是md5值  目前同步时没有做判断
            * */
        }else {
            // 新建文件夹
                Log.e("=======","正在创建文件夹="+f.getName()+"===路径=="+f.getPath());
                f.mkdirs();
            next(f,null,serverFile);
        }
    }
}

    static Handler handler=new Handler(AppUtils.getBaseApplication().getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                downLoadCallback.synchronizeComplete();
            }
        }
    };

    public static void DownLoadFiles(final List<DownLoadModel> infoList, final FileDownLoadUtil.DownLoadCallback callback){
        downLoadCallback = callback;
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<String> urlList=new ArrayList<>();
                List<String> pathList=new ArrayList<>();
                for(DownLoadModel model:infoList){
                    File file=new File(rootFile,"/"+model.getPath());
                    if(!TextUtils.isEmpty(model.getPath())&&!TextUtils.isEmpty(model.getUrl())&&!TextUtils.isEmpty(model.getMd5())){
                        if(!pathList.contains(file.getPath())){
                            if(!file.exists()){
                                urlList.add(model.getUrl());
                                pathList.add(file.getPath());
                            }else {
                                Log.e("========clientMd5","=="+FileMd5Util.getFileMD5(file.getPath()));
                                Log.e("========severMd5","=="+model.getMd5());
                                if(!(FileMd5Util.getFileMD5(file.getPath())+"").equals(model.getMd5())){
                                    urlList.add(model.getUrl());
                                    pathList.add(file.getPath());
                                }
                            }
                        }
                    }
                }
                Log.e("=======",urlList+"");
                Log.e("=======",pathList+"");
                if(urlList.size()==0){
                    handler.sendEmptyMessage(2);
                }else
                    FileDownLoadUtil.downLoad(urlList, pathList,callback ,0,urlList.size());
            }
        }.start();
    }
}
