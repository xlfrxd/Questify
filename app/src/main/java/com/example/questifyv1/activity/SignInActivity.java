package com.example.questifyv1.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
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
import com.example.questifyv1.database.QuestContract;
import com.example.questifyv1.database.QuestsDatabaseHandler;
import com.example.questifyv1.database.UserContract;
import com.example.questifyv1.database.UserDatabaseHandler;

public class SignInActivity extends AppCompatActivity {
    private Button signInButton;
    private TextView registerButton;
    private String userSession; // Username for currently signed in user

    private UserDatabaseHandler dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        EdgeToEdge.enable(this);
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
            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
            }
            // Check if username does not exist
            else if(!dbHelper.checkExists(username, UserContract.UserEntry.COLUMN_NAME_USERNAME)){
                // TODO: Replace hardcoded text
                Toast.makeText(this, "Username does not exist", Toast.LENGTH_SHORT).show();
            }
            // Check if credentials match
            else if(!dbHelper.verifyCredentials(username, password)){
                // TODO: Replace hardcoded text
                Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
            }
            else {
                // MFA Integration: Send verification token and move to verification activity
                sendVerificationEmail(username); // This method needs to be implemented
                Intent intent = new Intent(SignInActivity.this, TokenVerificationActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        // Navigate to Register Activity
        registerButton = findViewById(R.id.tvRegister);
        registerButton.setOnClickListener(v -> {
            // Handle register button click
            Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Implement sendVerificationEmail
    private void sendVerificationEmail(String username) {
        String token = generateToken(); // Implement token generation
        // Logic to send email with token
    }

    // Generate a secure random token
    private String generateToken() {
        // Implementation of secure random token generation
        return "123456"; // This is a placeholder
    }
}