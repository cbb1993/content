package com.huanhong.content.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huanhong.content.R;
import com.huanhong.content.adapter.FileAdapter;
import com.huanhong.content.model.DownLoadModel;
import com.huanhong.content.model.PlayMsgModel;
import com.huanhong.content.model.uploadfile.FileUploadService;
import com.huanhong.content.model.uploadfile.UpLoadListener;
import com.huanhong.content.model.uploadfile.UpLoadUtil;
import com.huanhong.content.util.FileDownLoadUtil;
import com.huanhong.content.util.FileJsonUtil;
import com.huanhong.content.util.FileUtil;
import com.huanhong.content.util.NormalUtil;
import com.huanhong.content.util.TextSizeUtil;
import com.huanhong.content.view.dialog.CreatFileDialog;
import com.huanhong.content.view.dialog.ProgressDialog;
import com.zyn.lib.activity.BaseActivity;
import com.zyn.lib.constant.HostConstants;
import com.zyn.lib.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Administrator on 2016/12/22.
 */

public class FileActivity extends BaseActivity implements FileDownLoadUtil.DownLoadCallback {

    private static final File ROOT = new File(Environment.getExternalStorageDirectory(), NormalUtil.DATABASE);
    private File currentFile=ROOT;
    private TextView activity_file_tv_canlce;//取消删除
    private TextView activity_file_tv_sure;//确认删除
    private LinearLayout activity_file_top_ll;//顶部目录导航
    private TextView activity_file_tv_creat;//新建
    private TextView activity_file_tv_delete;//删除
    private TextView activity_file_tv_download;
    private TextView activity_file_tv_play;
    private ListView activity_file_listview;//文件列表
    private List<File> dataList = new ArrayList<>();//当前文件列表
    private FileAdapter adapter;
    private int btnFlag=0; // 0默认 1 删除 2 选择本地播放列表
    private List<File> deleteFileList=new ArrayList<>();//删除列表
    private List<String> playList=new ArrayList<>();//播放列表
    private TextView activity_file_tv_back;
    private TextView tv_title;
//    ProgressDialog progressDialog;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_file_percent_layout;
    }
    @Override
    protected void initView() {
        super.initView();
        NormalUtil.initApplicationContext(this);
        activity_file_tv_play= (TextView) findViewById(R.id.activity_file_tv_play);
        activity_file_tv_canlce= (TextView) findViewById(R.id.activity_file_tv_canlce);
        activity_file_tv_sure= (TextView) findViewById(R.id.activity_file_tv_sure);
        activity_file_top_ll= (LinearLayout) findViewById(R.id.activity_file_top_ll);
        activity_file_tv_sure= (TextView) findViewById(R.id.activity_file_tv_sure);
        activity_file_tv_creat= (TextView) findViewById(R.id.activity_file_tv_creat);
        activity_file_tv_delete= (TextView) findViewById(R.id.activity_file_tv_delete);
        activity_file_tv_download= (TextView) findViewById(R.id.activity_file_tv_download);
        activity_file_listview= (ListView) findViewById(R.id.activity_file_listview);
        activity_file_tv_back= (TextView) findViewById(R.id.activity_file_tv_back);
        tv_title= (TextView) findViewById(R.id.tv_title);
//        FileUtil.mkDir(Environment.getExternalStorageDirectory(), DATABASE, false,null,false);
//        FileUtil.mkDir(ROOT, APPDATABASE, false,null,false);
//        FileUtil.mkDir(ROOT, LOCALDATABASE, false,null,false);

        TextSizeUtil.setTextSize(this,tv_title,0.04f);
        TextSizeUtil.setTextSize(this,activity_file_tv_back,0.04f);
        TextSizeUtil.setTextSize(this,activity_file_tv_play,0.04f);
    }

    @Override
    protected void initListener() {
        super.initListener();
        activity_file_tv_back.setOnClickListener(this);
        activity_file_tv_canlce.setOnClickListener(this);
        activity_file_tv_sure.setOnClickListener(this);
        activity_file_tv_creat.setOnClickListener(this);
        activity_file_tv_delete.setOnClickListener(this);
        activity_file_tv_download.setOnClickListener(this);
        activity_file_tv_play.setOnClickListener(this);
        activity_file_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File file = dataList.get(i);
                if ( btnFlag == 1) {
                    CheckBox checkbox_select = (CheckBox) view.findViewById(R.id.item_file_checkbox_select);
                        /*选择状态*/
                    if (checkbox_select.isChecked()) {
                        checkbox_select.setChecked(false);
                    } else checkbox_select.setChecked(true);
                        deleteFile(checkbox_select, file);
                } else  if ( btnFlag == 2) {
                    CheckBox checkbox_select = (CheckBox) view.findViewById(R.id.item_file_checkbox_select);
                        /*选择状态*/
                    if (checkbox_select.isChecked()) {
                        checkbox_select.setChecked(false);
                    } else checkbox_select.setChecked(true);
                         playFileList(checkbox_select,file);
                    if(playList==null||playList.size()==0){
                        if(file.isFile()){
                            NormalUtil.showToast("请选择可播放的视频文件");
                        }else {
                            NormalUtil.showToast("请选择有可播放的视频的文件夹");
                        }
                        checkbox_select.setChecked(false);
                    }else {
                        PlayMsgModel playMsgModel=PlayMsgModel.getPlayMsgInfo();
                        if(playMsgModel==null){
                            playMsgModel=new PlayMsgModel();
                        }
                        playMsgModel.setType("2");
                        playMsgModel.setPlayList(playList);
                        playMsgModel.setFilePath(file.getPath());
                        String s= new Gson().toJson(playMsgModel);
                        SharedPreferencesUtils.addData(NormalUtil.PLAYMSG,s);
                        Intent intent=new Intent();
                        intent.putExtra("filePath",file.getPath());
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                } else {
                    /*如果是文件夹  就点击进去  如果是文件  暂时不做操作*/
                    if (file.isFile()) {

                    } else {
                        currentFile = file;
                        addNormalPath(file.getName(), file.getPath());
                        getFileDir(file.getPath());
                    }
                }
            }
        });
    }
    @Override
    protected void initOther() {
        super.initOther();
        addRootPath();
        new Thread() {
            @Override
            public void run() {
                super.run();
                getFileDir(ROOT.getPath());
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
                case R.id.activity_file_tv_creat:
                    if(btnFlag==0){
                    new CreatFileDialog(FileActivity.this, new CreatFileDialog.OnCreatCallback() {
                        @Override
                        public void onComfirmClick(String FileName, boolean isFile) {
                            FileUtil.mkDir(currentFile, FileName, isFile, new FileUtil.CreatStatusCallback() {
                                @Override
                                public void success() {
                                    getFileDir(currentFile.getPath());
                                }
                            },true);
                        }
                    }).show();
            }
                break;
            case R.id.activity_file_tv_back:
                finish();
                break;
            case R.id.activity_file_tv_delete:
                if(btnFlag==0){
                    activity_file_tv_canlce.setVisibility(View.VISIBLE);
                    activity_file_tv_sure.setVisibility(View.VISIBLE);
                    btnFlag=1;
                    adapter.setSelectFlag(true);
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.activity_file_tv_sure:
                if(btnFlag==2){
                    activity_file_tv_canlce.setVisibility(View.GONE);
                    activity_file_tv_sure.setVisibility(View.GONE);
                    btnFlag=0;
                }else {
                    FileUtil.delete(deleteFileList);
                    adapter.setSelectFlag(false);
                    getFileDir(currentFile.getPath());
                    btnFlag = 0;
                    deleteFileList.clear();
                    playList.clear();
                    activity_file_tv_canlce.setVisibility(View.GONE);
                    activity_file_tv_sure.setVisibility(View.GONE);
                }
                break;
            case R.id.activity_file_tv_canlce:
                adapter.setSelectFlag(false);
                adapter.notifyDataSetChanged();
                btnFlag = 0;
                deleteFileList.clear();
                playList.clear();
                activity_file_tv_sure.setVisibility(View.GONE);
                activity_file_tv_canlce.setVisibility(View.GONE);
                break;
            case R.id.activity_file_tv_download:

                break;
            case R.id.activity_file_tv_play:
                if(btnFlag==0){
                    activity_file_tv_canlce.setVisibility(View.VISIBLE);
                    btnFlag=2;
                    adapter.setSelectFlag(true);
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                Button button = (Button) v;
                File file = (File) button.getTag();
                if (file.getPath().length() < currentFile.getPath().length()) {
                    btnFlag = 0;
                    deleteFileList.clear();
                    playList.clear();
                    adapter.setSelectFlag(false);
                    for (int i = activity_file_top_ll.getChildCount() - 1; i > -1; i--) {
                        Button button1 = (Button) activity_file_top_ll.getChildAt(i);
                        if (button.getText().toString().equals(button1.getText().toString())) {
                            getFileDir(file.getPath());
                            break;
                        }
                        activity_file_top_ll.removeViewAt(i);
                    }
                }else {
                    activity_file_tv_sure.setVisibility(View.GONE);
                    activity_file_tv_canlce.setVisibility(View.GONE);
                }
                currentFile=file;
                break;
        }
    }

    public boolean getFileDir(String filePath) {
        try {
            File f = new File(filePath);
            File[] files = f.listFiles();// 列出所有文件
            if (files != null || files.length == 0) {
                dataList.clear();
                for (File file : files) {
                    dataList.add(file);
                }
                if (adapter == null) {
                    adapter = new FileAdapter();
                    adapter.setList(dataList);
                    activity_file_listview.setAdapter(adapter);
                } else {
                    adapter.setList(dataList);
                    adapter.notifyDataSetChanged();
                }
                if (dataList == null || dataList.size() == 0) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void deleteFile(CheckBox checkbox_select,  File file) {
        if(checkbox_select.isChecked()){
            deleteFileList.add(file);
        }else {
            if(deleteFileList.contains(file)){
                deleteFileList.remove(file);
            }
        }
    }

    private void playFileList(CheckBox checkbox_select,  File file) {
        if(checkbox_select.isChecked()){
            addAllFile(file);
        }else {
            removeFile(file);
        }
    }

    private void addAllFile(File file){
        if(file.isFile()){
            if(file.getPath().endsWith(".mp4")){
                playList.add(file.getPath());
            }
        }else {
            File[] files=file.listFiles();
            if(files!=null){
                for(File f:files){
                    addAllFile(f);
                }
            }
        }
    }

    private void removeFile(File file){
        if(file.isFile()){
            if(playList.contains(file.getPath())){
                playList.remove(file);
            }
        }else {
            File[] files=file.listFiles();
            if(files!=null){
                for(File f:files){
                    removeFile(f);
                }
            }
        }
    }

    /*添加根目录*/
    private void addRootPath() {
        activity_file_top_ll.removeAllViews();
        Button button = new Button(this);
        button.setText(NormalUtil.DATABASE);
        button.setTextColor(Color.BLACK);
        button.setTextSize(14.0f);
        button.setBackground(getResources().getDrawable(R.drawable.qpath_item_bg));
        activity_file_top_ll.addView(button);
        button.setOnClickListener(this);
        button.setTag(ROOT);
    }
    /*添加详细目录*/
    private void addNormalPath(String btnText, String path) {
        Button button = new Button(this);
        button.setText(btnText);
        button.setTextColor(Color.BLACK);
        button.setTextSize(14.0f);
        activity_file_top_ll.addView(button);
        button.setBackground(getResources().getDrawable(R.drawable.qpath_item_bg));
        button.setOnClickListener(this);
        button.setTag(new File(path));
    }


    @Override
    public void downLoadComplete() {
        getFileDir(currentFile.getPath());
    }

    @Override
    public void synchronizeComplete() {
//        progressDialog.dismiss();
        Log.e("================","synchronizeComplete()");
        getFileDir(currentFile.getPath());
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void downLoadIng(int index,int size,int percent) {
        Log.e("===========","共"+size+"个文件，正在下载第"+index+"个文件，下载进度为"+percent+"%");
//        progressDialog.setMsg("");
    }
    @Override
    public void downLoadStart() {
        getFileDir(currentFile.getPath());
    }

    @Override
    protected boolean isColorStatusBar() {
        return false;
    }


}
