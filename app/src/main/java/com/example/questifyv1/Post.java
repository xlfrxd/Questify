package com.example.questifyv1;

public class Post {
    private String title;
    private String dueDate;
    private String username;
    private String price;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDibsBy() {
        return dibsBy;
    }

    public void setDibsBy(String dibsBy) {
        this.dibsBy = dibsBy;
    }

    private String dibsBy;

    private int imageResource;

    public Post(String title, String dueDate, String username, String price, int imageResource, String desc, String dibsBy) {
        this.title = title;
        this.dueDate = dueDate;
        this.username = username;
        this.price = price;
        this.imageResource = imageResource;
        this.desc = desc;
        this.dibsBy = dibsBy;
    }

    public String getTitle() { return title; }
    public String getDueDate() { return dueDate; }
    public String getUsername() { return username; }
    public String getPrice() { return price; }
    public int getImageResource() { return imageResource; }
}
