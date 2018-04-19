package com.andrzejkalkowski.joggingarchivizer.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TimePicker;

import com.andrzejkalkowski.joggingarchivizer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReminderActivity extends AppCompatActivity {

    @BindView(R.id.switch_monday)
    public Switch switchMonday;

    @BindView(R.id.switch_tuesday)
    public Switch switchTuesday;

    @BindView(R.id.switch_wednesday)
    public Switch switchWednesday;

    @BindView(R.id.switch_thursday)
    public Switch switchThursday;

    @BindView(R.id.switch_friday)
    public Switch switchFriday;

    @BindView(R.id.switch_saturday)
    public Switch switchSaturday;

    @BindView(R.id.switch_sunday)
    public Switch switchSunday;

    @BindView(R.id.time_picker)
    public TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        timePicker.setIs24HourView(true);
        timePicker.setFocusable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
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
            case R.id.action_main:
                intent = new Intent(this, MainActivity.class);
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
}
