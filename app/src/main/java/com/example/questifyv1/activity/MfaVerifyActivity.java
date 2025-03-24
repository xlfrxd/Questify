package com.example.questifyv1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.questifyv1.R;
import com.example.questifyv1.database.UserDatabaseHandler;

public class MfaVerifyActivity extends AppCompatActivity {
    private EditText mfaCodeInput;
    private Button verifyButton, resendButton;
    private UserDatabaseHandler dbHelper;
    private int currentUserId;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mfa_verify); // Ensure this matches your XML filename

        // Initialize views
        mfaCodeInput = findViewById(R.id.mfaCodeInput);
        verifyButton = findViewById(R.id.verifyButton);
        resendButton = findViewById(R.id.resendButton);
        dbHelper = new UserDatabaseHandler(this);

        // Get user data from intent
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        // Send initial token
        sendNewToken();

        verifyButton.setOnClickListener(v -> {
            String userInput = mfaCodeInput.getText().toString().trim();
            if (userInput.length() == 6) {
                if (dbHelper.verifyMfaToken(currentUserId, userInput)) {
                    Toast.makeText(this, "Verified!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("userSession", getIntent().getStringExtra("USERNAME"));
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Invalid/expired code", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter a 6-digit code", Toast.LENGTH_SHORT).show();
            }
        });

        resendButton.setOnClickListener(v -> sendNewToken());
    }

    private void sendNewToken() {
        String token = dbHelper.generateAndSaveMfaToken(currentUserId);
        sendEmailWithToken(userEmail, token);
        Toast.makeText(this, "New code sent to " + userEmail, Toast.LENGTH_SHORT).show();
    }

    private void sendEmailWithToken(String email, String token) {
        // Replace with actual email logic (e.g., Firebase, JavaMail)
        Log.d("MFA_DEBUG", "Email to: " + email + " | Token: " + token);
    }
}