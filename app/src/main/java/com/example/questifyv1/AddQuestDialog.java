package com.example.questifyv1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

public class AddQuestDialog extends DialogFragment {

    Button btnPostQuest;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the LayoutInflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because it's going in the dialog layout.
        builder.setView(inflater.inflate(R.layout.dialog_add, null));

        // Declare widgets
        btnPostQuest = btnPostQuest.findViewById(R.id.btn_addQuest);
        btnPostQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Add quest to database


            }
        });

        // Create the AlertDialog and return it
        return builder.create();
    }


}
