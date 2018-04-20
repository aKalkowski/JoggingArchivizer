package com.andrzejkalkowski.joggingarchivizer.Controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.andrzejkalkowski.joggingarchivizer.Model.Helpers.DatabaseHelper;
import com.andrzejkalkowski.joggingarchivizer.R;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class OptionsActivity extends AppCompatActivity {

    private static final String TAG = "OptionsActivity";

    private final String PREFERENCES_NAME = "prefs";
    private final String PREFERENCES_NIGHT_MODE = "nightMode";
    private final String PREFERENCES_WEIGHT = "weight";
    private final String PREFERENCES_ACTIVITY = "activity";
    private final String PREFERENCES_GENDER = "gender";
    private final String PREFERENCES_ACTIVITY_INDEX = "activityIndex";
    private final String PREFERENCES_GENDER_INDEX = "genderIndex";
    private final String PREFERENCES_WAS_NIGHT_MODE_ENABLED = "wasNightModeEnabled";

    private DatabaseHelper helper;
    private SQLiteDatabase database;

    private double weight;
    private boolean nightModeEnabled;
    private boolean wasNightModeEnabled;
    private ArrayAdapter<String> arrayAdapter;
    private String[] activityValues;
    private String[] genderValues;
    private String currentActivity;
    private String gender;
    private int spinnerActivityIndex;
    private int spinnerGenderIndex;

    private SharedPreferences sharedPreferences;

    @BindView(R.id.night_mode_switch)
    public Switch nightModeSwitch;

    @BindView(R.id.activity_options_spinner)
    public Spinner activityOptions;

    @BindView(R.id.gender_spinner)
    public Spinner genderOptions;

    @BindView(R.id.edit_weight)
    public EditText weightEdit;

    @BindView(R.id.button_delete_table)
    public Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        activityValues = getResources().getStringArray(R.array.activity_options);
        genderValues = getResources().getStringArray(R.array.gender_options);
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, activityValues);
        activityOptions.setAdapter(arrayAdapter);
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, genderValues);
        genderOptions.setAdapter(arrayAdapter);
        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean created = super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.getItem(1).setChecked(true);
        return created;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_main:
                intent = new Intent(this, MainActivity.class);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //TODO: implement reminding notifications for every day of week



    @Override
    protected void onStart() {
        super.onStart();
        restoreData();
        genderOptions.setSelection(spinnerGenderIndex);
        activityOptions.setSelection(spinnerActivityIndex);

    }

    @OnItemSelected(R.id.activity_options_spinner)
    public void activitySpinnerItemSelected(Spinner spinner, int position) {

    }

    @OnCheckedChanged(R.id.night_mode_switch)
    public void onSwitchChecked(Switch nightModeSwitch, boolean isChecked) {
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nightModeEnabled = true;
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            nightModeEnabled = false;
        }
        wasNightModeEnabled = nightModeEnabled;
        if (isChecked != wasNightModeEnabled) {
            saveData();
        }
    }

    @OnClick(R.id.button_delete_table)
    public void deleteTable(View view) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(getResources().getString(R.string.delete_warning));
        alert.setMessage(getResources().getString(R.string.dialog_delete_table));
        alert.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.dialog_positive_response),
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DatabaseTask().execute();
                    }
                });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.dialog_negative_response),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.dialog_data_not_deleted),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        alert.show();
    }



    @OnItemSelected(R.id.gender_spinner)
    public void genderSpinnerItemSelected(Spinner spinner, int position) {

    }

    public void saveOptions(View view) {
        saveData();
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREFERENCES_NIGHT_MODE, nightModeEnabled);
        editor.putLong(PREFERENCES_WEIGHT, Double.doubleToLongBits(
                Double.valueOf(weightEdit.getText().toString())));
        editor.putString(PREFERENCES_ACTIVITY, activityOptions.getSelectedItem().toString());
        editor.putString(PREFERENCES_GENDER, genderOptions.getSelectedItem().toString());
        editor.putInt(PREFERENCES_ACTIVITY_INDEX, activityOptions.getSelectedItemPosition());
        editor.putInt(PREFERENCES_GENDER_INDEX, genderOptions.getSelectedItemPosition());
        editor.putBoolean(PREFERENCES_WAS_NIGHT_MODE_ENABLED, wasNightModeEnabled);
        if(editor.commit()) {
            Log.d(TAG, "saveData: Data saved");
            Toast.makeText(this, R.string.settings_save_success, Toast.LENGTH_SHORT).show();
        }
    }

    private void restoreData() {
        nightModeEnabled = sharedPreferences.getBoolean(PREFERENCES_NIGHT_MODE, false);
        weight = Double.longBitsToDouble(sharedPreferences.getLong(PREFERENCES_WEIGHT, 0));
        weightEdit.setText(String.valueOf(weight));
        spinnerActivityIndex = sharedPreferences.getInt(PREFERENCES_ACTIVITY_INDEX, 0);
        spinnerGenderIndex = sharedPreferences.getInt(PREFERENCES_GENDER_INDEX, 0);
        wasNightModeEnabled = sharedPreferences.getBoolean(PREFERENCES_WAS_NIGHT_MODE_ENABLED, false);
        if (nightModeEnabled) {
            nightModeSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        Log.d(TAG, "restoreData: Data restored");
    }

    private class DatabaseTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                helper = DatabaseHelper.getInstance(getApplicationContext());
                database = helper.getWritableDatabase();
                helper.deleteAllRecords(database);
            } catch (SQLiteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(OptionsActivity.this,
                        "Database unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

