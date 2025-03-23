package com.example.questifyv1.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.questifyv1.R;
import com.example.questifyv1.database.UserContract;
import com.example.questifyv1.database.UserDatabaseHandler;
import com.google.android.gms.safetynet.SafetyNet;

import java.util.Objects;

// TOTP imports
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;

public class RegisterActivity extends AppCompatActivity {

    private CheckBox cbCaptcha;
    private UserDatabaseHandler dbHelper;

    private static final String SITE_KEY = "your_site_key_here"; // Replace with your actual site key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new UserDatabaseHandler(this);
        cbCaptcha = findViewById(R.id.cbCaptcha);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> attemptRegistration());

        cbCaptcha.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                verifyRecaptcha();
            }
        });
    }

    private void attemptRegistration() {
        EditText etName = findViewById(R.id.etName);
        EditText etUsername = findViewById(R.id.etUsername);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);

        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
        } else if (!cbCaptcha.isChecked()) {
            Toast.makeText(getApplicationContext(), "Please verify the reCAPTCHA", Toast.LENGTH_LONG).show();
        } else {
            registerUser(name, username, email, password);
        }
    }

    private void verifyRecaptcha() {
        SafetyNet.getClient(this).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(this, response -> {
                    if (Objects.requireNonNull(response.getTokenResult()).isEmpty()) {
                        Toast.makeText(this, "ReCaptcha verification failed, try again.", Toast.LENGTH_LONG).show();
                    } else {
                        cbCaptcha.setChecked(true); // Programmatically check this if verification is successful
                        Toast.makeText(this, "Verification succeeded.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> Toast.makeText(this, "ReCaptcha verification failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
    }

    private void registerUser(String name, String username, String email, String password) {
        // Generate TOTP secret key
        DefaultSecretGenerator secretGenerator = new DefaultSecretGenerator();
        String secretKey = secretGenerator.generate();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME_NAME, name);
        values.put(UserContract.UserEntry.COLUMN_NAME_USERNAME, username);
        values.put(UserContract.UserEntry.COLUMN_NAME_EMAIL, email);
        values.put(UserContract.UserEntry.COLUMN_NAME_PASSWORD, password);
        values.put(UserContract.UserEntry.COLUMN_NAME_WALLET, 0); // Default wallet balance
        values.put(UserContract.UserEntry.COLUMN_NAME_TOTP_SECRET, secretKey); // Use the constant from UserContract

        long newRowId = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);
        db.close();

        if (newRowId != -1) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
