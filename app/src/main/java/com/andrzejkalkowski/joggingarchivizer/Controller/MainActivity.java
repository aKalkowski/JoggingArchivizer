package com.andrzejkalkowski.joggingarchivizer.Controller;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andrzejkalkowski.joggingarchivizer.Model.Services.DistanceService;
import com.andrzejkalkowski.joggingarchivizer.Model.Timer;
import com.andrzejkalkowski.joggingarchivizer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static com.andrzejkalkowski.joggingarchivizer.Model.NightMode.setDefaultNightMode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private double speed = 0.0d;
    private double distance = 0.0d;
    private double averageSpeed = 0.0d;
    private int calories = 0;

    private boolean running = false;
    private boolean wasRunning;
    private boolean bound = false;
    private boolean nightMode;
    private static Boolean gpsEnabled;

    private final String PREFERENCES_NAME = "prefs";
    private final String PREFERENCES_NIGHT_MODE = "nightMode";
    private final String PREFERENCES_ACTIVITY = "activity";
    private final String PREFERENCES_GENDER = "gender";

    private static final String STATE_GPS_ENABLED = "gpsEnabled";
    private String activity;

    private Timer timer = new Timer();
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
        restoreData();
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        runClocks();
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
            averageSpeed = speed;
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
        distance = 0.0d;
        calories = 0;
        timer.setRunning(running);
        timer.setSeconds(0);
        distanceService.setDistance(distance);
        return true;
    }

    private void runClocks() {
        final Handler handler = new Handler();
        final String kilometers = getResources().getString(R.string.kilometers);
        final String speedUnit = getResources().getString(R.string.speed_unit);
        handler.post(new Runnable() {
            @Override
            public void run() {
                timer.runTimer();
                timerView.setText(timer.getTime());
                Log.d(TAG, "run: timer: " + timer.getSeconds());
                handler.postDelayed(this, 1000);
                if (distanceService != null) {
                    if (running) {
                        distance = distanceService.getDistanceInMeters();
                        speed = distance  / timer.getSeconds() * 3600;
                        Log.d(TAG, "run: distance: " + distance);
                        Log.d(TAG, "run: speed: " + speed);
                    }
                    distanceView.setText(
                            String.format("%1$.2f", distance) + " " + kilometers);
                    speedView.setText(
                            String.format("%1$.2f", speed) + " " + speedUnit);
                }
            }
        });
    }

    private void restoreData() {
        nightMode = sharedPreferences.getBoolean(PREFERENCES_NIGHT_MODE, false);
    }

    //TODO: implement calories calculator
}
