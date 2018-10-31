package com.sachet.database2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";

    public static final String DATABASE_NAME = "TaskTimer.db";
    public static final int DATABASE_VERSION = 1;

    private static AppDatabase instance = null;

    private AppDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor called ");
    }

    public static AppDatabase getInstance(Context context){
        if(instance == null){
            instance = new AppDatabase(context);
            Log.d(TAG, "getInstance: creating the singleton instance");
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: creating the database ");
        String sSql;
//        sSql = "CREATE TABLE Tasks(_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT, SortOrder INTEGER);";
        sSql = "CREATE TABLE "+TaskContracts.TABLE_NAME+
                "( "+TaskContracts.Columns._ID+" INTEGER PRIMARY KEY NOT NULL, "
                    +TaskContracts.Columns.TASKS_NAME+" TEXT NOT NULL, "
                    +TaskContracts.Columns.TASKS_DESCRIPTION+" TEXT, "
                    +TaskContracts.Columns.TASKS_SORTORDER+" INTEGER"+
                " );";
        db.execSQL(sSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch (oldVersion){
            case 1:
                //update logic from version 1
                break;

                default:
                    throw new IllegalStateException("OnUpgrade called with unknown new version "+newVersion);
        }
        Log.d(TAG, "onUpgrade: finished");
    }
}
