package com.huanhong.content.util.bluetooth;

import android.content.Context;

import com.zyn.lib.util.Utils;

/**
 * Created by 坎坎 on 2017/11/21.
 */

public class MyBlueToothInitUtil {
    public static void init(Context context){
        if(BluetoothManager.getInstance()==null){
            Utils.toastShort("该设备不支持蓝牙");
            return;
        }
        //注册
        BluetoothManager.getInstance().registerReceiver(context);
        BluetoothManager.getInstance().startDiscovery();
    }

}
