package com.huanhong.content.util.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zyn.lib.util.SharedPreferencesUtils;
import com.zyn.lib.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tuka2401 on 2017/2/21.
 */
public class BluetoothManager {
    public static final String TAG_PRINT = "tag_print";
    private static BluetoothManager mInstance;
    private final int REQUEST_CODE_ENABLE = 1151;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothManager() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    /**
     * 当设备不支持蓝牙时返回null
     *
     * @return
     */
    public static synchronized
    @Nullable
    BluetoothManager getInstance() {
        if (mInstance == null) {
            mInstance = new BluetoothManager();
        }

        //若bluetooth adapter == null 说明设备不支持蓝牙
        if (mInstance.getBluetoothAdapter() == null) {
            return null;
        }

        return mInstance;
    }

    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 若蓝牙未开启，请求开启蓝牙，与onActivityResult配合使用
     *
     * @param activity
     * @param listener
     */
    public void enable(Activity activity, EnabledListener listener) {
        if (!isEnabled()) {
            //弹出对话框提示用户是后打开
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enabler, REQUEST_CODE_ENABLE);
            //不做提示，强行打开
//            mBluetoothAdapter.enable();
        } else {
            if (listener != null) {
                listener.onEnabled();
            }
        }
    }

    public void disable() {
        mBluetoothAdapter.disable();
    }

    public void onActivityResult(int requestCode, EnabledListener listener) {
        if (listener == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_ENABLE) {
            if (isEnabled()) {
                listener.onEnabled();
            } else {
                listener.onDisabled();
            }
        }
    }

    public void startDiscovery(Context context, final DiscoveryListener discoveryListener) {

        //接收器
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //找到设备
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        if (discoveryListener != null) {
                            discoveryListener.onFind(device);
                        }
                    }
                }
                //搜索完成
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {
                    context.unregisterReceiver(this);
                    if (discoveryListener != null) {
                        discoveryListener.onFinish();
                    }
                }
            }
        };

        //设置intent过滤
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, filter);

        mBluetoothAdapter.startDiscovery();
    }

    private List<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    //已经配对的
                    boolean isExist = false;
                    for (BluetoothDevice bluetoothDevice : list) {
                        if (bluetoothDevice.getAddress() == device.getAddress()) {
                            isExist = true;
                        }
                    }
                    if (!isExist) {
                        Log.e("bluetooch_已匹配设备", device.getName() + ":" + device.getAddress() + "\n");
                        list.add(device);
                        // 保存设备的信息
                        if (device.getName().contains("rint")) {
                            connect(device);
                            SharedPreferencesUtils.addData(TAG_PRINT, device.getAddress());
                        }
                    }
                }
            } else if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                Utils.toastShort("搜索完成");
                Log.e("bluetooch", "搜索完成");
                context.unregisterReceiver(this);
            } else if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                Log.e("bluetooch", "状态改变");
//                mBluetoothAdapter.startDiscovery();
            }

        }
    };
    //连接
    private void connect(BluetoothDevice device) {
        try {
            // 固定的UUID
            String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
            UUID uuid = UUID.fromString(SPP_UUID);
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
        }catch (Exception e){
        }
    }

    //注册
    public  void startDiscovery() {
        list.clear();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }


    public void registerReceiver(Context context){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, filter);
    }

    public List<BluetoothDevice> getBondedDevices()
    {
        List<BluetoothDevice> list = new ArrayList<>();
        list.addAll(mBluetoothAdapter.getBondedDevices());
        return list;
    }

    public BluetoothAdapter getBluetoothAdapter()
    {
        return mBluetoothAdapter;
    }

    public interface EnabledListener
    {
        void onEnabled();

        void onDisabled();
    }

    public interface DiscoveryListener
    {
        void onFind(BluetoothDevice device);

        void onFinish();
    }
}
