package com.andrzejkalkowski.joggingarchivizer.Model.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andrzejkalkowski.joggingarchivizer.Model.Timer;
import com.andrzejkalkowski.joggingarchivizer.Model.Training;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by komputerek on 11.04.18.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static DatabaseHelper instance;

    private static final String DATABASE_NAME = "activities";
    private static final String TABLE_NAME = "activity";
    private static final int DATABASE_VERSION = 1;

    private Calendar calendar = Calendar.getInstance();


    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db, oldVersion, newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("CREATE TABLE activity (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date TEXT, "
            + "activity TEXT, "
            + "average_speed DECIMAL, "
            + "distance DECIMAL, "
            + "time TEXT);");
        }

    }

    public void deleteAllRecords(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public void addActivity(SQLiteDatabase db, Training training) {
        ContentValues values = new ContentValues();
        values.put("date", DateFormat.getDateInstance().format(calendar.getTime()));
        values.put("activity",training.getActivityType() );
        values.put("average_speed", training.getAverageSpeed());
        values.put("distance", training.getDistance());
        values.put("time", Timer.seconds);
        db.insert(TABLE_NAME, null, values);
    }

    public void selectFromDatabase(SQLiteDatabase db, String activityName) {

    }

    public void selectAllActivities(SQLiteDatabase db) {

    }


    public static String getDbName() {
        return DATABASE_NAME;
    }

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }
}
