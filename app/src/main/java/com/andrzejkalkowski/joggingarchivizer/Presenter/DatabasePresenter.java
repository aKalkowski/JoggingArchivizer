package com.andrzejkalkowski.joggingarchivizer.Presenter;

/**
 * Created by komputerek on 12.04.18.
 */

public class DatabasePresenter {

    private long currentTime;
    private String activity;
    private double averageSpeed;
    private double distance;
    private long time;

    public DatabasePresenter() {
        this.currentTime = 0;
        this.activity = "";
        this.averageSpeed = 0;
        this.distance = 0;
        this.time = 0;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
