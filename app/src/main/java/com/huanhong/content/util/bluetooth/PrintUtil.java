package com.huanhong.content.util.bluetooth;

import android.content.Context;
import android.text.TextUtils;

import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.huanhong.content.util.TimeUtil;
import com.zyn.lib.util.SharedPreferencesUtils;
import com.zyn.lib.util.Utils;

/**
 * Created by 坎坎 on 2017/11/21.
 */

public class PrintUtil {
    private static GpManager mGpManager=null ;
    public static void print(Context context){
        String add = SharedPreferencesUtils.readData(BluetoothManager.TAG_PRINT);
        if(TextUtils.isEmpty(add)){
            Utils.toastShort("请匹配好打印机");
            return;
        }

        if(mGpManager !=null){
            try {
                mGpManager.stop();
            }catch (Exception e){
            }
        }
        startEscPrint(context,add, getOrderEscCommand());
    }

    private static void startEscPrint(final Context context, String printerAddr, final EscCommand escCommand) {
        Utils.toastShort("准备打印");
        mGpManager = new GpManager(context, printerAddr,new  GpManager.GpManagerListener (){
            @Override
            public void onBindService() {
            }
            @Override
            public void onBindServiceFailed() {
            }
            @Override
            public void onServiceConnected() {
            }
            @Override
            public void onServiceDisconnected() {
            }
            @Override
            public void onPortOpen() {
            }
            @Override
            public void onPortOpenFailed() {
            }
            @Override
            public void onPortConnected() {
            }
            @Override
            public void onPortDisconnected() {
            }
            @Override
            public void onReady() {
                if (mGpManager != null) {
                    if (mGpManager.getPrinterCommandType() == GpCom.ESC_COMMAND) {
                        Utils.toastShort("正在打印");
                        mGpManager.printESC(escCommand, new  GpManager.PrintListener() {
                            @Override
                            public void onSuccessCannotStop() {
                                Utils.toastShort("打印成功");
                            }
                            @Override
                            public void onError(String error) {
                                Utils.toastShort(mGpManager.getPrinterStatus());
                            }
                        });
                    }
                }
            }

            @Override
            public void onReadyFailed() {
                Utils.toastShort(mGpManager.getPrinterStatus());
            }
        });
        mGpManager.start();
    }

    private static EscCommand getOrderEscCommand(){
        EscCommand esc = new EscCommand();
        esc.addPrintAndFeedLines((byte) 5);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);//设置打印居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON,
                EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);//设置为倍高倍宽
        esc.addText("焕鸿");
        esc.addPrintAndFeedLines((byte) 2);
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
        esc.addText("[购物单]");
        esc.addPrintAndLineFeed();
        esc.addPrintAndFeedLines((byte) 1);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText("购物时间："+ TimeUtil.dateFormat(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
        esc.addPrintAndLineFeed();
        esc.addText("订单号：000000000000000000000");
        esc.addPrintAndLineFeed();
        esc.addText("--------------------------------");
        esc.addPrintAndLineFeed();
        threeText(esc,"商品","数量","金额");
        esc.addText("--------------------------------");
        esc.addPrintAndLineFeed();
        threeText(esc,"哈哈哈哈","×1","100.0");
        esc.addText("--------------------------------");
        threeText(esc,"合计","×1","100.0");

        esc.addPrintAndFeedLines((byte)4);
        return esc;
    }

    private static void threeText(EscCommand esc,String str1,String str2,String str3){
        esc.addText(str1);
        esc.addSetHorAndVerMotionUnits((byte)7, (byte)0);
        esc.addSetAbsolutePrintPosition((byte)8);
        esc.addText(str2);
        esc.addSetAbsolutePrintPosition((byte)11);
        esc.addText(str3);
        esc.addPrintAndLineFeed();
    }
}
