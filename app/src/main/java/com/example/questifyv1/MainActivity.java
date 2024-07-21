package com.example.questifyv1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
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

import com.example.questifyv1.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends FragmentActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    DialogFragment dialog_add;
    Button dialog_btn_addQuest;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        replaceFragment(new HomeFragment());

        // Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.nav_add) { // Add Quest Dialog

                dialog_add = new AddQuestDialog();
                dialog_add.show(getSupportFragmentManager(),"addquest");

                //replaceFragment(new AddFragment());

                //Toast.makeText(MainActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();


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
    }
private void replaceFragment(Fragment fragment){
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.frame_layout, fragment);
    fragmentTransaction.commit();
}
}

