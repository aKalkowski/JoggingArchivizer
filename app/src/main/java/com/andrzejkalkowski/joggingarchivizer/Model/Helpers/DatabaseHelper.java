package com.andrzejkalkowski.joggingarchivizer.Model.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andrzejkalkowski.joggingarchivizer.Model.Timer;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by komputerek on 11.04.18.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "activities";
    public static final int DATABASE_VERSION = 1;

    private Calendar calendar = Calendar.getInstance();


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DATABASE_VERSION);
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

    private void insertActivity(SQLiteDatabase db, String activity, double avgSpeed, long distance, int time) {

        ContentValues values = new ContentValues();
        values.put("date", DateFormat.getDateInstance().format(calendar.getTime()));
        values.put("activity",activity );
        values.put("average_speed", avgSpeed);
        values.put("distance", distance);
        values.put("time", Timer.seconds);
        db.insert("activity", null, values);
    }
}
