package com.example.appuser.model;

import java.util.List;

public class AdsModel {
    boolean success;
    String message;
    List<Ads> result;

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

    public List<Ads> getList() {
        return result;
    }

    public void setList(List<Ads> list) {
        this.result = list;
    }
}
