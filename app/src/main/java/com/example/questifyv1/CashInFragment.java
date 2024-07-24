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

public class CashInFragment extends DialogFragment {
    private MainActivity mainActivity;
    private String displayCurrentBalance; // Current Wallet Balance: ₱480.10
    private double currentBalance; // 480.10
    private double depositAmount;
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
        View view = getActivity().getLayoutInflater().inflate(R.layout.new_wallet_cash_in,null,false);
        // Set the view for the dialog
        builder.setView(view);


        // Get current balance
        Bundle bundle = getArguments();
        currentBalance = bundle.getDouble("userWallet");

        // Initialize widgets
        // Display current balance
        TextView tvCurrentBalance = view.findViewById(R.id.tvCashIn_CurrentBalance);
        // Input cash to deposit
        EditText etCashInAmt = view.findViewById(R.id.et_CashInAmount);
        // Helper buttons for simple cash in (100, 500, 1000)
        Button btnAmt100 = view.findViewById(R.id.btnCashIn100);
        Button btnAmt500 = view.findViewById(R.id.btnCashIn500);
        Button btnAmt1000 = view.findViewById(R.id.btnCashIn1000);
        // Close button
        ImageButton btnClose = view.findViewById(R.id.btn_CloseCashIn);
        // Confirm button
        Button btnConfirmCashIn = view.findViewById(R.id.btn_ConfirmCashIn);

        // Format currentBalance to 2 decimal places and add placeholder text
        displayCurrentBalance = "Current Wallet Balance: ₱" + String.format("%.2f", currentBalance);

        // Set current balance
        tvCurrentBalance.setText(displayCurrentBalance);

        // Set button behavior

        // Close button
        btnClose.setOnClickListener(view1 -> {
            dismiss(); // Close dialog
        });
        // Set amount 100
        btnAmt100.setOnClickListener(view1 -> {
            etCashInAmt.setText(amountChoices[0]);
        });
        // Set amount 500
        btnAmt500.setOnClickListener(view1 -> {
            etCashInAmt.setText(amountChoices[1]);
        });
        // Set amount 1000
        btnAmt1000.setOnClickListener(view1 -> {
            etCashInAmt.setText(amountChoices[2]);
        });
        // Confirm deposit
        btnConfirmCashIn.setOnClickListener(view1 -> {
            // Check if empty
            if(etCashInAmt.getText().toString().isEmpty()){
                Toast.makeText(getContext(), "Input amount to deposit", Toast.LENGTH_SHORT).show();
            }
            else {
                // Get amount to deposit
                depositAmount = Double.valueOf(etCashInAmt.getText().toString());
                double newBalance = depositAmount + currentBalance;
                // Update user balance
                mainActivity.updateUserBalance(newBalance);

                dismiss();
            }
        });

        // Create AlertDialog
        return builder.create();
    }
}
