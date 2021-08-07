package com.example.fundonotes.model;

public class UserDetails {
    private String email, password;
    private String url;

    public UserDetails(String email, String password, String url) {
        this.email = email;
        this.password = password;
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
