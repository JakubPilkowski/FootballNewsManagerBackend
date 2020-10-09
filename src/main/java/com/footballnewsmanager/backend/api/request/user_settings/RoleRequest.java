package com.footballnewsmanager.backend.api.request.user_settings;

import com.footballnewsmanager.backend.models.RoleName;
import com.footballnewsmanager.backend.models.User;

public class RoleRequest {

    private boolean isAdminGranted;

    public boolean isAdminGranted() {
        return isAdminGranted;
    }
}
