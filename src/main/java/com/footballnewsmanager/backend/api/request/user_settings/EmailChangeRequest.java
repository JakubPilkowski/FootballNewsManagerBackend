package com.footballnewsmanager.backend.api.request.user_settings;


import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class EmailChangeRequest {
    @NotBlank(message = ValidationMessage.EMAIL_NOT_BLANK)
    @Email(message = ValidationMessage.EMAIL_VALID)
    @Size(max = 40, message = ValidationMessage.EMAIL_SIZE)
    private String oldCredential;

    @NotBlank(message = ValidationMessage.EMAIL_NOT_BLANK)
    @Email(message = ValidationMessage.EMAIL_VALID)
    @Size(max = 40, message = ValidationMessage.EMAIL_SIZE)
    private String newCredential;

    public String getOldCredential() {
        return oldCredential;
    }

    public String getNewCredential() {
        return newCredential;
    }
}
