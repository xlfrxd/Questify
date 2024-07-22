package com.example.questifyv1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;


import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.questifyv1.database.QuestsDatabaseHandler;
import com.example.questifyv1.database.QuestContract;

public class AddQuestDialog extends DialogFragment {
    private EditText etQuestTitle;
    private EditText etCategory;
    private EditText etDueDate;
    private EditText etDesc;
    private EditText etNumReward;
    private Button btnPostQuest;
    private MainActivity mainActivity;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
        etCategory = view.findViewById(R.id.et_Category);
        etDueDate = view.findViewById(R.id.et_DueDate);
        etDesc = view.findViewById(R.id.et_Desc);
        etNumReward = view.findViewById(R.id.et_Reward);
        btnPostQuest = view.findViewById(R.id.btn_addQuest);


        // Instantiate Database
        QuestsDatabaseHandler dbHelper = new QuestsDatabaseHandler(this.getContext());

        // Post Quest listener
        btnPostQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add quest entry

                // Gets the data repository in write mode
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(QuestContract.QuestEntry.COLUMN_NAME_TITLE, etQuestTitle.getText().toString());
                values.put(QuestContract.QuestEntry.COLUMN_NAME_CATEGORY, etCategory.getText().toString());
                values.put(QuestContract.QuestEntry.COLUMN_NAME_DUE_DATE, etDueDate.getText().toString());
                values.put(QuestContract.QuestEntry.COLUMN_NAME_DESCRIPTION, etDesc.getText().toString());
                values.put(QuestContract.QuestEntry.COLUMN_NAME_REWARD, etNumReward.getText().toString());
                values.put(QuestContract.QuestEntry.COLUMN_NAME_STATUS, "NONE");
                values.put(QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY, "default_user"); //TODO: Change to current user
                values.put(QuestContract.QuestEntry.COLUMN_NAME_DIBSBY, "NONE"); // default; to be updated when status changes

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(QuestContract.QuestEntry.TABLE_NAME, null, values);

                // After adding, update the RecyclerView
                if (mainActivity != null){
                    mainActivity.updateQuestFeed();
                    //mainActivity.setHomeFragment();
                }

                dismiss(); // Close dialog after
            }
        });

        // Create the AlertDialog and return it
        return builder.create();
    }
}
