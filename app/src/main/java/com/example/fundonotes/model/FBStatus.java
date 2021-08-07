package com.example.fundonotes.model;

public class FBStatus {
    UserDetails fbUserDetails;
    boolean fbUserStatus;

    public FBStatus(UserDetails fbUserDetails, boolean fbUserStatus) {
        this.fbUserDetails = fbUserDetails;
        this.fbUserStatus = fbUserStatus;
    }

    public UserDetails getFbUserDetails() {
        return fbUserDetails;
    }

    public void setFbUserDetails(UserDetails fbUserDetails) {
        this.fbUserDetails = fbUserDetails;
    }

    public boolean getFbUserStatus() {
        return fbUserStatus;
    }

    public void setFbUserStatus(boolean fbUserStatus) {
        this.fbUserStatus = fbUserStatus;
    }
}
