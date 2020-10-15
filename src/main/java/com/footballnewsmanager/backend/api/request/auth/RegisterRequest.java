package com.footballnewsmanager.backend.api.request.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


public class RegisterRequest {

    @NotBlank(message = ValidationMessage.USERNAME_NOT_BLANK)
    @Size(min = 4, max = 20, message = ValidationMessage.USERNAME_SIZE)
    private String username;

    @NotBlank(message = ValidationMessage.EMAIL_NOT_BLANK)
    @Size(max = 40, message = ValidationMessage.EMAIL_SIZE)
    @Email(message = ValidationMessage.EMAIL_VALID)
    private String email;

    @NotBlank(message = ValidationMessage.PASSWORD_NOT_BLANK)
    @Size(min = 8, max = 30, message = ValidationMessage.PASSWORD_SIZE)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}
