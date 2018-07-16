package com.example.trt.servicedownload;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.doris.sample.greendao.DaoMaster;
import com.doris.sample.greendao.DaoSession;
import com.example.trt.servicedownload.Event.RefreshEvent;
import com.example.trt.servicedownload.module.Ffile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by Trt on 2018/6/26.
 */

public class MyApplication extends Application {
    private DaoSession daoSession;

    public List<Ffile> getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(List<Ffile> uploadFile) {
        this.uploadFile = uploadFile;
    }

    public List<Ffile> uploadFile=null;
    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
    public DaoSession getDaoSession(){
        return daoSession;
    }

}
