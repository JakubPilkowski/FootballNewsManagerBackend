package com.footballnewsmanager.backend.api.request.auth;

import javax.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = ValidationMessage.DEFAULT_NOT_BLANK)
    private String usernameOrEmail;

    @NotBlank(message = ValidationMessage.PASSWORD_NOT_BLANK)
    private String password;

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
