package com.example.trt.servicedownload.module.entity;

import android.support.annotation.Nullable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Trt on 2018/6/26.
 */
@Entity (nameInDb = "download_file",indexes = {
        @Index(value = "filename DESC", unique = true)
})
public class DownloadFile {
    @Id(autoincrement = true) private Long id;
    @Nullable private String filename ;
    @Nullable private String maxsize;
    public DownloadFile(String filename, String maxsize) {
        this.filename = filename;
        this.maxsize = maxsize;
    }
@Generated(hash = 379234666)
public DownloadFile() {
}
@Generated(hash = 662725900)
public DownloadFile(Long id, String filename, String maxsize) {
    this.id = id;
    this.filename = filename;
    this.maxsize = maxsize;
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getFilename() {
    return this.filename;
}
public void setFilename(String filename) {
    this.filename = filename;
}
public String getMaxsize() {
    return this.maxsize;
}
public void setMaxsize(String maxsize) {
    this.maxsize = maxsize;
}
}
