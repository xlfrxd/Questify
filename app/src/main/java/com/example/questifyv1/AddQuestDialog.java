package com.example.questifyv1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class AddQuestDialog extends DialogFragment {

    private Button btnPostQuest;
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
        btnPostQuest = view.findViewById(R.id.btn_addQuest);

        // Post Quest listener
        btnPostQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add quest entry
            }
        });



        // Create the AlertDialog and return it
        return builder.create();
    }



}
