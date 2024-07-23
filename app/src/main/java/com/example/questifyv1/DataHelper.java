package com.example.questifyv1;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {

        private static List<Post> posts = new ArrayList<>();
    public static List<Post> getPostData() {
        /*
        posts.add(new Post("Buy Me Exam Booklet", "Due Today at 3:00pm", "@itskokel", 200, R.drawable.physical));
        posts.add(new Post("Categorize My Emails", "Due Today at 7:00am", "@xlfrxd", 300, R.drawable.laptop));
        posts.add(new Post("Fix My Cellphone", "Due Today at 5:00pm", "@lsakyaki", 900, R.drawable.briefcase));
        posts.add(new Post("Create Logo For My Brand", "Due Today at 9:00am", "@caicai", 2000, R.drawable.idea));
        posts.add(new Post("Buy Me Exam Booklet", "Due Today at 3:00pm", "@itskokel", 200, R.drawable.physical));
        posts.add(new Post("Categorize My Emails", "Due Today at 7:00am", "@xlfrxd", 300, R.drawable.laptop));
        posts.add(new Post("Fix My Cellphone", "Due Today at 5:00pm", "@lsakyaki", 900, R.drawable.briefcase));
        posts.add(new Post("Create Logo For My Brand", "Due Today at 9:00am", "@caicai", 2000, R.drawable.idea));
        */
        return posts;

    }

    public static void addPost(String title, String dueDate, String author, String points, String category, String desc, String dibsBy){
        posts.add(new Post(title, dueDate, author, points, getCategoryImage(category), desc, dibsBy));
    }

    public static void clear(){
        posts.clear();
    }

    public static int getCategoryImage(String category){
        switch (category){
            case "Physical":
                return R.drawable.physical;
            case "Digital":
                return R.drawable.laptop;
            case "Professional":
                return R.drawable.briefcase;
            case "Creative":
                return R.drawable.idea;
            default:
                return R.drawable.physical;
        }
    }
}

