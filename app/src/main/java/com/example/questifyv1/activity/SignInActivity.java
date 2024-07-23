package com.example.questifyv1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.questifyv1.R;

public class SignInActivity extends AppCompatActivity {
    private Button signInButton;
    private Button registerButton;

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

        signInButton = findViewById(R.id.btnSignIn);
        signInButton.setOnClickListener(v -> {
            // Handle sign-in button click
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