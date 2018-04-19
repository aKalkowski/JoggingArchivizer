package com.andrzejkalkowski.joggingarchivizer.Controller;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.andrzejkalkowski.joggingarchivizer.Model.Helpers.DatabaseHelper;
import com.andrzejkalkowski.joggingarchivizer.Model.Training;
import com.andrzejkalkowski.joggingarchivizer.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class DatabaseActivity extends AppCompatActivity {

    private ArrayAdapter<String> arrayAdapter;
    private SQLiteDatabase database;
    private Cursor cursor;
    private CursorAdapter adapter;
    private DatabaseMainTask databaseMainTask = new DatabaseMainTask();

    private String[] activityValues;
    private String selection;
    private List<Training> trainings;

    @BindView(R.id.database_list_view)
    public ListView activityList;

    @BindView(R.id.activity_databse_spinner)
    public Spinner activitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        activityValues = getResources().getStringArray(R.array.activity_options_database);
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, activityValues);
        activitySpinner.setAdapter(arrayAdapter);
        databaseMainTask.execute();
        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TrainingActivity.class);
                intent.putExtra(TrainingActivity.EXTRA_TRAINING_ID, (int) id);
                startActivity(intent);
            }
        });
    }

    @OnItemSelected(R.id.activity_databse_spinner)
    public void spinnerItemSelected(Spinner spinner, int position) {
        switch (position) {
            case 0:
                getSelectedActivitiesFromDatabase("all");
                break;
            default:
                selection = spinner.getSelectedItem().toString();
                getSelectedActivitiesFromDatabase(selection);
                break;
        }
        activityList.setAdapter(adapter);
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
            case R.id.action_reminder:
                intent = new Intent(this, ReminderActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_main:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            database.close();
        }
    }

    private void getSelectedActivitiesFromDatabase(String selection) {
        if (selection.equals("all")) {
            cursor = database.query(DatabaseHelper.getTableName(),
                    new String[]{"_id", "date", "activity"},
                    null,
                    null,
                    null,
                    null,
                    null);
            adapter = new SimpleCursorAdapter(this,
                    R.layout.database_list_item,
                    cursor,
                    new String[]{"date", "activity"},
                    new int[]{R.id.item_date_view, R.id.item_training_view},
                    0);
        } else {
            cursor = database.query(DatabaseHelper.getTableName(),
                    new String[]{"_id", "date", "activity"},
                    "activity = ?",
                    new String[]{selection},
                    null,
                    null,
                    null);
            adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.database_list_item,
                    cursor,
                    new String[]{"date", "activity"},
                    new int[]{R.id.item_date_view, R.id.item_training_view},
                    0);
        }

    }


    private class DatabaseMainTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... selections) {
            try {
                SQLiteOpenHelper helper = DatabaseHelper.getInstance(DatabaseActivity.this);
                database = helper.getReadableDatabase();
                if (selections.length == 0) {
                    cursor = database.query(DatabaseHelper.getTableName(),
                            new String[]{"_id", "date", "activity"},
                            null,
                            null,
                            null,
                            null,
                            null);
                }
                CursorAdapter adapter = new SimpleCursorAdapter(
                        DatabaseActivity.this,
                        R.layout.database_list_item,
                        cursor,
                        new String[]{"date", "activity"},
                        new int[]{R.id.item_date_view, R.id.item_training_view},
                        0);
                activityList.setAdapter(adapter);
            } catch (SQLiteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(DatabaseActivity.this, "Database unavailable",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}