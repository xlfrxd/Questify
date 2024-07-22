package com.example.questifyv1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class QuestsDatabaseHandler extends SQLiteOpenHelper{
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Posts.sqlite";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + QuestContract.QuestEntry.TABLE_NAME + " (" +
                    QuestContract.QuestEntry._ID + " INTEGER PRIMARY KEY," +
                    QuestContract.QuestEntry.COLUMN_NAME_TITLE + " TEXT," +
                    QuestContract.QuestEntry.COLUMN_NAME_CATEGORY + " TEXT," +
                    QuestContract.QuestEntry.COLUMN_NAME_DUE_DATE + " TEXT," +
                    QuestContract.QuestEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    QuestContract.QuestEntry.COLUMN_NAME_REWARD + " TEXT," +
                    QuestContract.QuestEntry.COLUMN_NAME_STATUS + " TEXT," +
                    QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY + " TEXT," +
                    QuestContract.QuestEntry.COLUMN_NAME_DIBSBY + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + QuestContract.QuestEntry.TABLE_NAME;

    public QuestsDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
