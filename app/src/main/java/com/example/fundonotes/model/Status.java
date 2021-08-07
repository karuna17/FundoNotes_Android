package com.example.fundonotes.model;

public class Status {
    //private int id;
    // private String token;
    private boolean status;
    private String message;

    public Status(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status() {
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
