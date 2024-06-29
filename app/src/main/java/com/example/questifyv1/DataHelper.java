package com.example.questifyv1;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {

    public static List<Post> getPostData() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post("Buy Me Exam Booklet", "Due Today at 3:00pm", "@itskokel", 200, R.drawable.physical));
        posts.add(new Post("Categorize My Emails", "Due Today at 7:00am", "@xlfrxd", 300, R.drawable.laptop));
        posts.add(new Post("Fix My Cellphone", "Due Today at 5:00pm", "@lsakyaki", 900, R.drawable.briefcase));
        posts.add(new Post("Create Logo For My Brand", "Due Today at 9:00am", "@caicai", 2000, R.drawable.idea));
        posts.add(new Post("Buy Me Exam Booklet", "Due Today at 3:00pm", "@itskokel", 200, R.drawable.physical));
        posts.add(new Post("Categorize My Emails", "Due Today at 7:00am", "@xlfrxd", 300, R.drawable.laptop));
        posts.add(new Post("Fix My Cellphone", "Due Today at 5:00pm", "@lsakyaki", 900, R.drawable.briefcase));
        posts.add(new Post("Create Logo For My Brand", "Due Today at 9:00am", "@caicai", 2000, R.drawable.idea));
        return posts;
    }
}

