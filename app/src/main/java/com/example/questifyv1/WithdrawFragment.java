package com.example.questifyv1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.questifyv1.activity.MainActivity;

public class WithdrawFragment extends DialogFragment {
    private MainActivity mainActivity;

    private String displayCurrentBalance; // Current Wallet Balance: ₱480.10
    private double currentBalance; // 480.10
    private double withdrawAmount;
    private String[] amountChoices = {"100", "500", "1000"};

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof MainActivity){
            mainActivity = (MainActivity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        // Get LayoutInflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Store in view to initialize child widgets
        View view = getActivity().getLayoutInflater().inflate(R.layout.new_wallet_withdraw,null,false);
        // Set the view for the dialog
        builder.setView(view);

        // Get current balance
        Bundle bundle = getArguments();
        currentBalance = bundle.getDouble("userWallet");

        // Initialize widgets
        // Displays user current balance
        TextView tvCurrentBalance = view.findViewById(R.id.tvWithdraw_CurrentBalance);
        // Input cash to withdraw
        EditText etWithdrawAmt = view.findViewById(R.id.et_WithdrawAmount);
        // Helper buttons for simple cash in (100, 500, 1000)
        Button btnAmt100 = view.findViewById(R.id.btnWithdraw100);
        Button btnAmt500 = view.findViewById(R.id.btnWithdraw500);
        Button btnAmt1000 = view.findViewById(R.id.btnWithdraw1000);
        // Close button
        ImageButton btnClose = view.findViewById(R.id.btn_CloseWithdraw);
        // Confirm button
        Button btnConfirmCashIn = view.findViewById(R.id.btn_ConfirmWithdraw);

        // Format currentBalance to 2 decimal places and add placeholder text
        displayCurrentBalance = "Current Wallet Balance: ₱" + String.format("%.2f", currentBalance);

        // Set current balance
        tvCurrentBalance.setText(displayCurrentBalance);

        // Set button behaviors
        // Close button
        btnClose.setOnClickListener(view1 -> {
            dismiss(); // Close dialog
        });
        // Set amount 100
        btnAmt100.setOnClickListener(view1 -> {
            etWithdrawAmt.setText(amountChoices[0]);
        });
        // Set amount 500
        btnAmt500.setOnClickListener(view1 -> {
            etWithdrawAmt.setText(amountChoices[1]);
        });
        // Set amount 1000
        btnAmt1000.setOnClickListener(view1 -> {
            etWithdrawAmt.setText(amountChoices[2]);
        });

        // Confirm withdraw
        btnConfirmCashIn.setOnClickListener(view1 -> {
            // Check if empty
            if(etWithdrawAmt.getText().toString().isEmpty()){
                Toast.makeText(getContext(), "Input amount to withdraw", Toast.LENGTH_SHORT).show();
            }
            // Check if balance < withdrawAmount
            else if(currentBalance < Double.valueOf(etWithdrawAmt.getText().toString())){
                Toast.makeText(getContext(),"You have insufficient funds", Toast.LENGTH_SHORT).show();
            }
            else {
                // Get amount to withdraw
                withdrawAmount = Double.valueOf(etWithdrawAmt.getText().toString());
                //Log.e("withdrawAmt:",String.valueOf(withdrawAmount));
                double newBalance = currentBalance - withdrawAmount;
                mainActivity.updateUserBalance(newBalance);

                dismiss();
            }
        });


        return builder.create();
    }

}
