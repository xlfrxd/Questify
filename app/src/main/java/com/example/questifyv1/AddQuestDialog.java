package com.example.questifyv1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;


import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.questifyv1.activity.MainActivity;
import com.example.questifyv1.database.QuestsDatabaseHandler;
import com.example.questifyv1.database.QuestContract;

import java.lang.reflect.Array;

public class AddQuestDialog extends DialogFragment {
    private EditText etQuestTitle;
    private EditText etDueDate;
    private EditText etDesc;
    private EditText etNumReward;
    private Button btnPostQuest;
    private ImageButton btnClose;
    private Spinner spCategory;
    private MainActivity mainActivity;
    private static final String USER_SESSION = "userSession";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity){
            mainActivity = (MainActivity) context;
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        // Get the LayoutInflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and pass null as the parent view because it's going in the dialog layout.
        // Store in view to initialize child widgets
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add,null,false);
        // Set the view for the dialog
        builder.setView(view);
        // Set the title for the dialog (optional but uglee)
        //builder.setTitle("Add Quest");

        // Initialize widgets
        etQuestTitle = view.findViewById(R.id.et_QuestTitle);
        spCategory = view.findViewById(R.id.et_Category);
        etDueDate = view.findViewById(R.id.et_DueDate);
        etDesc = view.findViewById(R.id.et_Desc);
        etNumReward = view.findViewById(R.id.et_Reward);
        btnPostQuest = view.findViewById(R.id.btn_addQuest);
        btnClose = view.findViewById(R.id.btn_close);

        // Spinner Setup
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.quest_categories,
                R.layout.spinner_item
        );
        //spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCategory.setAdapter(spAdapter);

        // Instantiate Database
        // Quest Database
        QuestsDatabaseHandler dbHelper = new QuestsDatabaseHandler(this.getContext());

        // Post Quest listener
        btnPostQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add Quest Entry

                // TODO: Implement -> Only 1 title by 1 author can be made

                // Check if input fields are empty
                if( etQuestTitle.getText().toString().isEmpty() ||
                    etDueDate.getText().toString().isEmpty() ||
                    etDesc.getText().toString().isEmpty() ||
                    etNumReward.getText().toString().isEmpty()
                    ){
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else {

                    // Placeholder for currently signed in user
                    String userName = "default_user";

                    // Get currently signed in user
                    if (getArguments() != null) {
                        userName = getArguments().getString(USER_SESSION);
                    }

                    // Flag for invalid quest
                    // Invalid quest: Same title, same user; Basically duplicate from the same user
                    Boolean questValid = false; // Set invalid quest to false by default
                    // Gets the data repository in read mode
                    SQLiteDatabase db = dbHelper.getReadableDatabase();

                    String[] projection = {
                            BaseColumns._ID,
                            QuestContract.QuestEntry.COLUMN_NAME_TITLE,
                            QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY
                    };

                    // WHERE title = etQuestTitle AND dibsBy = userName
                    String selection = QuestContract.QuestEntry.COLUMN_NAME_TITLE + " = ? AND " + QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY + " = ?";
                    String[] selectionArgs = {etQuestTitle.getText().toString(), userName};

                    // Sort by Title
                    String sortOrder = QuestContract.QuestEntry.COLUMN_NAME_TITLE + " ASC";

                    Cursor cursor; // Define cursor for results
                    try { // Try catch if ever cursor crashes from null value (empty)
                        cursor = db.query(
                                QuestContract.QuestEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
                        if(cursor.getCount()>0){ // has result = post exists
                            questValid=false; // invalid quest
                        } else { // no results = unique post
                            questValid=true; // valid quest
                        }
                    } catch (Exception e) {
                        // Quest has unique title and author is not current user
                        questValid = true; // Catches null exception if any
                    }
                    // Invalid Quest gets notified
                    if (!questValid) { // Notify user of duplicate quest
                        Toast.makeText(getContext(), "You already posted this quest", Toast.LENGTH_LONG).show();
                    } else { // Start adding quest
                        // Gets the data repository in write mode
                        db = dbHelper.getWritableDatabase();

                        // Create a new map of values, where column names are the keys
                        ContentValues values = new ContentValues();
                        values.put(QuestContract.QuestEntry.COLUMN_NAME_TITLE, etQuestTitle.getText().toString());
                        values.put(QuestContract.QuestEntry.COLUMN_NAME_CATEGORY, spCategory.getSelectedItem().toString());
                        values.put(QuestContract.QuestEntry.COLUMN_NAME_DUE_DATE, etDueDate.getText().toString());
                        values.put(QuestContract.QuestEntry.COLUMN_NAME_DESCRIPTION, etDesc.getText().toString());
                        values.put(QuestContract.QuestEntry.COLUMN_NAME_REWARD, etNumReward.getText().toString());
                        values.put(QuestContract.QuestEntry.COLUMN_NAME_STATUS, "NONE");
                        values.put(QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY, userName);
                        values.put(QuestContract.QuestEntry.COLUMN_NAME_DIBSBY, "NONE"); // default; to be updated when status changes

                        // Insert the new row, returning the primary key value of the new row
                        long newRowId = db.insert(QuestContract.QuestEntry.TABLE_NAME, null, values);

                        // After adding, update the RecyclerView
                        if (mainActivity != null) {
                            mainActivity.updateQuestFeed();
                            //mainActivity.setHomeFragment();
                        }
                        dismiss(); // Close dialog after successful quest input
                    }
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        // Create the AlertDialog and return it
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 1600;
        getDialog().getWindow().setAttributes(params);
    }
}
