package com.example.trt.servicedownload.Event;

/**
 * Created by Trt on 2018/6/22.
 */

public class DownloadEvent {
    private int progress;

    public boolean isDOWN_OVER() {
        return DOWN_OVER;
    }

    public void setDOWN_OVER(boolean DOWN_OVER) {
        this.DOWN_OVER = DOWN_OVER;
    }

    private boolean DOWN_OVER;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private String filename;

    public DownloadEvent(int progress,String filename,boolean DOWN_OVER) {
        this.progress = progress;
        this.filename=filename;
        this.DOWN_OVER=DOWN_OVER;
    }
    public DownloadEvent(boolean DOWN_OVER) {
        this.DOWN_OVER = DOWN_OVER;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
