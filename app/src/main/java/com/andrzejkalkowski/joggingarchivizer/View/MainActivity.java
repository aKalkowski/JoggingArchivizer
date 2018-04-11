package com.andrzejkalkowski.joggingarchivizer.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andrzejkalkowski.joggingarchivizer.DistanceService;
import com.andrzejkalkowski.joggingarchivizer.Presenter.Timer;
import com.andrzejkalkowski.joggingarchivizer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class MainActivity extends AppCompatActivity {

    private AppCompatDelegate.NightMode nightMode;


    private double speed = 0.0d;
    private double distance = 0.0d;
    private int seconds = 0;
    private int calories = 0;

    private boolean running;
    private boolean wasRunning;
    private boolean bound = false;
    private static Boolean gpsEnabled;

    private final String PREFERENCES_NAME = "prefs";
    private final String PREFERENCES_NIGHT_MODE = "nightMode";
    private final String PREFERENCES_ACTIVITY = "activity";
    private final String PREFERENCES_GENDER = "gender";

    private static final String STATE_GPS_ENABLED = "gpsEnabled";
    private String activity;

    private Timer timer = new Timer(seconds, running);
    private DistanceService distanceService;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;
    private DrawerLayout drawerLayout;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DistanceService.DistanceBinder binder = (DistanceService.DistanceBinder) service;
            distanceService = binder.getDistanceBinder();
            if (!distanceService.getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)
                    && gpsEnabled == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.gps_not_enabled)
                        .setCancelable(false)
                        .setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                gpsEnabled = true;
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.dont_enable, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gpsEnabled = false;
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

    @BindView(R.id.button_toggle_start)
    public Button buttonToggleStart;

    @BindView(R.id.activity_view)
    public TextView activityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);

        runTimer();
        watchDistanceAndSpeed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_reminder:
                Intent intent1 = new Intent(this, ReminderActivity.class);
                startActivity(intent1);
                return true;
            case R.id.action_database:
                Intent intent2 = new Intent(this, DatabaseActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, DistanceService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        activity = sharedPreferences.getString(PREFERENCES_ACTIVITY,
                getResources().getString(R.string.default_activity));
        activityView.setText(activity);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @OnClick(R.id.button_toggle_start)
    public void onClickToggleStart(View view) {
        if (running) {
            running = false;
            buttonToggleStart.setText(R.string.start_button);
        } else {
            running = true;
            buttonToggleStart.setText(R.string.start_button_toggled);
        }
        timer.setRunning(running);
    }

    @OnLongClick(R.id.button_toggle_start)
    public boolean onLongClickButtonStart(View view) {
        if (running) {
            running = false;
            buttonToggleStart.setText(R.string.start_button);
        }
        seconds = 0;
        distance = 0.0d;
        calories = 0;
        timer.setRunning(running);
        timer.setSeconds(seconds);
        return true;
    }

    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                timer.runTimer();
                timerView.setText(timer.getTime());
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
                if (distanceService != null) {
                    if (running) {
                        distance = distanceService.getDistanceInMeters();
                        int secs = timer.getSeconds();
                        speed = (distance * 1000) / (secs * 3600);
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

    private void restoreData() {

    }

    //TODO: implement callories calculator
}
