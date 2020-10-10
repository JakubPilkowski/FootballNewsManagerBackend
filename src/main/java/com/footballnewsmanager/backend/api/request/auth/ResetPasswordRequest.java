package com.footballnewsmanager.backend.api.request.auth;

public class ResetPasswordRequest {

    private String token;

    private String password;


    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }
}
