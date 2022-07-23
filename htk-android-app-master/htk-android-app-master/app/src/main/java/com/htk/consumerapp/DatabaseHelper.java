package com.htk.consumerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "DB";
    public static final String TABLE_USER = "user";
    public static final String COL_1 = "loggedInWith";
    public SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_USER + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, loggedInWith TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public SQLiteDatabase getDB() {
        return this.getWritableDatabase();
    }


}