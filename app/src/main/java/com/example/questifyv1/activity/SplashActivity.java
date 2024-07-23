package com.example.questifyv1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.questifyv1.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Duration of the Splash screen display
        int SPLASH_DISPLAY_LENGTH = 2000; // 2 seconds

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // After the splash screen duration, start the next activity
                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}