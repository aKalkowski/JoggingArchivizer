package com.andrzejkalkowski.joggingarchivizer.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.andrzejkalkowski.joggingarchivizer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
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

    private double weight;
    private boolean nightModeEnabled;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        activityValues = getResources().getStringArray(R.array.activity_options);
        genderValues = getResources().getStringArray(R.array.gender_options);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activityValues);
        activityOptions.setAdapter(arrayAdapter);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, genderValues);
        genderOptions.setAdapter(arrayAdapter);
        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setDefaultNightMode();
                    Toast.makeText(OptionsActivity.this,
                            R.string.night_mode_toast, Toast.LENGTH_SHORT).show();
                    nightModeEnabled = true;
                } else if (!isChecked) {
                    setDefaultNightMode();
                    Toast.makeText(OptionsActivity.this,
                            R.string.night_mode_toast_disabled, Toast.LENGTH_SHORT).show();
                    nightModeEnabled = false;
                }
                saveData();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean created = super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(1).setChecked(true);
        return created;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main:
                Intent intent = new Intent(this, MainActivity.class);
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

    public void setDefaultNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public void setDefaultDayMode(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }


    @OnItemSelected(R.id.gender_spinner)
    public void genderSpinnerItemSelected(Spinner spinner, int position) {

    }

    public void saveOptions(View view) {
        saveData();
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREFERENCES_NIGHT_MODE, nightModeEnabled).apply();
        editor.putLong(PREFERENCES_WEIGHT, Double.doubleToLongBits(
                Double.valueOf(weightEdit.getText().toString())));
        editor.putString(PREFERENCES_ACTIVITY, activityOptions.getSelectedItem().toString());
        editor.putString(PREFERENCES_GENDER, genderOptions.getSelectedItem().toString());
        editor.putInt(PREFERENCES_ACTIVITY_INDEX, activityOptions.getSelectedItemPosition());
        editor.putInt(PREFERENCES_GENDER_INDEX, genderOptions.getSelectedItemPosition());
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
        Log.d(TAG, "restoreData: Data restored");
    }
}

