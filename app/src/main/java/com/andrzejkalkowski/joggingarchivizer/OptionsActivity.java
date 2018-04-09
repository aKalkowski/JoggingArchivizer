package com.andrzejkalkowski.joggingarchivizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionsActivity extends AppCompatActivity {

    @BindView(R.id.night_mode_switch)
    public Switch nightModeSwitch;

    @BindView(R.id.activity_options)
    public Spinner activityOptions;

    @BindView(R.id.edit_weight)
    public EditText weightEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        ButterKnife.bind(this);
    }
}
