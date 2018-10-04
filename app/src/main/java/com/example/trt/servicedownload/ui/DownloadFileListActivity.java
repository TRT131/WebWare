package com.example.trt.servicedownload.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.doris.sample.greendao.DownloadFileDao;
import com.example.trt.servicedownload.BaseActivity;
import com.example.trt.servicedownload.MyApplication;
import com.example.trt.servicedownload.R;
import com.example.trt.servicedownload.adapter.DownAdapter;
import com.example.trt.servicedownload.common.Const;
import com.example.trt.servicedownload.module.entity.DownloadFile;
import com.example.trt.servicedownload.util.CallOtherOpeanFile;
import com.example.trt.servicedownload.util.MyDividerItemDecoration;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Trt on 2018/6/26.
 */

public class DownloadFileListActivity extends BaseActivity{
    @BindView(R.id.downRefresh) SwipeRefreshLayout downRefresh;
    @BindView(R.id.downRecycler) RecyclerView downRecycler;

    private List<DownloadFile> fileList=null;
    private DownAdapter downAdapter;
    private String path= Const.downLoadPath;
    private File localFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        request();
        setLayoutRes(R.layout.activity_downlist);
        super.onCreate(savedInstanceState);
    }

    public void bindView(){
        System.out.println("==="+fileList);
        downRecycler.setLayoutManager(new LinearLayoutManager(this));
        System.out.println("==="+fileList);
        downRecycler.addItemDecoration(new MyDividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        downRecycler.setItemAnimator(new DefaultItemAnimator());
        downAdapter=new DownAdapter(fileList);
        downAdapter.setOnItemClickListener(new DownAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CallOtherOpeanFile otherOpeanFile = new CallOtherOpeanFile();
                otherOpeanFile.openFile(DownloadFileListActivity.this, new File(path+"/"+fileList.get(position).getFilename()));
            }

            @Override
            public void onItemLongClick(View view, int position) {
                showPopMenu(view,position);
            }
        });
        downRecycler.setAdapter(downAdapter);
        downRefresh.setProgressViewOffset(true,30,60);
        downRefresh.setSize(SwipeRefreshLayout.LARGE);
        downRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        downRefresh.setEnabled(true);
        downRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        downRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request();
                downAdapter.updateData(fileList);
                downRefresh.setRefreshing(false);
            }
        });
    }
    public void request(){
        DownloadFileDao downloadFileDao=((MyApplication)getApplication()).getDaoSession().getDownloadFileDao();
        fileList=downloadFileDao.loadAll();
    }
    public void delete(int position){
        DownloadFileDao downloadFileDao=((MyApplication)getApplication()).getDaoSession().getDownloadFileDao();
        downloadFileDao.delete(fileList.get(position));
    }
    public void showPopMenu(View view, final int pos){
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_item,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String filename=fileList.get(pos).getFilename();
                localFile=new File(path+"/"+filename);
                if (localFile.exists()){
                    localFile.delete();
                    delete(pos);
                    request();
                    downAdapter.updateData(fileList);
                }
                return false;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }
}
