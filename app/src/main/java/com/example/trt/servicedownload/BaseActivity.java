package com.example.trt.servicedownload;

import android.app.Activity;
import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

public abstract class BaseActivity extends Activity {
    protected  int layoutRes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutRes);
        ButterKnife.bind(this);
        bindView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    protected abstract void bindView();
    protected void setLayoutRes(int res){
        this.layoutRes=res;
    }
}
