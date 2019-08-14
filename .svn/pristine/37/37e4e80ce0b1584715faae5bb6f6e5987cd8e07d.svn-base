package com.huanhong.content.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 坎坎 on 2017/3/2.
 */

public class PermissionUtil {
    //permission  例子  Manifest.permission.CALL_PHONE

    /*
    * 检查是否有权限
    * */
    public static boolean checkPermision(Activity activity, @NonNull String permission){
        if(ContextCompat.checkSelfPermission(activity,permission)!= PackageManager.PERMISSION_GRANTED){
            return false;
        }else return true;
    }

    /*
      * 没有权限的情况下去请求权限
      * */
    public static boolean requestPermisionIfNot(Activity activity , @NonNull String permission, int requestCode){
        if(!checkPermision(activity,permission)){
            ActivityCompat.requestPermissions(activity,new String[]{permission},requestCode);
            return false;
        }else return true;
    }

    /*
          * 没有权限的情况下去请求多个权限
          * */
    public static void requestPermisionListIfNot(Activity activity , @NonNull List<String> permissionList, int requestCode){
        List needRequestList=new ArrayList();
        for(String s:permissionList){
            if(!checkPermision(activity,s)){
                needRequestList.add(s);
            }
        }
        if(needRequestList.size()>0){
            ActivityCompat.requestPermissions(activity, (String[]) needRequestList.toArray(new String[needRequestList.size()]),requestCode);
        }

    }




    /**
     * 判断单个权限有没有请求成功  ----  用户同意或者拒绝了
     * @param grantResults
     */
    public static boolean onRequestPermissionsResult(@NonNull int[] grantResults) {
        if(grantResults.length>0){
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    return true;
                }
        }
        return false;
    }
}
