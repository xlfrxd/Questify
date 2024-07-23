package com.example.questifyv1.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.questifyv1.R;
import com.example.questifyv1.database.UserContract;
import com.example.questifyv1.database.UserDatabaseHandler;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private TextView btnBack;
    private String userSession; // Username for currently signed in user
    private UserDatabaseHandler dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Instantiate dbHelper

        dbHelper = new UserDatabaseHandler(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Register Account
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
            // Implement registration logic here

            // Get user inputs
            EditText etName = findViewById(R.id.etName);
            EditText etUsername = findViewById(R.id.etUsername);
            EditText etEmail = findViewById(R.id.etEmail);
            EditText etPassword = findViewById(R.id.etPassword);
            // Store locally
            String name = etName.getText().toString();
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            int wallet = 0; // Default wallet balance


            // Check if empty fields
            if ( name.trim().isEmpty() || username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
            }
            // Check if username already exists
            else if (dbHelper.checkExists(username, "username")) {
                // Show error message
                //Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_LONG).show();
            }
            // Check if email already exists
            else if (dbHelper.checkExists(email, "email")) {
                // Show error message
                //Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_LONG).show();
            }
            else {

            // Register user credentials to database

            // Map new values
            ContentValues values = new ContentValues();
            values.put(UserContract.UserEntry.COLUMN_NAME_NAME, name);
            values.put(UserContract.UserEntry.COLUMN_NAME_WALLET, wallet);
            values.put(UserContract.UserEntry.COLUMN_NAME_USERNAME, username);
            values.put(UserContract.UserEntry.COLUMN_NAME_EMAIL, email);
            values.put(UserContract.UserEntry.COLUMN_NAME_PASSWORD, password);

            // Insert the new row
            db.insert(UserContract.UserEntry.TABLE_NAME, null, values);
            db.close();
            // Navigate to Main Activity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            // Pass username to MainActivity
            userSession = username;
            intent.putExtra("userSession", userSession);
            startActivity(intent);
            finish();
            }
        });

        // TODO: Implement reverse swipe animation
        // Navigate back to Sign In
        btnBack = findViewById(R.id.tvSignIn);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this,SignInActivity.class);
            startActivity(intent);
        finish();
        });
    }
}