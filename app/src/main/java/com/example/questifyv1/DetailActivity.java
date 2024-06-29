package com.example.questifyv1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Retrieve data from the Intent
        String title = getIntent().getStringExtra("title");
        String dueDate = getIntent().getStringExtra("dueDate");
        String username = getIntent().getStringExtra("username");
        int price = getIntent().getIntExtra("price", 0);
        int imageResource = getIntent().getIntExtra("imageResource", 0);

        // Find the views and set data
        TextView titleView = findViewById(R.id.detail_title);
        TextView dueDateView = findViewById(R.id.detail_due_date);
        TextView usernameView = findViewById(R.id.detail_username);
        TextView priceView = findViewById(R.id.detail_price);
        ImageView imageView = findViewById(R.id.detail_image);

        titleView.setText(title);
        dueDateView.setText(dueDate);
        usernameView.setText(username);
        priceView.setText("PHP " + price);
        imageView.setImageResource(imageResource);
    }
}

