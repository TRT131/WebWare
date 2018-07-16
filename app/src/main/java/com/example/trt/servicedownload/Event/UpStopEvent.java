package com.example.trt.servicedownload.Event;

/**
 * Created by Trt on 2018/6/22.
 */

public class UpStopEvent {
    boolean isBreak=false;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    String filename;
    public boolean isBreak() {
        return isBreak;
    }

    public void setBreak(boolean aBreak) {
        isBreak = aBreak;
    }
    public UpStopEvent(boolean isBreak,String filename) {
        this.isBreak = isBreak;
        this.filename=filename;
    }
}
