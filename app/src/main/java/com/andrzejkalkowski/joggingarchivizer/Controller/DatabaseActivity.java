package com.andrzejkalkowski.joggingarchivizer.Controller;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.andrzejkalkowski.joggingarchivizer.Model.Helpers.DatabaseHelper;
import com.andrzejkalkowski.joggingarchivizer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DatabaseActivity extends ListActivity {

    private ArrayAdapter<String> arrayAdapter;
    private SQLiteDatabase database;
    private Cursor cursor;

    private String[] activityValues;

//    @BindView(R.id.activity_list)
//    public ListView activityList;

//    @BindView(R.id.activity_databse_spinner)
//    public Spinner activitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_database);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        ListView listView = getListView();
//        activityValues = getResources().getStringArray(R.array.activity_options_database);
//        arrayAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_expandable_list_item_1, activityValues);
//        activitySpinner.setAdapter(arrayAdapter);
        try {
            SQLiteOpenHelper helper = DatabaseHelper.getInstance(this);
            database = helper.getReadableDatabase();
            cursor = database.query(DatabaseHelper.getTableName(),
                    new String[] {
                    "_id", "date", "activity"},
                    null, null, null, null, null);
            CursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                    cursor, new String[] {"date", "activity"},
                    new int[] {android.R.id.text1}, 0);
            listView.setAdapter(adapter);
        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(this, "Database unaccessible", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_ID, (int) id);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Intent intent = new Intent(this, OptionsActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.action_reminder:
//                Intent intent1 = new Intent(this, ReminderActivity.class);
//                startActivity(intent1);
//                return true;
//            case R.id.action_main:
//                Intent intent2 = new Intent(this, MainActivity.class);
//                startActivity(intent2);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


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
}
