package com.example.questifyv1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


public class UserDatabaseHandler extends SQLiteOpenHelper {
public static final int DATABASE_VERSION = 4; // Ient this when you change the database schema
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Creating the 'users' table with a new 'wallet' column.
            String createUsersTable = "CREATE TABLE IF NOT EXISTS " + UserContract.UserEntry.TABLE_NAME + " (" +
                    UserContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    UserContract.UserEntry.COLUMN_NAME_NAME + " TEXT," +
                    UserContract.UserEntry.COLUMN_NAME_WALLET + " REAL DEFAULT 0.0," +
                    UserContract.UserEntry.COLUMN_NAME_USERNAME + " TEXT UNIQUE," +
                    UserContract.UserEntry.COLUMN_NAME_EMAIL + " TEXT UNIQUE," +
                    UserContract.UserEntry.COLUMN_NAME_PASSWORD + " TEXT NOT NULL" +  // Removed 'role', 'mfa_code', 'mfa_verified'
                    ")";
            db.execSQL(createUsersTable);
            Log.d("Database", "Users table created successfully");

            // Create the 'audit_logs' table for logging user actions.
            String createAuditLogsTable = "CREATE TABLE IF NOT EXISTS audit_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "user_action TEXT NOT NULL, " +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(user_id) REFERENCES users(_id))";
            db.execSQL(createAuditLogsTable);
            Log.d("Database", "Audit Logs table created successfully");
        } catch (Exception e) {
            Log.e("Database", "Error creating tables: " + e.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // Update logic for new version
            // Add new columns or create tables if necessary
            db.execSQL("ALTER TABLE users ADD COLUMN wallet REAL DEFAULT 0.0");
            db.execSQL("CREATE TABLE IF NOT EXISTS audit_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "user_action TEXT NOT NULL, " +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(user_id) REFERENCES users(_id))");
            Log.d("Database", "Database updated to version 4");
        }
        // Handle further upgrades for higher versions if necessary
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void logAction(String username, String action) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Try to fetch the user ID
        Cursor cursor = db.rawQuery("SELECT _id FROM users WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            values.put("user_id", userId);
        } else {
            Log.w("Database", "Username '" + username + "' not found. Logging action without user_id.");
            // Leave user_id NULL
        }

        // Log the action regardless of whether the user was found
        values.put("user_action", action);
        long result = db.insert("audit_logs", null, values);

        if (result != -1) {
            Log.d("Database", "Action logged: " + action);
        } else {
            Log.e("Database", "Failed to log action: " + action);
        }

        if (cursor != null) cursor.close();
        db.close();
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
