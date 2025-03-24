package com.example.questifyv1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.security.SecureRandom;


public class UserDatabaseHandler extends SQLiteOpenHelper {
public static final int DATABASE_VERSION = 5; // Ient this when you change the database schema
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
        // Enable foreign keys
        db.execSQL("PRAGMA foreign_keys = ON;");

        db.beginTransaction();
        try {
            // Create users table
            String createUsersTable = "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                    UserContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    UserContract.UserEntry.COLUMN_NAME_NAME + " TEXT," +
                    UserContract.UserEntry.COLUMN_NAME_WALLET + " REAL DEFAULT 0.0," +
                    UserContract.UserEntry.COLUMN_NAME_USERNAME + " TEXT UNIQUE," +
                    UserContract.UserEntry.COLUMN_NAME_EMAIL + " TEXT UNIQUE," +
                    UserContract.UserEntry.COLUMN_NAME_PASSWORD + " TEXT NOT NULL," +
                    "role TEXT CHECK(role IN ('admin','quest_poster','quest_taker')) DEFAULT 'quest_taker'," +
                    "mfa_code TEXT," +
                    "mfa_verified INTEGER DEFAULT 0," +
                    "mfa_expires_at INTEGER)";
            db.execSQL(createUsersTable);

            // Create audit_logs table
            String createAuditLogs = "CREATE TABLE audit_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "user_action TEXT NOT NULL," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(user_id) REFERENCES " + UserContract.UserEntry.TABLE_NAME +
                    "(" + UserContract.UserEntry._ID + ") ON DELETE CASCADE)";
            db.execSQL(createAuditLogs);

            db.setTransactionSuccessful();
            Log.d("Database", "Tables created successfully");
        } catch (Exception e) {
            Log.e("Database", "Table creation failed", e);
            throw new RuntimeException("Database initialization failed", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            if (oldVersion < 5) {  // Match your current version
                // Completely rebuild the database for major version changes
                db.execSQL("DROP TABLE IF EXISTS audit_logs");
                db.execSQL("DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME);
                onCreate(db);
            }
            // Add other version-specific migrations here if needed
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Database", "Upgrade failed", e);
        } finally {
            db.endTransaction();
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void logAction(String username, String action) {
        // Validate inputs
        if (username == null || username.trim().isEmpty() || action == null) {
            Log.e("Database", "Invalid input: username=" + username + ", action=" + action);
            return;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getWritableDatabase();

            // 1. Get user ID with parameterized query
            cursor = db.rawQuery(
                    "SELECT " + UserContract.UserEntry._ID + " FROM " + UserContract.UserEntry.TABLE_NAME +
                            " WHERE " + UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?",
                    new String[]{username}
            );

            if (cursor != null && cursor.moveToFirst()) {
                int userId = cursor.getInt(0);

                // 2. Insert with transaction
                db.beginTransaction();
                try {
                    ContentValues values = new ContentValues();
                    values.put("user_id", userId);
                    values.put("user_action", action);

                    long result = db.insert("audit_logs", null, values);
                    db.setTransactionSuccessful(); // Mark successful if we reach here

                    if (result == -1) {
                        Log.e("Database", "Insert failed for action: " + action);
                        // Optional: Check common failure reasons
                        logDatabaseDiagnostics(db);
                    } else {
                        Log.d("Database", "Logged action: " + action);
                    }
                } finally {
                    db.endTransaction();
                }
            } else {
                Log.e("Database", "User not found: " + username);
            }
        } catch (Exception e) {
            Log.e("Database", "Error logging action: " + e.getMessage(), e);
        } finally {
            // 3. Safely close resources
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON");
    }

    // Helper method to diagnose common issues
    private void logDatabaseDiagnostics(SQLiteDatabase db) {
        try {
            // Check if table exists
            Cursor c = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='audit_logs'",
                    null
            );
            boolean tableExists = c.getCount() > 0;
            c.close();
            Log.d("Database", "audit_logs table exists: " + tableExists);

            // Check foreign key constraints
            if (tableExists) {
                c = db.rawQuery("PRAGMA foreign_key_check(audit_logs)", null);
                if (c.getCount() > 0) {
                    Log.e("Database", "Foreign key violation in audit_logs");
                }
                c.close();
            }
        } catch (Exception e) {
            Log.e("Database", "Diagnostic failed: " + e.getMessage());
        }
    }

    public String generateAndSaveMfaToken(int userId) {
        SecureRandom random = new SecureRandom();
        String token = String.format("%06d", random.nextInt(999999)); // 6-digit code

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("mfa_code", token);
            values.put("mfa_verified", 0);
            values.put("mfa_expires_at", System.currentTimeMillis() + (5 * 60 * 1000)); // 5 minute expiry

            int rows = db.update(
                    UserContract.UserEntry.TABLE_NAME,
                    values,
                    UserContract.UserEntry._ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );

            if (rows == 0) {
                Log.e("MFA", "No rows updated for user ID: " + userId);
            }
            return token;
        } finally {
            db.close();
        }
    }

    public boolean verifyMfaToken(int userId, String userInputToken) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String query = "SELECT mfa_code, mfa_expires_at FROM " +
                    UserContract.UserEntry.TABLE_NAME +
                    " WHERE " + UserContract.UserEntry._ID + " = ? AND mfa_verified = 0";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            try {
                if (cursor.moveToFirst()) {
                    String storedToken = cursor.getString(0);
                    long expiresAt = cursor.getLong(1);

                    if (storedToken.equals(userInputToken) &&
                            System.currentTimeMillis() < expiresAt) {

                        // Mark as verified
                        ContentValues values = new ContentValues();
                        values.put("mfa_verified", 1);
                        db.update(
                                UserContract.UserEntry.TABLE_NAME,
                                values,
                                UserContract.UserEntry._ID + " = ?",
                                new String[]{String.valueOf(userId)}
                        );
                        return true;
                    }
                }
                return false;
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }

    public UserDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("Database Path", context.getDatabasePath(DATABASE_NAME).getAbsolutePath());

    }

    // Get user ID from username
    public int getUserIdFromUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                new String[]{UserContract.UserEntry._ID},
                UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?",
                new String[]{username},
                null, null, null
        );

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }

    // Get user email (for sending MFA codes)
    public String getUserEmail(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                new String[]{UserContract.UserEntry.COLUMN_NAME_EMAIL},
                UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?",
                new String[]{username},
                null, null, null
        );

        String email = "";
        if (cursor.moveToFirst()) {
            email = cursor.getString(0);
        }
        cursor.close();
        return email;
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
