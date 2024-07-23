package com.example.questifyv1.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageButton;
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
        String desc = getIntent().getStringExtra("desc");
        String dibsBy = getIntent().getStringExtra("dibsBy");

        // Find the views and set data
        TextView titleView = findViewById(R.id.tvQuestTitle);
        TextView dueDateView = findViewById(R.id.tvQuestDueDate);
        TextView usernameView = findViewById(R.id.tvQuestPostedBy);
        TextView priceView = findViewById(R.id.tvQuestReward);
        TextView categoryView = findViewById(R.id.tvQuestCategory);
        TextView descView = findViewById(R.id.tvQuestDesc);
        TextView dibsByView = findViewById(R.id.tvQuestDibsBy);


        titleView.setText(title);
        dueDateView.setText(dueDate);
        usernameView.setText(username);
        priceView.setText("PHP " + price);
        categoryView.setText(category);
        descView.setText(desc);
        dibsByView.setText(dibsBy);

        // TODO: Set do quest button functionality
        // If dibsby = none; G
        // If dibsby = someone; not G
        // If dibsby = yourself; not G

        // Set back button functionality
        ImageButton btnBack = findViewById(R.id.btnQuestBack);
        btnBack.setOnClickListener( v -> {
            finish();
                }
        );
        //btnBack.setOnClickListener();


    }
}

