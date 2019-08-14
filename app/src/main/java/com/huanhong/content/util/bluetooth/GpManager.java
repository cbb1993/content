package com.huanhong.content.util.bluetooth;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.TscCommand;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;

import java.util.Vector;

import static com.gprinter.command.GpCom.ACTION_CONNECT_STATUS;

/*
 * 佳博打印机管理者
 * 使用流程: start -> openPort -> do something -> stop
 * Created by tuka2401 on 2017/2/23.
 */
public class GpManager
{
    private boolean isCallBack=false; // 服务连接回调
    Handler handler=new Handler();
    private final String TAG = getClass().getSimpleName();

    private final int PRINTER_ID = 1;

    //设备端口是否开启
    private boolean isPortConnected = false;
    //服务是否连接
    private boolean isServiceConnected = false;

    @NonNull
    private Context mContext;
    @NonNull
    private String mPrinterAddr;
    private GpService mGpService;
    private GpManagerListener mGpManagerListener;
    private ServiceConnection mConn = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            Log.e(TAG, "服务已连接");
            isServiceConnected = true;
            isCallBack=true;
            mGpService = GpService.Stub.asInterface(iBinder);
            openPort();

            if (mGpManagerListener != null)
            {
                mGpManagerListener.onServiceConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            Log.e(TAG, "服务已断开");
            isServiceConnected = false;
            mGpService = null;

            if (mGpManagerListener != null)
            {
                mGpManagerListener.onServiceDisconnected();
            }
        }
    };

    private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (mGpManagerListener == null)
            {
                return;
            }

            if (ACTION_CONNECT_STATUS.equals(intent.getAction()))
            {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
//                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);
                if (type == GpDevice.STATE_CONNECTED)
                {
                    //设备端口刚连接时还未准备好
                    Log.e(TAG, "设备状态：端口已连接");
                    isPortConnected = true;
                    mGpManagerListener.onPortConnected();
                } else if (type == GpDevice.STATE_NONE)
                {
                    Log.e(TAG, "设备状态：端口已断开");
                    isPortConnected = false;
                    mGpManagerListener.onPortDisconnected();
                } else if (type == GpDevice.STATE_VALID_PRINTER)
                {
                    Log.e(TAG, "设备状态：已就绪");
                    mGpManagerListener.onReady();
                } else if (type == GpDevice.STATE_INVALID_PRINTER)
                {
                    Log.e(TAG, "设备状态：未就绪");
                    mGpManagerListener.onReadyFailed();
                }
            }
        }
    };

    public GpManager(@NonNull Context context, @NonNull String printerAddr, GpManagerListener gpManagerListener)
    {
        mContext = context;
        mPrinterAddr = printerAddr;
        mGpManagerListener = gpManagerListener;
    }

    /**
     * 开启gp管理
     */
    public void start()
    {
        isCallBack=false;
        Log.e(TAG, "---开启---");

        //注册receiver
        Log.e(TAG, "注册广播");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CONNECT_STATUS);

        try {
            mContext.registerReceiver(PrinterStatusBroadcastReceiver, filter);
        }catch (Exception e){}


        // bindService
        Log.e(TAG, "正在绑定服务");
        Intent intent = new Intent(mContext, GpPrintService.class);
        try {
            if (mContext.bindService(intent, mConn, Context.BIND_AUTO_CREATE))
            {// 第二次打印的时候可能没有走onServiceConnected回调 重复尝试
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isCallBack){
                            start();
                        }
                    }
                },1000);
                Log.e(TAG, "绑定服务成功");
                if (mGpManagerListener != null)
                {
                    mGpManagerListener.onBindService();
                }
            } else
            {
                Log.e(TAG, "绑定服务失败");
                if (mGpManagerListener != null)
                {
                    mGpManagerListener.onBindServiceFailed();
                }
            }
        }catch (Exception e){

        }

    }

    private void openPort()
    {
        int rel = 0;
        try
        {
            Log.e(TAG, "正在开启设备端口");
            if (mGpService == null)
            {
                if (mGpManagerListener != null)
                {
                    mGpManagerListener.onPortOpenFailed();
                }
                return;
            }

            rel = mGpService.openPort(PRINTER_ID, PortParameters.BLUETOOTH, mPrinterAddr, 0);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }

        //若开启成功
        GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
        if (r == GpCom.ERROR_CODE.SUCCESS || r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN)
        {
            Log.e(TAG, "开启设备端口成功");
            if (mGpManagerListener != null)
            {
                mGpManagerListener.onPortOpen();
            }

        } else
        {
            Log.e(TAG, "开启设备端口失败");
            if (mGpManagerListener != null)
            {
                mGpManagerListener.onPortOpenFailed();
            }
        }
    }

    private void closePort()
    {
        try
        {
            Log.e(TAG, "关闭设备端口");
            if (mGpService != null)
            {
                mGpService.closePort(PRINTER_ID);
            }
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 结束gp管理 不要在onSuccess中
     */
    public void stop()
    {
        closePort();
        Log.e(TAG, "解绑服务");
        try {
            mContext.unbindService(mConn);
        }catch (Exception e){

        }
        Log.e(TAG, "注销广播");
        mContext.unregisterReceiver(PrinterStatusBroadcastReceiver);

        Log.e(TAG, "---结束---");
    }

    public boolean isPortConnected()
    {
        return isPortConnected;
    }

    public void setPortConnected(boolean portConnected)
    {
        isPortConnected = portConnected;
    }

    public boolean isServiceConnected()
    {
        return isServiceConnected;
    }

    public void setServiceConnected(boolean serviceConnected)
    {
        isServiceConnected = serviceConnected;
    }

    public void test(final PrintListener listener)
    {
        try
        {
            Log.e(TAG, "正在打印测试页");
            mGpService.printeTestPage(PRINTER_ID);
            Log.e(TAG, "打印测试成功");
            if (listener != null)
            {
                listener.onSuccessCannotStop();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "打印测试失败");
            if (listener != null)
            {
                listener.onError(e.getMessage());
            }
        }
    }

    public int getPrinterCommandType()
    {
        int type = -1;
        if (mGpService == null)
        {
            return type;
        }

        try
        {
            type = mGpService.getPrinterCommandType(PRINTER_ID);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return type;
    }

    public void printESC(EscCommand escCommand, final PrintListener listener)
    {
        if (mGpService == null)
        {
            return;
        }

        Vector<Byte> datas = escCommand.getCommand();
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = new byte[Bytes.length];
        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = Bytes[i];
        }
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try
        {
            rel = mGpService.sendEscCommand(PRINTER_ID, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r == GpCom.ERROR_CODE.SUCCESS)
            {
                Log.e(TAG, "打印成功");
                if (listener != null)
                {
                    listener.onSuccessCannotStop();
                }
            } else
            {
                Log.e(TAG, "打印失败");
                if (listener != null)
                {
                    listener.onError("打印失败");
                }
            }
        } catch (RemoteException e)
        {
            Log.e(TAG, "打印失败");
            e.printStackTrace();
            if (listener != null)
            {
                listener.onError("打印失败");
            }
        }
    }

    public void printTSC(TscCommand tscCommand, final PrintListener listener)
    {
        if (mGpService == null)
        {
            return;
        }

        Vector<Byte> datas = tscCommand.getCommand();
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = new byte[Bytes.length];
        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = Bytes[i];
        }
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try
        {
            rel = mGpService.sendTscCommand(PRINTER_ID, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r == GpCom.ERROR_CODE.SUCCESS)
            {
                Log.e(TAG, "打印成功");
                if (listener != null)
                {
                    listener.onSuccessCannotStop();
                }
            } else
            {
                Log.e(TAG, "打印失败");
                if (listener != null)
                {
                    listener.onError("打印失败");
                }
            }
        } catch (RemoteException e)
        {
            Log.e(TAG, "打印失败");
            e.printStackTrace();
            if (listener != null)
            {
                listener.onError("打印失败");
            }
        }
    }


    public String getPrinterStatus()
    {
        int status = 0;
        try
        {
            status = mGpService.queryPrinterStatus(PRINTER_ID, 500);
        } catch (Exception e)
        {
            e.printStackTrace();
            return "服务异常";
        }
        String str = null;
        if (status == GpCom.STATE_NO_ERR)
        {
            str = "打印机正常";
        } else if ((byte) (status & GpCom.STATE_OFFLINE) > 0)
        {
            str = "打印机脱机";
        } else if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0)
        {
            str = "打印机缺纸";
        } else if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0)
        {
            str = "打印机开盖";
        } else if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0)
        {
            str = "打印机出错";
        }
        return str;
    }

    public interface GpManagerListener
    {
        void onBindService();

        void onBindServiceFailed();

        void onServiceConnected();

        void onServiceDisconnected();

        void onPortOpen();

        void onPortOpenFailed();

        void onPortConnected();

        void onPortDisconnected();

        void onReady();

        void onReadyFailed();
    }

    public interface PrintListener
    {
        //打印成功，但是打印还没有结束，不能stop
        void onSuccessCannotStop();

        void onError(String error);
    }
}
