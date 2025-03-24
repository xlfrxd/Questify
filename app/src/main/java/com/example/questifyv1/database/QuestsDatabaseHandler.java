package com.example.questifyv1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuestsDatabaseHandler extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 4; // Incremented to reflect schema changes
    public static final String DATABASE_NAME = "Posts.sqlite";

    private static final String SQL_CREATE_USERS_TABLE =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL)";

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
            db.execSQL(SQL_CREATE_USERS_TABLE); // ✅ Ensure users table exists
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
        int userId = -1;

        Cursor cursorCheck = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users'", null);
        if (cursorCheck != null && cursorCheck.moveToFirst()) {
            cursorCheck.close();

            Cursor cursor = db.rawQuery("SELECT _id FROM users WHERE username = ?", new String[]{username});
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
            } else {
                // Username not found — insert it
                ContentValues userValues = new ContentValues();
                userValues.put("username", username);
                userId = (int) db.insert("users", null, userValues);
                Log.i("Database", "Inserted new user '" + username + "' with ID " + userId);
            }
            if (cursor != null) cursor.close();

            values.put("user_id", userId);
            values.put("user_action", action);
            long rowId = db.insert("audit_logs", null, values);
            Log.d("Database", "Logged action: " + action + " | Row ID: " + rowId);
        } else {
            Log.e("Database", "'users' table does not exist.");
        }
        db.close();
    }

    public void logPostQuestAction(String userName, String questTitle) {
        String action = "User " + userName + " posted a quest: " + questTitle;
        logAction(userName, action); // Log the action when a quest is posted
    }
}
