package com.example.trt.servicedownload.Event;

import com.example.trt.servicedownload.module.Ffile;

import java.util.List;

/**
 * Created by Trt on 2018/6/23.
 */

public class RefreshEvent {
    public RefreshEvent(List<Ffile> ffiles) {
        this.ffiles = ffiles;
    }

    public List<Ffile> getFfiles() {
        return ffiles;
    }

    public void setFfiles(List<Ffile> ffiles) {
        this.ffiles = ffiles;
    }

    private List<Ffile> ffiles;
}
