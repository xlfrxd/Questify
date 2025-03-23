package com.example.questifyv1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuestsDatabaseHandler extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 3; // Incremented to reflect schema changes
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

    private static final String SQL_CREATE_AUDIT_LOGS_TABLE =
            "CREATE TABLE IF NOT EXISTS audit_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "user_action TEXT NOT NULL, " +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(user_id) REFERENCES users(_id))";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + QuestContract.QuestEntry.TABLE_NAME;

    public QuestsDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_ENTRIES);
            db.execSQL(SQL_CREATE_AUDIT_LOGS_TABLE);
            Log.d("Database", "Tables created successfully");
        } catch (Exception e) {
            Log.e("Database", "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(SQL_CREATE_AUDIT_LOGS_TABLE); // Create audit logs table in case it doesn't exist
            Log.d("Database", "Database upgraded to version 2");
        }
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // New method to log user actions
    public void logAction(String username, String action) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Check if the 'users' table exists
        Cursor cursorCheck = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users'", null);
        if (cursorCheck != null && cursorCheck.moveToFirst()) {
            // 'users' table exists, fetch the user id
            cursorCheck.close();

            Cursor cursor = db.rawQuery("SELECT _id FROM users WHERE username = ?", new String[]{username});
            if (cursor.moveToFirst()) {
                int userId = cursor.getInt(0);

                // Insert the log into the audit_logs table
                values.put("user_id", userId);
                values.put("user_action", action);
                db.insert("audit_logs", null, values);
            }
            cursor.close();
        } else {
            Log.e("Database", "'users' table does not exist.");
            // Handle the error, such as showing an error message or logging it.
        }

        db.close();
    }


    public void logPostQuestAction(String userName, String questTitle) {
        String action = "User posted a quest: " + questTitle;
        logAction(userName, action); // Log the action when a quest is posted
    }
}
