package com.andrzejkalkowski.joggingarchivizer.Controller;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.andrzejkalkowski.joggingarchivizer.Model.Helpers.DatabaseHelper;
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
        ButterKnife.bind(this);

        int trainingId = (Integer) getIntent().getExtras().get(EXTRA_TRAINING_ID);

        try {
            SQLiteOpenHelper helper = DatabaseHelper.getInstance(this);
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.query(DatabaseHelper.getTableName(), new String[] {
                    "date", "activity", "average_speed", "distance", "time"
            }, "_id = ?", new String[] {Integer.toString(trainingId)},
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
            Toast.makeText(this, "Unable to open database", Toast.LENGTH_SHORT).show();
        }
    }


    //TODO: implement training charts? (time vs. speed)
}
