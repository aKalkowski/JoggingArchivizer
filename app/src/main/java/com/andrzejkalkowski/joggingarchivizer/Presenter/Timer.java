package com.andrzejkalkowski.joggingarchivizer.Presenter;

import android.util.Log;

/**
 * Created by komputerek on 11.04.18.
 */

public class Timer {
    public static int seconds = 0;
    private boolean running;
    private String time = "00:00:00";
    private static final String TAG = "Timer";

    public Timer() {
        this.running = false;
    }

    public void runTimer() {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        time = String.format("%02d:%02d:%02d", hours, minutes, secs);
        if (running) {
            seconds++;
        }
        Log.d(TAG, "runTimer: " + seconds);
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
