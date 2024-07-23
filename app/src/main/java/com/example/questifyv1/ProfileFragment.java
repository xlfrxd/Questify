package com.example.questifyv1;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.questifyv1.activity.SignInActivity;
import com.example.questifyv1.database.UserDatabaseHandler;

import java.text.DecimalFormat;

public class ProfileFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_SESSION = "userSession";

    private String userName;
    private String userFullName;
    private double userWallet;
    private String userEmail;
    private String userPassword;
    private TextView tvProfileTitle;
    private TextView tvWallet;

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



        // Get user info from db
        String[] userInfo;
        UserDatabaseHandler userDB = new UserDatabaseHandler(getActivity());
        userInfo = userDB.getUserInfo(userName);
        // Store to local variables
        userFullName = userInfo[0];
        userName = userInfo[1];
        userWallet = Integer.parseInt(userInfo[2]);
        userEmail = userInfo[3];
        userPassword = userInfo[4];

        // TODO: Display user information onto widgets
        // Initialize widgets
        tvProfileTitle = view.findViewById(R.id.tvProfileTitle);
        tvWallet = view.findViewById(R.id.tvWallet);
        // Display Wallet Balance (wallet)
        tvWallet.setText(String.format("₱%.2f", userWallet));
        // Display Profile Title (username)
        tvProfileTitle.setText(userName);

        // Logout Button
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout
                Toast.makeText(getActivity(), "Logging out...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
    });

}
}
