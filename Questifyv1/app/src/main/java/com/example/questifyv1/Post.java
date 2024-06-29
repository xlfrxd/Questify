package com.example.questifyv1;

public class Post {
    private String title;
    private String dueDate;
    private String username;
    private int price;
    private int imageResource;

    public Post(String title, String dueDate, String username, int price, int imageResource) {
        this.title = title;
        this.dueDate = dueDate;
        this.username = username;
        this.price = price;
        this.imageResource = imageResource;
    }

    public String getTitle() { return title; }
    public String getDueDate() { return dueDate; }
    public String getUsername() { return username; }
    public int getPrice() { return price; }
    public int getImageResource() { return imageResource; }
}
