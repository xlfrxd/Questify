package com.example.questifyv1.post;

public class Post {
    private String title;
    private String dueDate;
    private String username;
    private String price;
    private String desc;
    private String dibsBy;
    private int imageResource;
    private String status;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


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


    public Post(String title, String dueDate, String username, String price, int imageResource, String desc, String dibsBy, String status) {
        this.title = title;
        this.dueDate = dueDate;
        this.username = username;
        this.price = price;
        this.imageResource = imageResource;
        this.desc = desc;
        this.dibsBy = dibsBy;
        this.status = status;
    }

    public String getTitle() { return title; }
    public String getDueDate() { return dueDate; }
    public String getUsername() { return username; }
    public String getPrice() { return price; }
    public int getImageResource() { return imageResource; }
}
