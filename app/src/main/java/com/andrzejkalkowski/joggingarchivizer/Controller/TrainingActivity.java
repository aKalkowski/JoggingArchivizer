package com.andrzejkalkowski.joggingarchivizer.Controller;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.andrzejkalkowski.joggingarchivizer.Model.Helpers.DatabaseHelper;
import com.andrzejkalkowski.joggingarchivizer.Model.Training;
import com.andrzejkalkowski.joggingarchivizer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingActivity extends AppCompatActivity {

    public static final String EXTRA_TRAINING_ID = "trainingId";

    @BindView(R.id.date_view)
    public TextView dateView;

    @BindView(R.id.training_view)
    public TextView trainingView;

    @BindView(R.id.average_speed_view)
    public TextView speedView;

    @BindView(R.id.distance_view)
    public TextView distanceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        int trainingId = (Integer) getIntent().getExtras().get(EXTRA_TRAINING_ID);
        new DatabaseTask().execute(trainingId);
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
    private class DatabaseTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... trainingId) {
            try {
                SQLiteOpenHelper helper = DatabaseHelper.getInstance(TrainingActivity.this);
                SQLiteDatabase database = helper.getReadableDatabase();
                Cursor cursor = database.query(DatabaseHelper.getTableName(), new String[] {
                                "date", "activity", "average_speed", "distance", "time"
                        }, "_id = ?", new String[] {Integer.toString(trainingId[0])},
                        null, null, null);
                if (cursor.moveToFirst()) {
                    String dateText = cursor.getString(0);
                    String trainingText = cursor.getString(1);
                    double speed = cursor.getDouble(2);
                    double distance = cursor.getDouble(3);

                    String speedText = String.format("%1$.2f", distance) + " "
                            + getResources().getString(R.string.kilometers);
                    String distanceText = String.format("%1$.2f", speed) + " "
                            + getResources().getString(R.string.speed_unit);

                    dateView.setText(dateText);
                    trainingView.setText(trainingText);
                    speedView.setText(speedText);
                    distanceView.setText(distanceText);
                }
                cursor.close();
                database.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(!success) {
                Toast.makeText(TrainingActivity.this, "Database unavailable",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //TODO: implement training charts? (time vs. speed)
}
