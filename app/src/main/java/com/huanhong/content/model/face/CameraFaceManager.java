package com.huanhong.content.model.face;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.huanhong.content.model.face.util.FaceRect;
import com.huanhong.content.model.face.util.ParseResult;
import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.util.Accelerometer;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by tuka2401 on 2017/4/12.
 */

public class CameraFaceManager {
    //屏幕自然方向和activity方向的顺时针角度
    private static int mDegrees;
    //屏幕自然方向和相机感应方向的顺时针角度
    private static int mOrientation;
    private final String AUTH_ID = "auth_id";
    private final Object mOtherLock = new Object();
    private Activity mActivity;
    // Camera nv21格式预览帧的尺寸，640 * 480，相机的宽高是横屏模式
    private int PREVIEW_WIDTH = 640;
    private int PREVIEW_HEIGHT = 480;
    // 预览帧数据存储数组和缓存数组
    private volatile byte[] nv21;
    private byte[] buffer;
    private Thread mOtherThread;
    private Camera mCamera;
    // 加速度感应器，用于获取手机的朝向
    private Accelerometer mAcc;
    // FaceDetector对象，集成了离线人脸识别：人脸检测、视频流检测功能
    private FaceDetector mFaceDetector;
    //在线图片检测
    private FaceRequest mFaceRequest;
    private boolean mIsFrontCamera = true;

    public CameraFaceManager(Activity activity) {
        mActivity = activity;
        nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        buffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        mAcc = new Accelerometer(mActivity);
        mFaceDetector = FaceDetector.createDetector(mActivity, null);
        mFaceRequest = new FaceRequest(mActivity);
    }

    /**
     * 保证预览方向正确
     *
     * @param activity
     * @param cameraId
     */
    public static int getCameraDisplayOrientation(Activity activity, int cameraId) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        //屏幕自然方向和activity方向的顺时针角度，info.orientation是屏幕自然方向和相机感应方向的顺时针角度
        int rotation = activity
                .getWindowManager()
                .getDefaultDisplay()
                .getRotation();

        mOrientation = info.orientation;
        mDegrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                mDegrees = 0;
                break;
            case Surface.ROTATION_90:
                mDegrees = 90;
                break;
            case Surface.ROTATION_180:
                mDegrees = 180;
                break;
            case Surface.ROTATION_270:
                mDegrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (mOrientation + mDegrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (mOrientation - mDegrees + 360) % 360;
        }
        return result;
    }

    public void onResume() {
        log("onResume");
        if (null != mAcc) {
            mAcc.start();
            log("加速度监控已开启");
        }
        openCamera();
        startOtherThread();
    }

    public void onPause() {
        log("onPause");
        if (null != mAcc) {
            mAcc.stop();
            log("加速度监控已关闭");
        }
        closeCamera();
        stopOtherThread();
    }

    public void onDestroy() {
        log("onDestroy");
        if (null != mFaceDetector) {
            // 销毁对象
            mFaceDetector.destroy();
        }
    }

    private void log(String msg) {
        Log.e(getClass().getSimpleName(), msg);
    }

    private void startOtherThread() {
        stopOtherThread();
        mOtherThread = new OtherThread();
        mOtherThread.start();
        log("开启OtherThread");
    }

    private void stopOtherThread() {
        if (mOtherThread != null) {
            mOtherThread.interrupt();
            mOtherThread = null;
            log("关闭OtherThread");
        }
    }

    private boolean openCamera() {
        if (mCamera != null) {
            closeCamera();
        }

        if (!checkCameraPermission()) {
            onNoCameraPermission();
            return false;
        }

        try {
            if (mIsFrontCamera) {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
            if (mCamera != null) {
                log("相机已开启");
            }
            setCameraParameters();
        } catch (Exception e) {
            e.printStackTrace();
            log("相机开启失败");
            closeCamera();
            return false;
        }

        try {
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 设置照相机参数
     */
    private void setCameraParameters() {
        if (mCamera == null) {
            return;
        }

        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewFormat(ImageFormat.NV21);
        params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        if (mIsFrontCamera) {
            mCamera.setDisplayOrientation(getCameraDisplayOrientation(mActivity, Camera.CameraInfo.CAMERA_FACING_FRONT));
        } else {
            mCamera.setDisplayOrientation(getCameraDisplayOrientation(mActivity, Camera.CameraInfo.CAMERA_FACING_BACK));
        }
        mCamera.setParameters(params);


        mCamera.setPreviewCallback(new Camera.PreviewCallback() {

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                synchronized (mOtherLock) {
                    System.arraycopy(data, 0, nv21, 0, data.length);
                }
            }
        });
    }

    private void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            /**
             * Note: 一定要调用 _mCamera.setPreviewCallback(null), 否则出现 Method called
             * after release() 异常.
             */
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            log("相机已关闭");
        }
    }

    private boolean checkCameraPermission() {
        int status = mActivity.checkPermission(Manifest.permission.CAMERA, Process.myPid(), Process.myUid());
        if (PackageManager.PERMISSION_GRANTED == status) {
            return true;
        }
        return false;
    }


    private void onNoCameraPermission() {
        Toast
                .makeText(mActivity, "没有相机权限,无法开启相机", Toast.LENGTH_SHORT)
                .show();
    }

    public static class CameraFaceEvent {

    }

    private class OtherThread extends Thread {

        volatile long lastFace = 0;
        volatile boolean hasFace = false;
        Handler handler = new Handler(Looper.getMainLooper());

        public void run() {

            while (!isInterrupted()) {
                try {
                    if (nv21 == null) {
                        continue;
                    }

                    synchronized (mOtherLock) {
                        System.arraycopy(nv21, 0, buffer, 0, nv21.length);
                    }

                    // 获取手机朝向，返回值0,1,2,3分别表示0,90,180和270度
                    int direction = Accelerometer.getDirection();
                    //根据mOrientation判断加速度修正（需要顺时针旋转图像多少度能使图像变为正向）
                    int direction_offset = (mOrientation - 90 + 360) / 90 % 4;
                    //修正
                    direction = (direction + direction_offset + 4) % 4;

                    //分析结果
                    String result = mFaceDetector.trackNV21(buffer, PREVIEW_WIDTH, PREVIEW_HEIGHT, 1, direction);
                    FaceRect[] faces = ParseResult.parseResult(result);
                    //有时候前置摄像头的mOrientation不正确，如应返回180但是返回0，尝试镜像处理
                    if ((faces == null || faces.length <= 0) && mIsFrontCamera) {
                        // 前置摄像头预览显示的是镜像，需要将手机朝向换算成摄相头视角下的朝向。
                        // 转换公式：a' = (360 - a)%360，a为人眼视角下的朝向（单位：角度）
                        direction = (direction + 2) % 4;
                        result = mFaceDetector.trackNV21(buffer, PREVIEW_WIDTH, PREVIEW_HEIGHT, 1, direction);
                        faces = ParseResult.parseResult(result);
                    }
                    Log.w(getClass().getSimpleName(), "result:" + result);

                    if (faces != null && faces.length > 0) {
                        if(!hasFace)
                        {
                            //TODO
                        }

                        //  三秒中都有人脸
                        hasFace = true;
                        if (lastFace == 0) {
                            lastFace = System.currentTimeMillis();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (hasFace) {
                                        EventBus
                                                .getDefault()
                                                .post(new CameraFaceEvent());
                                    }
                                    lastFace = 0;
                                }
                            }, 3000);
                        }
                    } else {
                        hasFace = false;
                    }

                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }

        }
    }

}
