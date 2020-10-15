package com.footballnewsmanager.backend.api.request.user_settings;

import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.models.RoleName;
import com.footballnewsmanager.backend.models.User;

import javax.validation.constraints.NotBlank;

public class RoleRequest {

    @NotBlank(message = ValidationMessage.ADMIN_GRANTED)
    private boolean isAdminGranted;

    public boolean isAdminGranted() {
        return isAdminGranted;
    }
}
