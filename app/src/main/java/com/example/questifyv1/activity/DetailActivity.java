package com.example.questifyv1.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.questifyv1.R;
import com.example.questifyv1.database.QuestContract;
import com.example.questifyv1.database.QuestsDatabaseHandler;

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
        String category;
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
        } else {
            category = "";
        }
        String categoryTitle = category + " Service";
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
        LinearLayout categoryContainer = findViewById(R.id.categoryContainer); // Changes between 4 categories


        titleView.setText(title);
        dueDateView.setText(dueDate);
        usernameView.setText("@"+username);
        priceView.setText("PHP " + price);
        categoryView.setText(categoryTitle);
        descView.setText(desc);
        dibsByView.setText(dibsBy);
        // Change quest background based on quest Category
        switch (category){
            case "Physical":
                categoryContainer.setBackground(getDrawable(R.drawable.physical_container));
                break;
            case "Digital":
                categoryContainer.setBackground(getDrawable(R.drawable.digital_container));
                break;
            case "Professional":
                categoryContainer.setBackground(getDrawable(R.drawable.professional_container));
                break;
            case "Creative":
                categoryContainer.setBackground(getDrawable(R.drawable.creative_container));
                break;
        }

        // TODO: Set do quest button functionality
        // Get do quest button
        Button btnDoQuest = findViewById(R.id.btnQuestConfirm);
        btnDoQuest.setOnClickListener(v ->{
            // Check if quest is dibsBy = currentUser
            if(dibsByView.getText().equals(userSession)){
                // Quest is already dibs by current user or is owned by current user
                Toast.makeText(this,"You already called dibs on this!", Toast.LENGTH_LONG).show();
            }
            else if(username.equals(userSession)){
                // Quest is owned by current user
                Toast.makeText(this,"You posted this!", Toast.LENGTH_LONG).show();
            }
            // Check if quest is vacant (dibsBy is NONE)
            else if(dibsByView.getText().equals("NONE")){
                // Quest is vacant
                // Instantiate database
                QuestsDatabaseHandler dbHelper = new QuestsDatabaseHandler(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // New values for column
                ContentValues values = new ContentValues();
                values.put(QuestContract.QuestEntry.COLUMN_NAME_TITLE, title);
                values.put(QuestContract.QuestEntry.COLUMN_NAME_CATEGORY, category);
                values.put(QuestContract.QuestEntry.COLUMN_NAME_DUE_DATE, dueDate);
                values.put(QuestContract.QuestEntry.COLUMN_NAME_DESCRIPTION, desc);
                values.put(QuestContract.QuestEntry.COLUMN_NAME_REWARD, price);
                values.put(QuestContract.QuestEntry.COLUMN_NAME_STATUS, "IN_PROGRESS"); // Update status to IN PROGRESS
                values.put(QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY, username); // postedBy; not to be confused with current user
                values.put(QuestContract.QuestEntry.COLUMN_NAME_DIBSBY, userSession); // Current user
                // Search database for same title and author
                String selection = QuestContract.QuestEntry.COLUMN_NAME_TITLE + " LIKE ? AND " + QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY + " LIKE ?";
                String[] selectionArgs = {title, username};

                // count returns number of rows affected
                int count = db.update(
                        QuestContract.QuestEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );

                //Log.e("Number of rows affected", String.valueOf(count));
                Toast.makeText(this, "Dibs on " + title + "!", Toast.LENGTH_SHORT).show();
                // Back to MainActivity
                finish();
            }
            else {
                // Quest is dibs by other users
                Toast.makeText(this,  dibsBy + " has dibs on this!",Toast.LENGTH_LONG).show();
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

