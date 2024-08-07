package com.example.questifyv1.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


public class UserDatabaseHandler extends SQLiteOpenHelper {
public static final int DATABASE_VERSION = 3; // Ient this when you change the database schema
public static final String DATABASE_NAME = "Users.sqlite";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                    UserContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    UserContract.UserEntry.COLUMN_NAME_NAME + " TEXT," +
                    UserContract.UserEntry.COLUMN_NAME_WALLET + " TEXT," +
                    UserContract.UserEntry.COLUMN_NAME_USERNAME + " TEXT," +
                    UserContract.UserEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    UserContract.UserEntry.COLUMN_NAME_PASSWORD + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;



    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_ENTRIES);
            Log.d("Database", "Table created successfully");
        } catch (Exception e) {
            Log.e("Database", "Error creating table: " + e.getMessage());
        }
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

    public UserDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("Database Path", context.getDatabasePath(DATABASE_NAME).getAbsolutePath());

    }

    public boolean verifyCredentials(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        // Get all users information
        String[] projection = {
                BaseColumns._ID,
                UserContract.UserEntry.COLUMN_NAME_USERNAME,
                UserContract.UserEntry.COLUMN_NAME_PASSWORD
        };

        // Filter results WHERE "username" = username AND "password" = password
        String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ? AND " + UserContract.UserEntry.COLUMN_NAME_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        // Sort by username
        String sortOrder = UserContract.UserEntry.COLUMN_NAME_USERNAME + " ASC";

        Cursor cursor;
        try { // Cursor will return a table or null if no results
            cursor = db.query(
                    UserContract.UserEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
        }
        catch (Exception e){ // If table is null or empty
            e.printStackTrace();
            return false;
        }

        // Check if query returns results
        if (cursor.getCount() > 0){
            return true;
        }
        // Invalid Credentials
        return false;
    }

    public String[] getUserInfo(String username){
        SQLiteDatabase db = getReadableDatabase();

        String[] details = new String[6];
        // Get all user information
        String[] projection = {
                BaseColumns._ID,
                UserContract.UserEntry.COLUMN_NAME_NAME,
                UserContract.UserEntry.COLUMN_NAME_USERNAME,
                UserContract.UserEntry.COLUMN_NAME_WALLET,
                UserContract.UserEntry.COLUMN_NAME_EMAIL,
                UserContract.UserEntry.COLUMN_NAME_PASSWORD
        };

        // Filter results WHERE "username" = username
        String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = {username};

        // Sort by username
        String sortOrder = UserContract.UserEntry.COLUMN_NAME_USERNAME + " ASC";

        Cursor cursor;
        try {
            // Query
            cursor = db.query(
                    UserContract.UserEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            // Check if query returns results
            if (cursor.getCount() == 0) {
                return null;
            }
            else{
                cursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        details = new String[]{
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5)
        };

        return details;


    }

    public boolean checkExists(String searchArg, String columnName){
        SQLiteDatabase db = this.getReadableDatabase();
        // Get all users credentials
        String[] projection = {
                BaseColumns._ID,
                UserContract.UserEntry.COLUMN_NAME_NAME,
                UserContract.UserEntry.COLUMN_NAME_USERNAME,
                UserContract.UserEntry.COLUMN_NAME_EMAIL,
                UserContract.UserEntry.COLUMN_NAME_PASSWORD
        };
        // Filter results WHERE "columnName" = columnName
        String selection = UserContract.UserEntry._ID + " = ?";

        switch (columnName.toUpperCase()){
            case "NAME":
                selection = UserContract.UserEntry.COLUMN_NAME_NAME + " = ?";
                break;
            case "USERNAME":
                selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
                break;
            case "EMAIL":
                selection = UserContract.UserEntry.COLUMN_NAME_EMAIL + " = ?";
                break;
            case "PASSWORD":
                selection = UserContract.UserEntry.COLUMN_NAME_PASSWORD + " = ?";
                break;
        }
        // Filter results for the WHERE clause
        String[] selectionArgs = {searchArg};

        // Sort by ID
        String sortOrder = UserContract.UserEntry.COLUMN_NAME_USERNAME + " ASC";


        Cursor cursor;
        try{ // Cursor will return a table or null if no results
            // Query the user table
            cursor = db.query(
                    UserContract.UserEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
            //Log.e("Cursor Count", String.valueOf(cursor.getCount()));
        }
        catch (Exception e){ // User does not exist
            //Log.e("User does not exist", e.toString());
            return false;
        }
        if (cursor.getCount() == 0){
            return false;
        }
        // User exists
        return true;

    }


}
