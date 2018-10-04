package com.example.trt.servicedownload.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trt.servicedownload.BaseActivity;
import com.example.trt.servicedownload.Event.DownloadEvent;
import com.example.trt.servicedownload.Event.RefreshEvent;
import com.example.trt.servicedownload.MyApplication;
import com.example.trt.servicedownload.adapter.MyAdapter;
import com.example.trt.servicedownload.adapter.MyRecyclerAdapter;
import com.example.trt.servicedownload.adapter.MyRefreshAdapter;
import com.example.trt.servicedownload.module.Ffile;
import com.example.trt.servicedownload.R;
import com.example.trt.servicedownload.common.Const;
import com.example.trt.servicedownload.util.FormetFileSize;
import com.example.trt.servicedownload.util.MyDividerItemDecoration;
import com.example.trt.servicedownload.util.OkHttpClientInstance;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.lemon.view.RefreshRecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FirstActivity extends BaseActivity {

    @BindView(R.id.myRecycler) RecyclerView myRecycler;
    @BindView(R.id.openUp) TextView openUp;
    @BindView(R.id.myRefresh) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.openDown) TextView opDown;
    @BindView(R.id.commonTab) CommonTabLayout commonTab;
    private static String IP= Const.IP;
    private static int HTTP_PORT=Const.HTTP_PORT;
    private List<Ffile> mDatas=null;
    private OkHttpClient client;
    private MyAdapter adapter;
    private MyApplication application;
    private  ArrayList<CustomTabEntity> mTabEntity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayoutRes(R.layout.activity_first);
        super.onCreate(savedInstanceState);
        request();
        EventBus.getDefault().register(this);
        application= (MyApplication) getApplication();
        application.setUploadFile(mDatas);
        permissonRequest();
    }

    public void bindView(){
        mTabEntity=new ArrayList<>();
        mTabEntity.add(new CustomTabEntity() {
            @Override
            public String getTabTitle() {
                return "云盘";
            }

            @Override
            public int getTabSelectedIcon() {
                return R.drawable.ic_ware_selected;
            }

            @Override
            public int getTabUnselectedIcon() {
                return R.drawable.ic_ware_normal;
            }
        });
        mTabEntity.add(new CustomTabEntity() {
            @Override
            public String getTabTitle() {
                return "发现";
            }

            @Override
            public int getTabSelectedIcon() {
                return R.drawable.ic_find_selected;
            }

            @Override
            public int getTabUnselectedIcon() {
                return R.drawable.ic_find_normal;
            }
        });
        mTabEntity.add(new CustomTabEntity() {
            @Override
            public String getTabTitle() {
                return "我的";
            }

            @Override
            public int getTabSelectedIcon() {
                return R.drawable.ic_set_selected;
            }

            @Override
            public int getTabUnselectedIcon() {
                return R.drawable.ic_set_normal;
            }
        });
        commonTab.setTabData(mTabEntity);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));
        myRecycler.addItemDecoration(new MyDividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        myRecycler.setItemAnimator(new DefaultItemAnimator());
        adapter=new MyAdapter(mDatas);
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(FirstActivity.this,DownLoadActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("dFile",mDatas.get(position));
                bundle.putString("ftpFileName",mDatas.get(position).getFilename());
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        myRecycler.setAdapter(adapter);
        refreshLayout.setProgressViewOffset(true,30,60);
        refreshLayout.setSize(SwipeRefreshLayout.LARGE);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setEnabled(true);
        refreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request();
                refreshLayout.setRefreshing(false);
                application.setUploadFile(mDatas);
            }
        });
    }
    public void request(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                client= OkHttpClientInstance.getInstance();
                try {
                    URL url = new URL("http://"+IP+":"+HTTP_PORT+"/WebWare/contacts/index.html");
                    Request request=new Request.Builder().get().url(url).build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("kkk", "数据请求失败");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String jsonStr=response.body().string();
                            Gson gson=new Gson();
                            Type lt=new TypeToken<List<Ffile>>(){}.getType();
                            List<Ffile> ffiles=gson.fromJson(jsonStr,lt);
                            mDatas=ffiles;
                            EventBus.getDefault().post(new RefreshEvent(ffiles));
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(RefreshEvent messageEvent) {
        adapter.updateData(messageEvent.getFfiles());
        System.out.println(getApplication());
    }

    @OnClick(R.id.openUp) void upload(){
        startActivity(new Intent(this,FilePathListActivity.class));
    }
    @OnClick(R.id.openDown) void openDown(){
        startActivity(new Intent(this,DownloadFileListActivity.class));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        client=null;
    }

    private void permissonRequest(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }
    }
}
