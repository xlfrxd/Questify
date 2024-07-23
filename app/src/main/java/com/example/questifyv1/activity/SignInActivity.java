package com.example.questifyv1.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.questifyv1.R;
import com.example.questifyv1.database.QuestContract;
import com.example.questifyv1.database.QuestsDatabaseHandler;
import com.example.questifyv1.database.UserContract;
import com.example.questifyv1.database.UserDatabaseHandler;

public class SignInActivity extends AppCompatActivity {
    private Button signInButton;
    private Button registerButton;

    private UserDatabaseHandler dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Instantiate Database
        dbHelper = new UserDatabaseHandler(this);
        db = dbHelper.getReadableDatabase();

        // Sign in
        signInButton = findViewById(R.id.btnSignIn);
        signInButton.setOnClickListener(v -> {
            // Implement sign in logic

            // Get user inputs
            EditText etUsername = findViewById(R.id.etUsername);
            EditText etPassword = findViewById(R.id.etPassword);
            // Store locally
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            // Check user credentials
            String[] projection = {
                    BaseColumns._ID,
                    UserContract.UserEntry.COLUMN_NAME_USERNAME,
                    UserContract.UserEntry.COLUMN_NAME_PASSWORD
            };
            // Filter results WHERE "username" = username AND "password" = password
            String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ? AND " +
                    UserContract.UserEntry.COLUMN_NAME_PASSWORD + " = ?";
            String[] selectionArgs = {username, password};

            // Sort
            String sortOrder = UserContract.UserEntry._ID + " ASC";

            // TODO: Create better workaround for this
            try{ // Check if users table is empty

                // Query
                Cursor cursor = db.query(
                        UserContract.UserEntry.TABLE_NAME,
                        projection,         // Columns to display
                        selection,          // Filter results
                        selectionArgs,      // Filter results
                        null,               // Group by
                        null,               // Filter by row groups
                        sortOrder           // Sort order
                );
            }
            catch (Exception e){ // Users table is empty
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            // Sign in flag
            boolean isUserValid = false;

            // Check if username exists
            if(dbHelper.checkExists(username, UserContract.UserEntry.COLUMN_NAME_USERNAME)){
                // TODO: Replace hardcoded text
                Toast.makeText(this, "Username does not exist", Toast.LENGTH_SHORT).show();
            }

            // Sign in Successful
            if(isUserValid){
                // TODO: Pass username to MainActivity
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                finish();
            }


        });

        // Navigate to Register Activity
        registerButton = findViewById(R.id.btnRegister);
        registerButton.setOnClickListener(v -> {
            // Handle register button click
            Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}