package com.footballnewsmanager.backend.api.request.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = ValidationMessage.TOKEN_NOT_BLANK)
    private String token;

    @NotBlank(message = ValidationMessage.PASSWORD_NOT_BLANK)
    @Size(min = 8, max = 30, message = ValidationMessage.PASSWORD_SIZE)
    private String password;

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }
}
