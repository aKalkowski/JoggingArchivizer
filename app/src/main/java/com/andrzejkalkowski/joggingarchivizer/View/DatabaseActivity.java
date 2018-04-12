package com.andrzejkalkowski.joggingarchivizer.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.andrzejkalkowski.joggingarchivizer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DatabaseActivity extends AppCompatActivity {

    @BindView(R.id.activity_list)
    public ListView activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
    }
}
