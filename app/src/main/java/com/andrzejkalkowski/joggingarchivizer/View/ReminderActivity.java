package com.andrzejkalkowski.joggingarchivizer.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    }
}
