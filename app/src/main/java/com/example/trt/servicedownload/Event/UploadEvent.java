package com.example.trt.servicedownload.Event;

/**
 * Created by Trt on 2018/6/22.
 */

public class UploadEvent {
    private int progress;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private String filename;

    public UploadEvent(int progress,String filename) {
        this.progress = progress;
        this.filename=filename;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
