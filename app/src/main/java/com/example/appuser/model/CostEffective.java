package com.example.appuser.model;

public class CostEffective {
    int id;
    String image_url;
    String title;
    String content;

    public CostEffective(int id, String image_url, String title, String content) {
        this.id = id;
        this.image_url = image_url;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
