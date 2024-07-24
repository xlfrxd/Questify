package com.example.questifyv1.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.questifyv1.AddQuestDialog;
import com.example.questifyv1.DataHelper;
import com.example.questifyv1.HomeFragment;
import com.example.questifyv1.Post;
import com.example.questifyv1.ProfileFragment;
import com.example.questifyv1.R;
import com.example.questifyv1.database.QuestsDatabaseHandler;
import com.example.questifyv1.database.QuestContract;
import com.example.questifyv1.database.UserContract;
import com.example.questifyv1.database.UserDatabaseHandler;
import com.example.questifyv1.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends FragmentActivity {

    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private QuestsDatabaseHandler dbHelper;
    private SQLiteDatabase db;
    private static String userSession; // Currently signed in user

    private ActivityMainBinding binding;

    public static String getUserSession(){
        return userSession;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Bundle extras = getIntent().getExtras();
        // Get username of currently signed in user
        if (extras != null) {
            userSession = extras.getString("userSession");
        }

        Toast.makeText(this, "Welcome " + userSession, Toast.LENGTH_SHORT).show();

        // Instantiate Database
        dbHelper = new QuestsDatabaseHandler(this);
        db = dbHelper.getReadableDatabase();

        // Initialize fragment
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();

        // Update Quest Feed
        updateQuestFeed();

        // Set Default Home fragment to display
        replaceFragment(homeFragment);


        // Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.nav_add) { // Add Quest Dialog
                // Create Dialog
                DialogFragment dialog_add = new AddQuestDialog();
                dialog_add.setArguments(extras); // Send current user

                // Display Dialog
                dialog_add.show(getSupportFragmentManager(),"addquest");

            } else if (item.getItemId() == R.id.nav_profile) {
                //ProfileFragment profileFragment = new ProfileFragment();

                profileFragment.setArguments(extras); // Send current user
                replaceFragment(profileFragment);

            } else if (item.getItemId() == R.id.nav_home) {
                //homeFragment = new HomeFragment();
                updateQuestFeed();
                if(!homeFragment.isVisible()){

                   replaceFragment(homeFragment);
                }

            }

            return true;
        });

        // RecyclerView Stuff
        /*
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = DataHelper.getPostData();
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

         */

    }
private void replaceFragment(Fragment fragment){
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.frame_layout, fragment);
    fragmentTransaction.commit();
}

public void updateUserBalance(double newBalance){
    profileFragment = new ProfileFragment();
    UserDatabaseHandler userDbHelper = new UserDatabaseHandler(this);
    db = userDbHelper.getWritableDatabase();
    String[] projection = {
            BaseColumns._ID,
            UserContract.UserEntry.COLUMN_NAME_USERNAME,
            UserContract.UserEntry.COLUMN_NAME_WALLET
    };

    // WHERE currentUser = user
    String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " LIKE ?";
    String[] selectionArgs = {userSession};

    ContentValues values = new ContentValues();
    values.put(UserContract.UserEntry.COLUMN_NAME_WALLET, String.valueOf(newBalance));

    // number of rows affected
    int count = db.update(
            UserContract.UserEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
            );

    Log.e("Balance:",String.valueOf(newBalance));
    Log.e("Affected Rows:", String.valueOf(count)   );
    Bundle currentUser = new Bundle();
    currentUser.putString("userSession", userSession);
    profileFragment.setArguments(currentUser);
    replaceFragment(profileFragment);
}

public void updateQuestFeed(){
    homeFragment = new HomeFragment();
        DataHelper.clear();


        db = dbHelper.getReadableDatabase();

    // Columns to display in recycler view cards
    String[] projection = {
            BaseColumns._ID,
            QuestContract.QuestEntry.COLUMN_NAME_TITLE,
            QuestContract.QuestEntry.COLUMN_NAME_DUE_DATE,
            QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY,
            QuestContract.QuestEntry.COLUMN_NAME_REWARD,
            QuestContract.QuestEntry.COLUMN_NAME_CATEGORY,
            QuestContract.QuestEntry.COLUMN_NAME_DESCRIPTION,
            QuestContract.QuestEntry.COLUMN_NAME_DIBSBY,
            QuestContract.QuestEntry.COLUMN_NAME_STATUS
    };

    // Filter results WHERE "status" != "DONE"
    String selection = QuestContract.QuestEntry.COLUMN_NAME_STATUS + " != ?";
    /*
    Status can be:
    - NONE (default)
    - IN_PROGRESS
    - DONE
     */
    String[] selectionArgs = { "DONE" };


    // Sorting by title (TODO: Change to due date)
    String sortOrder = QuestContract.QuestEntry.COLUMN_NAME_TITLE + " ASC";
    Cursor cursor = db.query(
            QuestContract.QuestEntry.TABLE_NAME,
            projection,         // Columns to display
            selection,          // Filter results
            selectionArgs,      // Filter results
            null,               // Group by
            null,               // Filter by row groups
            sortOrder           // Sort order
    );

    // Create new post list
    List<Post> updatedPostList = DataHelper.getPostData();

    // Loop through items
    while (cursor.moveToNext()){

        // Get values
        long itemId = cursor.getLong(
                cursor.getColumnIndexOrThrow(QuestContract.QuestEntry._ID));
        String itemTitle = cursor.getString(
                cursor.getColumnIndexOrThrow(QuestContract.QuestEntry.COLUMN_NAME_TITLE)
        );
        String itemDueDate = cursor.getString(
                cursor.getColumnIndexOrThrow(QuestContract.QuestEntry.COLUMN_NAME_DUE_DATE)
        );
        String itemAuthor = cursor.getString(
                cursor.getColumnIndexOrThrow(QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY)
        );
        String itemReward = cursor.getString(
                cursor.getColumnIndexOrThrow(QuestContract.QuestEntry.COLUMN_NAME_REWARD)
        );
        String itemCategory = cursor.getString(
                cursor.getColumnIndexOrThrow(QuestContract.QuestEntry.COLUMN_NAME_CATEGORY)
        );
        String itemDesc = cursor.getString(
                cursor.getColumnIndexOrThrow(QuestContract.QuestEntry.COLUMN_NAME_DESCRIPTION)
        );
        String itemDibsBy = cursor.getString(
                cursor.getColumnIndexOrThrow(QuestContract.QuestEntry.COLUMN_NAME_DIBSBY)
        );
        String itemStatus = cursor.getString(
                cursor.getColumnIndexOrThrow(QuestContract.QuestEntry.COLUMN_NAME_STATUS)
        );
        // Add to list
        DataHelper.addPost(itemTitle, itemDueDate, itemAuthor, itemReward, itemCategory, itemDesc, itemDibsBy, itemStatus);
    }
    // Close cursor
    cursor.close();
    // Update recycler view
    if (homeFragment.isVisible()) {
        homeFragment.updateData(updatedPostList);
    }
    replaceFragment(homeFragment);
}
}

