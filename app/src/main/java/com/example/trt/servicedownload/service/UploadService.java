package com.example.trt.servicedownload.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
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

import com.example.trt.servicedownload.Event.DownloadEvent;
import com.example.trt.servicedownload.Event.RefreshEvent;
import com.example.trt.servicedownload.Event.UpStopEvent;
import com.example.trt.servicedownload.Event.UploadEvent;
import com.example.trt.servicedownload.R;
import com.example.trt.servicedownload.common.Const;
import com.example.trt.servicedownload.module.Ffile;
import com.example.trt.servicedownload.ui.UploadActivity;
import com.example.trt.servicedownload.util.OkHttpClientInstance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Trt on 2018/6/22.
 */

public class UploadService extends Service{
    private static String IP= Const.IP;
    private static int HTTP_PORT=Const.HTTP_PORT;
    private FTPClient ftpClient;
    private String code="iso-8859-1";
    private UploadService.MyBinder myBinder=new UploadService.MyBinder();
    private OkHttpClient client;
    Map<String,UploadThread> control=new HashMap<>();
    NotificationManager manager;
    NotificationCompat.Builder builder;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                int progress = bundle.getInt("progress");
                if (progress % 20 == 0) {
                    builder.setProgress(100, progress, false);
                    if (progress == 100) {
                        builder.setContentText("上传完成");
                    }
                    manager.notify(0x123, builder.build());
                }
            }
        }
    };
    public class MyBinder extends Binder {
        public UploadService getService(){
            return UploadService.this;
        }
    }
    public UploadService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("===1111");
        // TODO: Return the communication channel to the service.
        String ftpFileName=intent.getStringExtra("ftpFileName");
        String strLocalFile=intent.getStringExtra("strLocalFile");
        UploadThread uploadThread=new UploadThread(ftpFileName,strLocalFile);
        uploadThread.start();
        control.put(ftpFileName,uploadThread);
        showNotifi(ftpFileName);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

        @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onShowMessageEvent(UpStopEvent messageEvent) throws InterruptedException {
        UploadThread uploadThread=control.get(messageEvent.getFilename());
        if (uploadThread!=null){
            if (messageEvent.isBreak()==true){
                uploadThread.isBreak=true;
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("===destroy");
        EventBus.getDefault().unregister(this);
    }
    class UploadThread extends Thread{
        private String ftpFileName = null;
        private String strLocalFile=null;
        private String ftpHost= Const.ftpHost;
        private int ftpPort=Const.ftpPort;
        private String ftpUser=Const.ftpUser;
        private String ftpPwd=Const.ftpPwd;
        private int progress;
        public boolean isBreak=false;
        public UploadThread(String ftpFileName,String strLocalFile){
            this.ftpFileName=ftpFileName;
            this.strLocalFile=strLocalFile;
        }
        @Override
        public void run() {
            try {
                try {
                    ftpClient=new FTPClient();
                    System.out.println("===Open");
                    ftpClient.connect(ftpHost,ftpPort);// 连接FTP服务器
                    System.out.println("===Ope11n");
                    ftpClient.setControlEncoding(code);
                } catch (Exception e) {
                    System.out.println("===Open Failed"+e);
                    return;
                }
                if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                    System.out.println("===11111111111");
                    return;
                }
                if (ftpClient.login(ftpUser, ftpPwd)) {
                    // 设置被动模式
                    ftpClient.enterLocalPassiveMode();
                    // 设置以二进制方式传输
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    // 检查远程文件是否存在
                    FTPFile[] files = ftpClient.listFiles(new String(
                            ftpFileName.getBytes("GBK"), code));
                    System.out.println("==="+ftpFileName.getBytes("GBK"));
                    File file = new File(strLocalFile);
                    System.out.println("==="+file.length());
                    int per = (int) (file.length() / 100);
                    OutputStream output = null;
                    RandomAccessFile raf ;
                    long FTPSize = 0L;
                    if (!file.exists()) {// 文件不存在，下载失败
                        System.out.println("===11111111"+strLocalFile);
                        System.out.println("===UPLOAD_FAILED");
                        return;
                    }
                    else {
                        // 开始上传
                        FileInputStream fis = new FileInputStream(file);
                        if (files.length > 0) {
                            // 存在，开始续传
                            FTPSize = files[0].getSize();
                            if (FTPSize > file.length()) {
                                // 上传完成
                                System.out.println("===UPLOAD_SUCCESS");
                                return;
                            }
                            // 续传
                            // 将文件指向已下载的位置
                            ftpClient.setRestartOffset(FTPSize);
                            raf = new RandomAccessFile(strLocalFile, "r");
                            raf.seek(FTPSize);
                            output = ftpClient.appendFileStream(new String(
                                    ftpFileName.getBytes("GBK"), code));
                            progress = (int) (FTPSize / per);
                            System.out.println("===UPLOAD_UPDATE");

                        } else {
                            // 直接下载
                            output = ftpClient.appendFileStream(new String(
                                    ftpFileName.getBytes("GBK"), code));
                            raf = new RandomAccessFile(strLocalFile, "r");
                            raf.seek(0L);
                            System.out.println("===UPLOAD_START");
                        }
                        if (output == null || raf == null) {
                            System.out.println("===222222");
                            System.out.println("===UPLOAD_FAILED");
                            return;
                        }
                        byte[] bytes = new byte[1024];
                        int c;
                        while ((c = raf.read(bytes)) != -1) {
                            if (isBreak) {
                                Log.i("xxx", "已停止上传！");
                                System.out.println("===DOWNLOAD_STOP");
                                break;
                            }
                            output.write(bytes, 0, c);
                            FTPSize += c;
                            long nowProcess = FTPSize / per;

                            if (nowProcess > progress) {
                                progress = (int) nowProcess;
                                if (progress % 1 == 0) {
                                    Log.i("xxx", "上传进度：" + progress);
                                    UploadEvent event = new UploadEvent(progress,ftpFileName);
                                    EventBus.getDefault().post(event);
                                    Bundle bundle=new Bundle();
                                    bundle.putInt("progress",progress);
                                    Message msg=new Message();
                                    msg.what=0x123;
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }
                                System.out.println("===UPLOAD_UPDATE");
                            }
                        }
                        raf.close();
                        output.close();
                        if (c <= 0) {// 下载完成
                            client= OkHttpClientInstance.getInstance();
                            try {
                                String ffileJson=new Gson().toJson(new Ffile(ftpFileName,file.length()));
                                System.out.println("==="+file.length());
                                URL url = new URL("http://"+IP+":"+HTTP_PORT+"/WebWare/contacts/upload.html");
                                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8")
                                        , ffileJson);
                                Request request=new Request.Builder().post(body).url(url).build();
                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Log.d("kkk", "数据请求失败");
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                    }
                                });
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            UploadEvent event = new UploadEvent(100,ftpFileName);
                            EventBus.getDefault().post(event);
                            System.out.println("===UPLOAD_SUCCESS");
                        } else {
                            System.out.println("===UPLOAD_STOP");
                        }
                    }
                }

            } catch (Exception e) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    public void showNotifi(String ftpFileName){
        builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.file);
        builder.setContentTitle(ftpFileName);
        builder.setContentText("正在上传");
        manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setProgress(100,0,false);
        manager.notify(0x123,builder.build());
    }

}
