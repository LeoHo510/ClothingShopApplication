package com.example.appuser.model;

import java.util.List;

public class TitleProduct {
    private String title;
    private List<Product> list;

    public TitleProduct(String title, List<Product> list) {
        this.title = title;
        this.list = list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Product> getList() {
        return list;
    }

    public void setList(List<Product> list) {
        this.list = list;
    }
}
