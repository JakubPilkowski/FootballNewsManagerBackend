package com.footballnewsmanager.backend.api.request.user_settings;

import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UsernameChangeRequest {
    @NotBlank(message = ValidationMessage.USERNAME_NOT_BLANK)
    @Size(min = 4, max = 20, message = ValidationMessage.USERNAME_SIZE)
    private String oldCredential;

    @NotBlank(message = ValidationMessage.USERNAME_NOT_BLANK)
    @Size(min = 4, max = 20, message = ValidationMessage.USERNAME_SIZE)
    private String newCredential;

    public String getOldCredential() {
        return oldCredential;
    }

    public String getNewCredential() {
        return newCredential;
    }
}
