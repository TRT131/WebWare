package com.example.trt.servicedownload.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.trt.servicedownload.BaseActivity;
import com.example.trt.servicedownload.Event.DownStopEvent;
import com.example.trt.servicedownload.Event.UpStopEvent;
import com.example.trt.servicedownload.module.Ffile;
import com.example.trt.servicedownload.service.DownladService;
import com.example.trt.servicedownload.Event.DownloadEvent;
import com.example.trt.servicedownload.R;
import com.example.trt.servicedownload.common.Const;
import com.example.trt.servicedownload.util.CallOtherOpeanFile;

import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Trt on 2017/12/10.
 */

public class DownLoadActivity extends BaseActivity {

    @BindView(R.id.download) Button download;
    @BindView(R.id.stop) Button stop;
    @BindView(R.id.openPath) Button openPath;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.back) ImageView back;
    @BindView(R.id.bar) ProgressBar bar;

    private String ftpFileName;
    private File strLocalFile;
    private String path= Const.downLoadPath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_download);
        EventBus.getDefault().register(this);
    }
    public void bindView(){
        Ffile ffile= (Ffile) getIntent().getSerializableExtra("dFile");
        ftpFileName=ffile.getFilename();
        name.setText(ftpFileName);
        strLocalFile=new File(path+"/"+ftpFileName);
        if (strLocalFile.exists()&&strLocalFile.length()>=ffile.getMaxsize()){
            openFile();
        }
    }
    @OnClick(R.id.download) void startDownload(){
        Intent intent=new Intent(this,DownladService.class);
        intent.putExtra("path",path);
        intent.putExtra("ftpFileName",ftpFileName);
        startService(intent);
    }
    @OnClick(R.id.stop) void stopDownload(){
        EventBus.getDefault().post(new DownStopEvent(true,ftpFileName));
    }
    @OnClick(R.id.back) void back(){
        onBackPressed();
    }
    @OnClick(R.id.openPath) void openfile(){
        CallOtherOpeanFile otherOpeanFile = new CallOtherOpeanFile();
        otherOpeanFile.openFile(DownLoadActivity.this, strLocalFile);
    }
    ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(DownloadEvent messageEvent) {
        if (messageEvent.getFilename().equals(ftpFileName)) {
            bar.setProgress(messageEvent.getProgress());
            if (messageEvent.isDOWN_OVER()==true){
                System.out.println("===111111");
                openFile();
            }
        }
    }
    public void openFile(){
        bar.setVisibility(View.INVISIBLE);
        openPath.setVisibility(View.VISIBLE);
        download.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

