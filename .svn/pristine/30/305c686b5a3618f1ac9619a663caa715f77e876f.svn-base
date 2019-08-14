package com.zyn.lib.view.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zyn.lib.R;
import com.zyn.lib.util.FileUtils;
import com.zyn.lib.util.UriUtils;

import java.io.File;
import java.util.Arrays;

public class UploadImageDialog implements View.OnClickListener {
    //常量
    private final String[] PERMISSIONS = {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    private final int REQUEST_CODE_PERMISSIONS = 1621;
    private final int REQUEST_CODE_PICK = 1618;
    private final int REQUEST_CODE_PHOTO = 1619;
    private final int REQUEST_CODE_PHOTOZOOM = 1620;
    private final String IMAGE_UNSPECIFIED = "image/*";

    //变量
    private Context mContext;
    private Dialog mDialog;
    private static Uri mPhotoUri;
    private static Uri mCropUri;
    private String mPath;
    private OnUploadImageListener mOnUploadImageListener;

    public UploadImageDialog(Context context, OnUploadImageListener onUploadImageListener) {
        mContext = context;
        mOnUploadImageListener = onUploadImageListener;
        initDialog();
    }

    private void initDialog() {
        mDialog = new Dialog(mContext, R.style.LibFloatingDialog);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(R.layout.lib_dialog_choose_image);
        mDialog.findViewById(R.id.dialog_choose_image_btn_take_photo).setOnClickListener(this);
        mDialog.findViewById(R.id.dialog_choose_image_btn_gallery).setOnClickListener(this);
        mDialog.findViewById(R.id.dialog_choose_image_btn_cancel).setOnClickListener(this);
        setViewBottom(mDialog);
    }


    private void setViewBottom(Dialog dialog) {
        Window win = dialog.getWindow();
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        win.getDecorView().setPadding(0, 0, 0, height / 6);
        win.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;

        switch (requestCode) {
            case REQUEST_CODE_PHOTO:
                startPhotoZoom(mPhotoUri);
                break;
            case REQUEST_CODE_PICK: {
                if (data == null) {
                    return;
                }
                startPhotoZoom(data.getData());
            }
            break;
            case REQUEST_CODE_PHOTOZOOM: {
//                mPath = UriUtils.getPath(mContext, mCropUri);
                if (mOnUploadImageListener != null) {
                    mOnUploadImageListener.onGotImage(mCropUri);
                }
            }
            break;
        }
    }

    private void startPhotoZoom(Uri uri) {

        String path = UriUtils.getPath(mContext, uri);
        File file = new File(path);
        mCropUri = Uri.fromFile(new File(FileUtils.getDiskCacheDir(mContext), file.getName()));

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 512);
        intent.putExtra("outputY", 512);
        intent.putExtra("scale", true);
        intent.putExtra("output", mCropUri);
        intent.putExtra("return-data", false);//小米true会无法返回
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_PHOTOZOOM);
    }

//    private Uri compress(Uri uri) {
//        String path = UriUtils.getPath(mContext, uri);
//        String endPath = IMAGE_PATH + System.currentTimeMillis() + ".jpg";
//
//        ImageUtils.compress(path, endPath);
//        return Uri.fromFile(new File(endPath));
//    }

    public void show() {
        if (Build.VERSION.SDK_INT >= 23) {
            ((Activity) mContext).requestPermissions(PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        } else {
            mDialog.show();
        }
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.dialog_choose_image_btn_gallery) {
            startPick();
            dismiss();
        } else if (i == R.id.dialog_choose_image_btn_take_photo) {
            startPhoto();
            dismiss();
        } else if (i == R.id.dialog_choose_image_btn_cancel) {
            dismiss();
        }
    }

    private void startPick() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_PICK);
    }

    private void startPhoto() {
        mPhotoUri = Uri.fromFile(new File(FileUtils.getDiskDirPath(mContext, Environment.DIRECTORY_PICTURES), String.format("/%d.jpg", System.currentTimeMillis())));
        // TODO Auto-generated method stub
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode != REQUEST_CODE_PERMISSIONS || !Arrays.equals(PERMISSIONS, permissions)) {
            return;
        }
        if (grantResults != null && grantResults.length == 0) {
            mDialog.show();
        } else if (grantResults != null && grantResults.length > 0) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            mDialog.show();
        }
    }

    public interface OnUploadImageListener {
        void onGotImage(Uri path);
    }
}
