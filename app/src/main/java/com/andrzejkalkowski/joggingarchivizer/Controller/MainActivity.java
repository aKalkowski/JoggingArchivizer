package com.andrzejkalkowski.joggingarchivizer.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.andrzejkalkowski.joggingarchivizer.Model.Helpers.DatabaseHelper;
import com.andrzejkalkowski.joggingarchivizer.Model.Services.DistanceService;
import com.andrzejkalkowski.joggingarchivizer.Model.Timer;
import com.andrzejkalkowski.joggingarchivizer.Model.Training;
import com.andrzejkalkowski.joggingarchivizer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private double speed = 0.0d;
    private double distance = 0.0d;
    private double averageSpeed = 0.0d;
    private int calories = 100;

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
    private Training training;

    private DistanceService distanceService;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper helper;
    private SQLiteDatabase database;
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
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_reminder:
                intent = new Intent(this, ReminderActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_database:
                intent = new Intent(this, DatabaseActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_add_to_database:
                training = new Training.TrainingBuilder(activityView.getText().toString())
                        .burntCalories(calories)
                        .distance(distance)
                        .seconds(timer.getSeconds())
                        .build();
                new DatabaseTask().execute(training);
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        activity = sharedPreferences.getString(PREFERENCES_ACTIVITY,
                getResources().getString(R.string.default_activity));
        activityView.setText(activity);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, DistanceService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
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
        speed = 0.0d;
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
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void restoreData() {
        nightMode = sharedPreferences.getBoolean(PREFERENCES_NIGHT_MODE, false);
    }

    private class DatabaseTask extends AsyncTask<Training, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Training... trainings) {
            try {
                helper = DatabaseHelper.getInstance(MainActivity.this);
                database = helper.getWritableDatabase();
                helper.addActivity(helper.getWritableDatabase(), trainings[0]);
                helper.close();
                return true;
            } catch (SQLiteException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(!success) {
                Toast.makeText(MainActivity.this, "Database unavailable",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //TODO: implement calories calculator
}
