package com.example.trt.servicedownload.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.doris.sample.greendao.DownloadFileDao;
import com.example.trt.servicedownload.Event.DownStopEvent;
import com.example.trt.servicedownload.Event.DownloadEvent;
import com.example.trt.servicedownload.Event.UpStopEvent;
import com.example.trt.servicedownload.MyApplication;
import com.example.trt.servicedownload.R;
import com.example.trt.servicedownload.common.Const;
import com.example.trt.servicedownload.module.entity.DownloadFile;
import com.example.trt.servicedownload.util.FormetFileSize;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DownladService extends Service {
    private FTPClient ftpClient;
    private String code="iso-8859-1";
    private MyBinder myBinder=new MyBinder();
    Map<String,DownloadThread> control=new HashMap<>();
    NotificationManager manager;
    NotificationCompat.Builder builder;
    public class MyBinder extends Binder{
        public DownladService getService(){
            return DownladService.this;
        }
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==0x234) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                int progress = bundle.getInt("progress");
                if (progress % 20 == 0) {
                    builder.setProgress(100, progress, false);
                    if (progress == 100) {
                        builder.setContentText("下载完成");
                    }
                    manager.notify(0x234, builder.build());
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         String path=intent.getStringExtra("path");
         String ftpFileName=intent.getStringExtra("ftpFileName");
         DownloadThread downloadThread=new DownloadThread(ftpFileName,path);
         downloadThread.start();
        control.put(ftpFileName,downloadThread);
        showNotifi(ftpFileName);
        return super.START_STICKY;
    }

    public DownladService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return myBinder;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onShowMessageEvent(DownStopEvent messageEvent) {
        DownloadThread downloadThread=control.get(messageEvent.getFilename());
        if (downloadThread!=null){
            if (messageEvent.isBreak()==true){
                downloadThread.isBreak=true;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    class DownloadThread extends Thread{
        private String ftpFileName = null;
        private String path=null;
         private String ftpHost= Const.ftpHost;
         private int ftpPort=Const.ftpPort;
         private String ftpUser=Const.ftpUser;
         private String ftpPwd=Const.ftpPwd;
        private int progress;
        public boolean isBreak=false;
         public DownloadThread(String ftpFileName,String path){
             this.ftpFileName=ftpFileName;
             this.path=path;
         }
        @Override
        public void run() {
            ftpClient=new FTPClient();
            try {
                ftpClient.connect(ftpHost,ftpPort);
                ftpClient.setControlEncoding(code);
            } catch (IOException e) {
                System.out.println("===Open Failed"+e);
            }
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                System.out.println("===11111111111");
                return;
            }
            try {
                if (ftpClient.login(ftpUser, ftpPwd)) {
                    // 设置被动模式
                    ftpClient.enterLocalPassiveMode();
                    // 设置以二进制方式传输
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    // 检查远程文件是否存在
                    System.out.println("===1188");
                    FTPFile[] files = ftpClient.listFiles(new String(
                            ftpFileName.getBytes("GBK"), code));
                    System.out.println("==="+ftpFileName);
                    int per = (int) (files[0].getSize() / 100);
                    FileOutputStream output = null;
                    InputStream input = null;
                    long localSize = 0L;
                    System.out.println("===8888888888");
                    if (files.length == 0) {// 文件不存在，下载失败
                        //                            mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                        System.out.println("===DOWNLOAD_FAILED");
                        return;
                    }
                    else {
                        // 开始下载
                        File f=new File(path);
                        if (!f.exists()){
                            f.mkdirs();
                        }
                        File file = new File(path+"/"+ftpFileName);
                        if (file.exists()) {
                            // 存在，开始续传
                            localSize = file.length();
                            if (localSize > files[0].getSize()) {// 下载完成
                                //                                    mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                                System.out.println("===DOWNLOAD_SUCCESS");
                                return;
                            }
                            // 从本地文件上续传
                            try {
                                output = new FileOutputStream(file, true);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            // 将文件指向已下载的位置
                            ftpClient.setRestartOffset(localSize);
                            input = ftpClient.retrieveFileStream(new String(
                                    ftpFileName.getBytes("GBK"),code));
                            progress = (int) (localSize / per);
                            //                                mHandler.sendEmptyMessage(DOWNLOAD_UPDATE);
                            System.out.println("===DOWNLOAD_UPDATE");

                        }
                        else {// 直接下载
                            System.out.println("===download  "+per);
                            try {
                                output = new FileOutputStream(file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            input = ftpClient.retrieveFileStream(new String(
                                    ftpFileName.getBytes("GBK"),code));
                            //                                mHandler.sendEmptyMessage(DOWNLOAD_START);
                            System.out.println("===DOWNLOAD_START");
                        }
                        if (input == null) {
                            //                                mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                            System.out.println("===222222");
                            System.out.println("===DOWNLOAD_FAILED");
                            return;
                        }
                        byte[] bytes = new byte[1024];
                        int c;
                        while ((c = input.read(bytes)) != -1) {
                            if (isBreak) {
                                Log.i("xxx", "已停止下载！");
                                //                                    mHandler.sendEmptyMessage(DOWNLOAD_STOP);
                                System.out.println("===DOWNLOAD_STOP");
                                break;
                            }
                            //                                if (isBreak) {
                            //                                    Log.i("xxx", "已停止下载！");
                            ////                                    mHandler.sendEmptyMessage(DOWNLOAD_STOP);
                            //                                    System.out.println("===DOWNLOAD_STOP");
                            //                                    break;
                            //                                }
                            output.write(bytes, 0, c);
                            localSize += c;
                            long nowProcess = localSize / per;

                            if (nowProcess > progress) {
                                progress = (int) nowProcess;
                                if (progress % 1 == 0) {
                                    Log.i("xxx", "下载进度：" + progress);
                                    DownloadEvent event=new DownloadEvent(progress,ftpFileName,false);
                                    EventBus.getDefault().post(event);
                                    Bundle bundle=new Bundle();
                                    bundle.putInt("progress",progress);
                                    Message msg=new Message();
                                    msg.what=0x234;
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }
                                //                                    mHandler.sendEmptyMessage(DOWNLOAD_UPDATE);
                                System.out.println("===DOWNLOAD_UPDATE");
                            }
                        }
                        input.close();
                        output.close();
                        if (c <= 0) {// 下载完成
                            //                                mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                            DownloadEvent event=new DownloadEvent(100,ftpFileName,true);
                            EventBus.getDefault().post(event);
                            DownloadFile downloadFile=new DownloadFile(ftpFileName, FormetFileSize.convertFileSize(files[0].getSize()));
                            MyApplication myApplication=(MyApplication)getApplication();
                            DownloadFileDao downloadFileDao=myApplication.getDaoSession().getDownloadFileDao();
                            if (downloadFileDao.queryRaw("where filename=?",ftpFileName).size()<=0)
                            {
                                downloadFileDao.insert(downloadFile);
                            }
                            System.out.println("===DOWNLOAD_SUCCESS");
                        } else {
                            //                                mHandler.sendEmptyMessage(DOWNLOAD_STOP);
                            System.out.println("===DOWNLOAD_STOP");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void showNotifi(String ftpFileName){
        builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.file);
        builder.setContentTitle(ftpFileName);
        builder.setContentText("正在下载");
        manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setProgress(100,0,false);
        manager.notify(0x234,builder.build());
    }

}
