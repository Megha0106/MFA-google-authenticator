package com.example.mfa_google_authenticator.entity;

public class VerifyTotpRequest {

    private String userName;
    private int code;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
