package com.example.questifyv1.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.questifyv1.R;
import com.example.questifyv1.activity.MainActivity;
import com.example.questifyv1.activity.SignInActivity;
import com.example.questifyv1.activity.SplashActivity;
import com.example.questifyv1.database.QuestContract;
import com.example.questifyv1.database.QuestsDatabaseHandler;
import com.example.questifyv1.database.UserDatabaseHandler;
import com.example.questifyv1.dialog.CashInDialog;
import com.example.questifyv1.dialog.WithdrawDialog;

import java.util.Timer;
import java.util.TimerTask;

public class ProfileFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_SESSION = "userSession";

    private String userName;
    private String userFullName;
    private double userWallet;
    private String questsCompleted;
    private String questsPosted;
    private TextView tvProfileTitle;
    private TextView tvWallet;
    private TextView tvProfileSubtitle;
    private TextView tvCompletedQuests;
    private TextView tvPostedQuests;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);


        if (getArguments() != null) {
            userName = getArguments().getString(USER_SESSION);
        }
        // For debugging
        //Toast.makeText(getActivity(), "Profile created for: " + userSession, Toast.LENGTH_SHORT).show();

        // Get full name, username, email, password and wallet balance from db
        String[] userInfo;
        UserDatabaseHandler userDB = new UserDatabaseHandler(getActivity());
        userInfo = userDB.getUserInfo(userName);
        // Store to local variables
        userFullName = userInfo[0];
        userName = userInfo[1];
        userWallet = Double.parseDouble(userInfo[2]);
        String userEmail = userInfo[3];
        String userPassword = userInfo[4];

        // Get quest posted and quests taken
        QuestsDatabaseHandler questDB = new QuestsDatabaseHandler(getActivity());
        SQLiteDatabase questDBHelper = questDB.getReadableDatabase();

        String[] projection = {
                QuestContract.QuestEntry._ID,
                QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY
        };
        String selection = QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY + " = ?";
        String[] selectionArgs = {userName};

        // Initialize count for postedBy
        questsPosted = "0";

        try {
            // SELECT posts WHERE postedBy = {currentUser}
            Cursor cursor = questDBHelper.query(
                    QuestContract.QuestEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            questsPosted = String.valueOf(cursor.getCount());
        }
        catch (Exception e){
            Log.e("questsPosted", e.toString());
        }

        // New projection for getting status
        projection = new String[]{
                QuestContract.QuestEntry._ID,
                QuestContract.QuestEntry.COLUMN_NAME_STATUS,
                QuestContract.QuestEntry.COLUMN_NAME_POSTEDBY
        };

        // WHERE status = DONE
        selection = QuestContract.QuestEntry.COLUMN_NAME_STATUS + " = ? AND " + QuestContract.QuestEntry.COLUMN_NAME_DIBSBY + " = ?";
        selectionArgs = new String[]{"DONE", userName};

        // Initialize count for questsCompleted
        questsCompleted = "0";

        try {

            // SELECT posts WHERE status = DONE
            Cursor cursor = questDBHelper.query(
                    QuestContract.QuestEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            questsCompleted = String.valueOf(cursor.getCount());
            Log.e("questsCompleted",questsCompleted);
        }
        catch (Exception e) {
            Log.e("questsCompleted", e.toString());
        }

        // Handle counter values
        // Quests Posted
        switch (questsPosted){
            case "0":
                    questsPosted = "No Quests Posted";
                break;
            case "1":
                    questsPosted = questsPosted + " Quest Posted";
                break;
            default:
                    questsPosted = questsPosted + " Quests Posted";
                break;
        }
        // Quests Completed
        switch (questsCompleted){
            case "0":
                questsCompleted = "No Quests Completed";
                break;
            case "1":
                questsCompleted = questsCompleted + " Quest Completed";
                break;
            default:
                questsCompleted = questsCompleted + " Quests Completed";
                break;
        }


        // TODO: Display user information onto widgets
        // Initialize widgets
        tvProfileTitle = view.findViewById(R.id.tvProfileTitle);
        tvWallet = view.findViewById(R.id.tvWallet);
        tvProfileSubtitle = view.findViewById(R.id.tvProfileSubtitle);
        tvPostedQuests = view.findViewById(R.id.tvQuestsPostedCount);
        tvCompletedQuests = view.findViewById(R.id.tvQuestsCompletedCount);

        // Display widgets
        // Display Wallet Balance (wallet)
        tvWallet.setText(String.format("₱%.2f", userWallet));
        // Display Profile Title (full name)
        tvProfileTitle.setText(userFullName);
        // Display Profile Subtitle (username)
        tvProfileSubtitle.setText("@"+userName);
        // Display Posted Quests
        tvPostedQuests.setText(questsPosted);
        // Display Completed Quests
        tvCompletedQuests.setText(questsCompleted);

        // Buttons
        // Cash In Button
        Button btnOpenCashIn = view.findViewById(R.id.cashin_button);
        btnOpenCashIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open cash in dialog
                DialogFragment dialog_deposit = new CashInDialog();
                // Create bundle to send userWallet to cashin/withdraw
                Bundle args = new Bundle();
                args.putDouble("userWallet",userWallet);
                dialog_deposit.setArguments(args); // Send user current wallet balance
                // TODO: Update balance via mainAcitivty.updateBalance("deposit", balance)

                dialog_deposit.show(getActivity().getSupportFragmentManager(), "deposit");

            }
        });

        // Withdraw Button
        Button btnOpenWithdraw = view.findViewById(R.id.send_button);
        btnOpenWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the withdraw dialog
                DialogFragment dialog_withdraw = new WithdrawDialog();
                Bundle args = new Bundle();
                args.putDouble("userWallet",userWallet);
                dialog_withdraw.setArguments(args); // Send user current wallet balance

                // TODO: Update balance via mainAcitivty.updateBalance("withdraw", balance)
                dialog_withdraw.show(getActivity().getSupportFragmentManager(), "withdraw");
            }
        });

        // Logout Button
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout

                final Handler handler = new Handler();
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                // Notify user of change
                                Toast.makeText(getActivity(), "Logging out...", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), SplashActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }, 1500);
            }
        });


}
}
