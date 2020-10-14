package com.footballnewsmanager.backend.api.request.user_settings;

import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PasswordChangeRequest {

    @NotBlank(message = ValidationMessage.PASSWORD_NOT_BLANK)
    @Size(min = 8, max = 30, message = ValidationMessage.PASSWORD_SIZE)
    private String oldCredential;

    @NotBlank(message = ValidationMessage.PASSWORD_NOT_BLANK)
    @Size(min = 8, max = 30, message = ValidationMessage.PASSWORD_SIZE)
    private String newCredential;

    public String getOldCredential() {
        return oldCredential;
    }

    public String getNewCredential() {
        return newCredential;
    }
}
