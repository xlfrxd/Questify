package com.example.questifyv1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.questifyv1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyActivity extends AppCompatActivity {

    private Button doneButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        firebaseAuth = FirebaseAuth.getInstance();

        doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> checkVerificationStatus());
    }

    private void checkVerificationStatus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            if (firebaseUser.isEmailVerified()) {
                // Email is verified, redirect to MainActivity
                Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Email not verified
                Toast.makeText(getApplicationContext(), "Please verify your email before proceeding.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
