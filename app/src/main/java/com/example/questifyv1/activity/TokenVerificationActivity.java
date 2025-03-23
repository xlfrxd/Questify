package com.example.questifyv1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.questifyv1.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class TokenVerificationActivity extends AppCompatActivity {

    EditText tokenField;
    Button verifyButton;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_verification);

        tokenField = findViewById(R.id.tokenField);
        verifyButton = findViewById(R.id.verifyButton);
        queue = Volley.newRequestQueue(this);

        verifyButton.setOnClickListener(v -> {
            String token = tokenField.getText().toString();
            verifyToken(token);
        });
    }

    private void verifyToken(String token) {
        String url = "https://your-backend.com/api/verifyToken";  // Replace with your actual URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Assuming the response from the server is a simple success flag
                if ("success".equals(response)) {
                    Toast.makeText(TokenVerificationActivity.this, "Token verified successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TokenVerificationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(TokenVerificationActivity.this, "Invalid token, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TokenVerificationActivity.this, "Error verifying token: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);  // The field 'token' should match your server expectation
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
