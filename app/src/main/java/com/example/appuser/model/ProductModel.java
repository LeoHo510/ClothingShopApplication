package com.example.appuser.model;

import java.util.List;

public class ProductModel {
    boolean success;
    String message;
    List<Product> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Product> getList() {
        return result;
    }

    public void setList(List<Product> list) {
        this.result = list;
    }
}
