package com.andrzejkalkowski.joggingarchivizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionsActivity extends AppCompatActivity {

    public static final String PREFERENCES_NAME = "prefs";
    public static final String PREFERENCES_NIGHT_MODE = "nightMode";
    public static final String PREFERENCES_WEIGHT = "weight";
    private double weight;
    private boolean nightModeEnabled;
    private ArrayAdapter<String> arrayAdapter;
    private String[] activityValues;

    private SharedPreferences sharedPreferences;

    @BindView(R.id.night_mode_switch)
    public Switch nightModeSwitch;

    @BindView(R.id.activity_options_spinner)
    public Spinner activityOptions;

    @BindView(R.id.edit_weight)
    public EditText weightEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        ButterKnife.bind(this);
        activityValues = getResources().getStringArray(R.array.activity_options);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activityValues);
        activityOptions.setAdapter(arrayAdapter);
        restoreData();
        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(OptionsActivity.this,
                            R.string.night_mode_toast, Toast.LENGTH_SHORT).show();
                    nightModeEnabled = true;
                } else if(!isChecked){
                    Toast.makeText(OptionsActivity.this,
                            R.string.night_mode_toast_disabled, Toast.LENGTH_SHORT).show();
                    nightModeEnabled = false;
                }
                saveData();
            }
        });
    }

    public void saveOptions(View view) {
        saveData();
    }

    private void saveData() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(PREFERENCES_NIGHT_MODE, nightModeEnabled).apply();
        sharedPreferences.edit().putLong(PREFERENCES_WEIGHT, Double.doubleToLongBits(weight));

    }

    private void restoreData() {
        nightModeEnabled = sharedPreferences.getBoolean(PREFERENCES_NIGHT_MODE, false);
        weight = Double.longBitsToDouble(sharedPreferences.getLong(PREFERENCES_WEIGHT, 90));
    }


}
