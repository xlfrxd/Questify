package com.example.questifyv1.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.questifyv1.R;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_quest);

        // Retrieve data from the Intent
        String title = getIntent().getStringExtra("title");
        String dueDate = getIntent().getStringExtra("dueDate");
        String username = getIntent().getStringExtra("username");
        String price = getIntent().getStringExtra("price");
        int imageResource = getIntent().getIntExtra("imageResource", 0);
        String category = "";
        if(imageResource == R.drawable.physical){
            category = "Physical";
        }
        else if(imageResource == R.drawable.laptop){
            category = "Digital";
        }
        else if(imageResource == R.drawable.briefcase){
            category = "Professional";
        }
        else if(imageResource == R.drawable.idea){
            category = "Creative";
        }

        category = category + " Service";

        // Find the views and set data
        TextView titleView = findViewById(R.id.questTitle);
        TextView dueDateView = findViewById(R.id.tvDueDate);
        TextView usernameView = findViewById(R.id.tvPostedBy);
        TextView priceView = findViewById(R.id.tvReward);
        TextView categoryView = findViewById(R.id.tvCategory);


        titleView.setText(title);
        dueDateView.setText(dueDate);
        usernameView.setText(username);
        priceView.setText("PHP " + price);
        categoryView.setText(category);

    }
}

