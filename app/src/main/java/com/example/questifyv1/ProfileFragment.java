package com.example.questifyv1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_SESSION = "userSession";

    private String userSession;
    private String userFullName;
    private TextView tvProfileTitle;

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
            userSession = getArguments().getString(USER_SESSION);
        }
        // For debugging
        //Toast.makeText(getActivity(), "Profile created for: " + userSession, Toast.LENGTH_SHORT).show();

        // TODO: Get user info from db

        tvProfileTitle = (TextView) view.findViewById(R.id.tvProfileTitle);
        // Display Profile Title to Full Name
        this.tvProfileTitle.setText(userFullName);
        tvProfileTitle.setText(userSession);
    }
}