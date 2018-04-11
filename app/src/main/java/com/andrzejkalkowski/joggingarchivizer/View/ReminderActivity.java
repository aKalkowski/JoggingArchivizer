package com.andrzejkalkowski.joggingarchivizer.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.andrzejkalkowski.joggingarchivizer.R;

import butterknife.ButterKnife;

public class ReminderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
    }
}
