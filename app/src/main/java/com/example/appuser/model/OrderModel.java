package com.example.appuser.model;

import java.util.List;

public class OrderModel {
    boolean success;
    String message;
    List<Order> result;

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

    public List<Order> getList() {
        return result;
    }

    public void setList(List<Order> list) {
        this.result = list;
    }
}
