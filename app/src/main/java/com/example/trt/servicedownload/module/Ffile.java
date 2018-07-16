package com.example.trt.servicedownload.module;

import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by Trt on 2018/6/23.
 */

public class Ffile implements Serializable{
    private @Nullable int id;
    private String filename;

    public Long getMaxsize() {
        return maxsize;
    }

    public void setMaxsize(Long maxsize) {
        this.maxsize = maxsize;
    }

    private Long maxsize;

    public Ffile(int id, String filename) {
        this.id = id;
        this.filename = filename;
    }
    public Ffile(String filename){
        this.filename=filename;
    }
    public Ffile(String filename,long maxsize) {
        this.filename = filename;
        this.maxsize=maxsize;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
