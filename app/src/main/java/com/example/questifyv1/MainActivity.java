package com.example.questifyv1;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.database.sqlite.SQLiteDatabase;
import com.example.questifyv1.database.QuestsDatabaseHandler;
import com.example.questifyv1.database.QuestContract;
import com.example.questifyv1.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends FragmentActivity {

    private HomeFragment homeFragment;
    private QuestsDatabaseHandler dbHelper;
    private SQLiteDatabase db;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Instantiate Database
        dbHelper = new QuestsDatabaseHandler(this);
        db = dbHelper.getReadableDatabase();

        // Initialize fragment
        homeFragment = new HomeFragment();

        // Update Quest Feed
        updateQuestFeed();

        // Set Default Home fragment to display
        replaceFragment(homeFragment);


        // Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.nav_add) { // Add Quest Dialog
                // Create Dialog
                DialogFragment dialog_add;
                dialog_add = new AddQuestDialog();

                dialog_add.show(getSupportFragmentManager(),"addquest");

            } else if (item.getItemId() == R.id.nav_profile) {
                replaceFragment(new ProfileFragment());

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
    };

    // Filter results WHERE "status" = "NONE"
    String selection = QuestContract.QuestEntry.COLUMN_NAME_STATUS + " = ?";
    String[] selectionArgs = { "NONE" };

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
        // Add to list
        DataHelper.addPost(itemTitle, itemDueDate, itemAuthor, itemReward, itemCategory);
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

