package com.example.appuser.model;

import java.util.List;

public class CostEffectiveModel {
    boolean success;
    String message;
    List<CostEffective> result;

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

    public List<CostEffective> getList() {
        return result;
    }

    public void setList(List<CostEffective> list) {
        this.result = list;
    }
}
