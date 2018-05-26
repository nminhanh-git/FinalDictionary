package com.app.mightylion.finaldictionary;

/**
 * Created by nminh on 8/18/2017.
 */

public class Info {
    private int mainInfo;
    private int subInfo;

    public Info() {
        this.mainInfo = 0;
        this.subInfo = 0;
    }

    public Info(int mainInfo, int subInfo) {
        this.mainInfo = mainInfo;
        this.subInfo = subInfo;
    }

    public int getMainInfo() {
        return mainInfo;
    }

    public void setMainInfo(int mainInfo) {
        this.mainInfo = mainInfo;
    }

    public int getSubInfo() {
        return subInfo;
    }

    public void setSubInfo(int subInfo) {
        this.subInfo = subInfo;
    }
}
