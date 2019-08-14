package com.huanhong.content.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.zyn.lib.app.AppUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/12/14.
 */
public class FileDownLoadUtil {
    private static DownLoadCallback downLoadCallback;
    static Handler handler = new Handler(AppUtils
            .getBaseApplication()
            .getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg==null)
            {
                return;
            }

            if (msg.what == 1) {
                if(downLoadCallback!=null)
                {
                    downLoadCallback.downLoadStart();
                }
            } else if (msg.what == 2) {
                if(downLoadCallback!=null)
                {
                    downLoadCallback.downLoadComplete();
                    downLoadCallback.synchronizeComplete();
                }
            } else {
                Bundle bundle = msg.getData();
                Log.e("===========", "共" + bundle.getInt("size") + "个文件，正在下载第" + bundle.getInt("index") + "个文件，下载进度为" + bundle.getInt("percent") + "%");
                if(downLoadCallback!=null) {
                    downLoadCallback.downLoadIng(bundle.getInt("index"), bundle.getInt("size"), bundle.getInt("percent"));
                }
            }
        }
    };

    public static void downLoad(final List<String> urlList, final List<String> pathList, final DownLoadCallback callback, final int nowIndex, final int allSize) {
        downLoadCallback = callback;
        FileDownloader
                .getImpl()
                .create(urlList.get(nowIndex))
                .setPath(pathList.get(nowIndex))
                .setAutoRetryTimes(5)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.e("========pending=====", "pending");
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        if (callback != null) {
                            if (nowIndex == 0) {
                                handler.sendEmptyMessage(1);//开始下载
                            }
//                               sendMsg("共"+allSize+"个文件，第"+(nowIndex+1)+"个文件连接中...");
                        }
                        Log.e("========connected=====", "connected");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        if (callback != null) {
                            sendMsg(nowIndex + 1, allSize, (int) ((float) soFarBytes / totalBytes * 100));
                        }
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        if (callback != null) {
                            if (nowIndex < allSize - 1) {
                                sendMsg(nowIndex + 1, allSize, 100);
                                int i = nowIndex + 1;
                                downLoad(urlList, pathList, callback, i, urlList.size());
                            } else {
                                handler.sendEmptyMessage(2);
                            }
                        }

                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.e("========paused=====", "paused");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        if (callback != null) {
                            if(task!=null)
                            {
                                callback.error("异常：\n"+task.getUrl()+"\n"+e.getMessage());
                            }
                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.e("========warn=====", "warn");
                    }
                })
                .start();
    }

    private static void sendMsg(int index, int size, int percent) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putInt("size", size);
        bundle.putInt("percent", percent);
        message.setData(bundle);
        message.what = 3;
        handler.sendMessage(message);
    }

    public interface DownLoadCallback {
        void downLoadIng(int index, int size, int percent);

        void downLoadStart();

        void downLoadComplete();

        void synchronizeComplete();

        void error(String error);

    }
}