package com.andrzejkalkowski.joggingarchivizer;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private int seconds = 0;
    private double speed = 0.0d;
    private double distance = 0.0d;
    private int calories = 0;
    private boolean running;
    private boolean wasRunning;
    private boolean bound = false;
    private DistanceService distanceService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DistanceService.DistanceBinder binder = (DistanceService.DistanceBinder) service;
            distanceService = binder.getDistanceBinder();
            if(!distanceService.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.gps_not_enabled)
                        .setCancelable(false)
                        .setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.dont_enable, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @BindView(R.id.timer)
    public TextView timerView;

    @BindView(R.id.distance_meter)
    public TextView distanceView;

    @BindView(R.id.average_speed)
    public TextView speedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        runTimer();
        watchDistanceAndSpeed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, DistanceService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bound) {
            unbindService(connection);
            bound = false;
        }
    }

    private void onClickStart() {
        runTimer();
        watchDistanceAndSpeed();
    }

    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                timerView.setText(
                        String.format("%02d:%02d:%02d", hours, minutes, secs));
                if(running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void watchDistanceAndSpeed() {
        final Handler handler = new Handler();
        final String kilometers = getResources().getString(R.string.kilometers);
        final String speedUnit = getResources().getString(R.string.speed_unit);
        handler.post(new Runnable() {
            @Override
            public void run() {
                distance = 0.0d;
                if(distanceService != null) {
                    distance = distanceService.getDistanceInMeters();
                    if(running) {
                        speed = distance / (seconds * 3600);
                    }
                    distanceView.setText(
                            String.format("%1$.2f", distance) + " " + kilometers);
                    speedView.setText(
                            String.format("%1$.2f", speed) + " " + speedUnit);
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void onClickStart(View view) {
        running = true;
    }

    public void onClickReset(View view) {
        running = false;
        seconds = 0;
        distance = 0;
        calories = 0;
    }

    public int getSeconds() {
        return seconds;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDistance() {
        return distance;
    }
}
