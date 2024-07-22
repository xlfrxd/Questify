package com.example.questifyv1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteDatabase;
import com.example.questifyv1.database.DatabaseHandler;
import com.example.questifyv1.database.QuestContract;
import com.example.questifyv1.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends FragmentActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    DialogFragment dialog_add;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Instantiate Database
        DatabaseHandler dbHelper = new DatabaseHandler(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

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

        // Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.nav_add) { // Add Quest Dialog

                replaceFragment(new HomeFragment());

                dialog_add = new AddQuestDialog();
                dialog_add.setCancelable(false);
                dialog_add.show(getSupportFragmentManager(),"addquest");


            } else if (item.getItemId() == R.id.nav_profile) {
                replaceFragment(new ProfileFragment());

            } else if (item.getItemId() == R.id.nav_home) {
                replaceFragment(new HomeFragment());

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

        // Set Default Home fragment to display
        replaceFragment(new HomeFragment());
    }
private void replaceFragment(Fragment fragment){
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.frame_layout, fragment);
    fragmentTransaction.commit();
}
}

