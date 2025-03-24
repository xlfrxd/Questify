package com.example.questifyv1.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.questifyv1.R;
import com.example.questifyv1.database.QuestContract;
import com.example.questifyv1.database.QuestsDatabaseHandler;
import com.example.questifyv1.database.UserContract;
import com.example.questifyv1.database.UserDatabaseHandler;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {
    private MainActivity mainActivity;
    private String categoryTitle;
    private String desc;
    private String dibsBy;
    private String status;
    private String username;
    private String userSession;

    // Buttons
    private Button btnDoQuest;
    private Button btnCompleteQuest;
    private Button btnCancelQuest;

    QuestsDatabaseHandler dbHelper = new QuestsDatabaseHandler(this);

    private void toggleButtonVisibility(){
        // Update button displays
        if(username.equals(userSession)){
            // Posted by user
            // Show Cancel and Mark as Complete
            btnDoQuest.setVisibility(View.GONE);
            btnCompleteQuest.setVisibility(View.VISIBLE);
            btnCancelQuest.setVisibility(View.VISIBLE);
            // User can't mark this as complete by default
            btnCompleteQuest.setEnabled(false);
            if(status.equals("IN_PROGRESS")){
                // Posted by user and has dibs
                // User can mark this as complete
                btnCompleteQuest.setEnabled(true);
            }
            else if(status.equals("CANCELLED")){
                // Disable both cancel and complete
                btnCancelQuest.setEnabled(false);
            }
            else if(status.equals("FLAG_DONE")){
                btnCompleteQuest.setEnabled(true);
            }
        }
        else if(dibsBy.equals(userSession) && !dibsBy.equals("CANCELLED") && !dibsBy.equals("DONE")) {
            // User dibs post
            // User can cancel or mark this as complete
            btnDoQuest.setVisibility(View.GONE);
            btnCompleteQuest.setVisibility(View.VISIBLE);
            btnCancelQuest.setVisibility(View.VISIBLE);
        }
        else if(status.equals("IN_PROGRESS") && !dibsBy.equals("NONE")){
            // Not posted by user and is not vacant
            // Show Do Quest
            btnDoQuest.setVisibility(View.VISIBLE);
            btnCompleteQuest.setVisibility(View.GONE);
            btnCancelQuest.setVisibility(View.GONE);
            // User can't dibs on this
            btnDoQuest.setEnabled(false);
        }
        else if(status.equals("NONE") && dibsBy.equals("NONE")){
            // Not posted by user and is vacant
            btnCompleteQuest.setVisibility(View.GONE);
            btnCancelQuest.setVisibility(View.GONE);
        }
        else if(status.equals("CANCELLED") || status.equals("DONE")) {
            // Cancelled completed by either host or user
            btnDoQuest.setVisibility(View.GONE);
            btnCompleteQuest.setVisibility(View.VISIBLE);
            btnCancelQuest.setVisibility(View.VISIBLE);
            // Disable both cancel and complete
            btnCompleteQuest.setEnabled(false);
            btnCancelQuest.setEnabled(false);


        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_quest);

        // Retrieve data from the Intent
        String title = getIntent().getStringExtra("title");
        String dueDate = getIntent().getStringExtra("dueDate");
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
        username = getIntent().getStringExtra("username");
        categoryTitle = category + " Service";
        desc = getIntent().getStringExtra("desc");
        dibsBy = getIntent().getStringExtra("dibsBy");
        status = getIntent().getStringExtra("status");
        // Get current user signed in
        userSession = getIntent().getStringExtra("userSession");

        // Find the views and set data
        TextView titleView = findViewById(R.id.tvQuestTitle);
        TextView dueDateView = findViewById(R.id.tvQuestDueDate);
        TextView usernameView = findViewById(R.id.tvQuestPostedBy);
        TextView priceView = findViewById(R.id.tvQuestReward);
        TextView categoryView = findViewById(R.id.tvQuestCategory);
        TextView descView = findViewById(R.id.tvQuestDesc);
        TextView dibsByView = findViewById(R.id.tvQuestDibsBy);
        TextView statusView = findViewById(R.id.tvQuestStatus);
        LinearLayout categoryContainer = findViewById(R.id.categoryContainer); // Changes between 4 categories

        // Buttons
        btnDoQuest = findViewById(R.id.btnQuestConfirm);
        btnCancelQuest = findViewById(R.id.btnQuestCancel); // TODO link
        btnCompleteQuest = findViewById(R.id.btnQuestCompleted); // TODO link


        // Set buttons to invisible by default

        // Set widget data
        titleView.setText(title);
        dueDateView.setText(dueDate);
        usernameView.setText("@"+username);
        priceView.setText("PHP " + price);
        categoryView.setText(categoryTitle);
        descView.setText(desc);
        dibsByView.setText(dibsBy);
        statusView.setText(status);
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

        // Update button visibility on start up
        toggleButtonVisibility();


        btnCompleteQuest.setOnClickListener(v -> {
            QuestsDatabaseHandler dbHelper = new QuestsDatabaseHandler(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            // if user dibsBy
            if(userSession.equals(dibsBy)){ // Quest Taker clicks "Mark as Complete" Button

                // update status to FLAG_DONE
                values.put(QuestContract.QuestEntry.COLUMN_NAME_STATUS, "FLAG_DONE");
                // search database for title and dibsby
                String selection = QuestContract.QuestEntry.COLUMN_NAME_TITLE + " LIKE ? AND " + QuestContract.QuestEntry.COLUMN_NAME_DIBSBY + " LIKE ?";
                String[] selectionArgs = {title, dibsBy};

                // count returns number of rows affected
                int count = db.update(
                        QuestContract.QuestEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                dbHelper.logAction(userSession, "Quest Taker " + userSession + " flagged quest as complete: " + title);
                Toast.makeText(this, "Sent Completed Request to " + username, Toast.LENGTH_SHORT).show();


            }
            // if user postedBy
            else if(userSession.equals(username)){ // Quest Owner clicks "Mark as Complete" Button

                // update status to done

                // New values for column
                values.put(QuestContract.QuestEntry.COLUMN_NAME_STATUS, "DONE");

                // Search database for title and author
                String selection = QuestContract.QuestEntry.COLUMN_NAME_TITLE + " LIKE ? AND " + QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY + " LIKE ? AND " + QuestContract.QuestEntry.COLUMN_NAME_STATUS + " LIKE ?";
                String[] selectionArgs = {title, username, "FLAG_DONE"};

                // count returns number of rows affected
                int count = db.update(
                        QuestContract.QuestEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                dbHelper.logAction(userSession, "Quest Owner " + username + " marked quest as complete: " + title);
                Toast.makeText(this,"Completed \"" + title + "\"!", Toast.LENGTH_LONG).show();

                // Update status of post
                status = "DONE";

                // Release funds to dibsBy
                UserDatabaseHandler userDatabaseHandler = new UserDatabaseHandler(this);
                double walletBalance = Double.parseDouble(userDatabaseHandler.getUserInfo(dibsBy)[2]);
                double sendBalance = Double.parseDouble(price);
                sendBalance = walletBalance + sendBalance;

                UserDatabaseHandler userDbHelper = new UserDatabaseHandler(this);
                db = userDbHelper.getWritableDatabase();
                String[] projection = {
                        BaseColumns._ID,
                        UserContract.UserEntry.COLUMN_NAME_USERNAME,
                        UserContract.UserEntry.COLUMN_NAME_WALLET
                };

                // WHERE currentUser = user
                selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " LIKE ?";
                selectionArgs = new String[]{dibsBy};

                values = new ContentValues();
                values.put(UserContract.UserEntry.COLUMN_NAME_WALLET, String.valueOf(sendBalance));

                // number of rows affected
                count = db.update(
                        UserContract.UserEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
            }


            // Update buttons
            toggleButtonVisibility();
            finish();
        });

        // Cancel quest
        btnCancelQuest.setOnClickListener(v -> { // Quest Taker clicks "Cancel" Button
            //Toast.makeText(this, "Cancel", Toast.LENGTH_LONG).show();
            // update status to cancel
            QuestsDatabaseHandler dbHelper = new QuestsDatabaseHandler(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // New values for column
            ContentValues values = new ContentValues();
            values.put(QuestContract.QuestEntry.COLUMN_NAME_STATUS, "CANCELLED");

            // Search database for title and author
            String selection = QuestContract.QuestEntry.COLUMN_NAME_TITLE + " LIKE ? AND " + QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY + " LIKE ?";
            String[] selectionArgs = {title, username};

            // count returns number of rows affected
            int count = db.update(
                    QuestContract.QuestEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );
            dbHelper.logAction(userSession, "Quest Taker " + userSession + " canceled the quest: " + title);
            Toast.makeText(this, "\"" + title + "\" was cancelled!", Toast.LENGTH_LONG).show();
            // Update status
            status = "CANCELLED";

            // Update buttons
            toggleButtonVisibility();
        });

        // Do Quest
        btnDoQuest.setOnClickListener(v ->{
            // Check if quest is dibsBy = currentUser
            if(dibsByView.getText().equals(userSession)){ // Quest Taker clicks "Do Quest" button
                // Quest is already dibs by current user or is owned by current user
                dbHelper.logAction(userSession, "Quest Taker" + userSession + "tried to do the a quest they already took: " + title);
                Toast.makeText(this,"You already called dibs on this!", Toast.LENGTH_LONG).show();
            }
            else if(username.equals(userSession)){ // Quest Owner clicks "Do Quest" button on their own posted quest
                // Quest is owned by current user
                dbHelper.logAction(userSession, "Quest Owner" + userSession + " tried to do their own quest: " + title);
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

                // Update dibsBy
                dibsBy = userSession;
                // Update status
                status = "IN_PROGRESS";

                dbHelper.logAction(userSession, "User " + userSession + " took the quest: " + title);
                Toast.makeText(this, "Dibs on " + title + "!", Toast.LENGTH_SHORT).show();
                toggleButtonVisibility();
            }
            else {
                // Quest is dibs by other users
                Toast.makeText(this,  dibsBy + " has dibs on this!",Toast.LENGTH_LONG).show();
            }
        });

        // Set back button functionality
        ImageButton btnBack = findViewById(R.id.btnQuestBack);
        btnBack.setOnClickListener( v -> {
            finish();
                }
        );
        //btnBack.setOnClickListener();


    }
}

