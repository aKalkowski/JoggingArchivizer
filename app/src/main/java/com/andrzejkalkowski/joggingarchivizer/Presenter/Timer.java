package com.andrzejkalkowski.joggingarchivizer.Presenter;

/**
 * Created by komputerek on 11.04.18.
 */

public class Timer {
    private int seconds = 0;
    boolean running = false;
    private String time = "00:00:00";

    public Timer(int seconds, boolean running) {
        this.seconds = seconds;
        this.running = running;
    }

    public void runTimer() {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        time = String.format("%02d:%02d:%02d", hours, minutes, secs);
        if (running) {
            seconds++;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public String getTime() {
        return time;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
