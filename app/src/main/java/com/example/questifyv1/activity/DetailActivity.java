package com.example.questifyv1.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
        // Get current user signed in
        String userSession = getIntent().getStringExtra("userSession");

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
        // Get do quest button
        Button btnDoQuest = findViewById(R.id.btnQuestConfirm);
        btnDoQuest.setOnClickListener(v ->{
            // Check dibsby
            if(dibsByView.getText().equals("NONE")){
                // Quest is vacant
                Toast.makeText(this, "Vacant",Toast.LENGTH_LONG).show();
                // TODO: Search database for same title and author
                // TODO: Update database
            }
            else if(dibsByView.getText().equals(userSession)){
                // Quest is already dibs by current user
                Toast.makeText(this,"You", Toast.LENGTH_LONG).show();
            } else {
                // Quest is dibs by other users
                Toast.makeText(this, dibsBy,Toast.LENGTH_LONG).show();;
            }
        });

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

