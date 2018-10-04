package com.example.trt.servicedownload.ui;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trt.servicedownload.BaseActivity;
import com.example.trt.servicedownload.Event.DownloadEvent;
import com.example.trt.servicedownload.Event.UpStopEvent;
import com.example.trt.servicedownload.Event.UploadEvent;
import com.example.trt.servicedownload.MyApplication;
import com.example.trt.servicedownload.R;
import com.example.trt.servicedownload.module.Ffile;
import com.example.trt.servicedownload.service.DownladService;
import com.example.trt.servicedownload.service.UploadService;

import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Trt on 2018/6/22.
 */

public class UploadActivity extends BaseActivity {
    @BindView(R.id.upload) Button upload;
    @BindView(R.id.stop) Button stop;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.back) ImageView back;
    @BindView(R.id.bar) ProgressBar bar;
    @BindView(R.id.up_down) TextView upDown;

    private String path;
    private String ftpFileName;
    private String strLocalFile;
    private int progress;
    private FTPClient ftpClient;
    private String code="iso-8859-1";
    private List<Ffile> uploadFile=null;
    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if   (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_upload);
        EventBus.getDefault().register(this);
    }
    protected void bindView(){
        Intent intent = getIntent();
        path=intent.getStringExtra("path");
        ftpFileName=intent.getStringExtra("name");
        strLocalFile=path+ "/"+ftpFileName;
        name.setText(ftpFileName);
        uploadFile=((MyApplication)getApplication()).getUploadFile();
        if (uploadFile!=null){
            for (Ffile ffile:uploadFile){
                System.out.println("==="+1111111);
                if (ffile.getFilename().equals(ftpFileName)){
                    System.out.println("==="+ffile.getFilename());
                    openFile();
                    break;
                }
            }
        }
    }
    @OnClick(R.id.upload) void startDownload(){
            Intent intent = new Intent(this, UploadService.class);
            intent.putExtra("ftpFileName", ftpFileName);
            intent.putExtra("strLocalFile", strLocalFile);
            startService(intent);
    }
    @OnClick(R.id.stop) void stopUpload(){
            EventBus.getDefault().post(new UpStopEvent(true,ftpFileName));

    }
    @OnClick(R.id.back) void back(){
        onBackPressed();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(UploadEvent messageEvent) {
        if (messageEvent.getFilename().equals(ftpFileName)) {
            bar.setProgress(messageEvent.getProgress());
            if (messageEvent.getProgress()>=100){
                openFile();
                ((MyApplication)getApplication()).getUploadFile().add(new Ffile(ftpFileName));
            }
        }
    }

    public void openFile(){
        bar.setVisibility(View.INVISIBLE);
        upDown.setVisibility(View.VISIBLE);
        upload.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }


}
