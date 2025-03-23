package com.example.questifyv1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.questifyv1.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EmailVerificationActivity extends AppCompatActivity {

    EditText tokenField;
    Button verifyButton;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        tokenField = findViewById(R.id.etToken);
        verifyButton = findViewById(R.id.btnVerifyToken);
        queue = Volley.newRequestQueue(this);

        verifyButton.setOnClickListener(v -> {
            String token = tokenField.getText().toString();
            verifyToken(token);
        });
    }

    private void verifyToken(String token) {
        String url = "https://your-backend.com/api/verifyToken";  // Replace with your actual URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");
                if (success) {
                    Toast.makeText(EmailVerificationActivity.this, "Verification successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EmailVerificationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(EmailVerificationActivity.this, "Invalid token, please try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(EmailVerificationActivity.this, "Error processing the response", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Log.e("Volley", "Error: " + error.getMessage());
            Toast.makeText(EmailVerificationActivity.this, "Network error, please try again later.", Toast.LENGTH_SHORT).show();
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
