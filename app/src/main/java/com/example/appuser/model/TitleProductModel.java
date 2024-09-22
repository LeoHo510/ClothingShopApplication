package com.example.appuser.model;

import java.util.List;

public class TitleProductModel {
    boolean success;
    String message;
    List<TitleProduct> result;

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

    public List<TitleProduct> getList() {
        return result;
    }

    public void setList(List<TitleProduct> list) {
        this.result = list;
    }
}
