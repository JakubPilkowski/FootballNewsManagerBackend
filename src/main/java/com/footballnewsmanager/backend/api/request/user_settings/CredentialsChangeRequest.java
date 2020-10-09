package com.footballnewsmanager.backend.api.request.user_settings;

public class CredentialsChangeRequest {

    private String oldCredential;

    private String newCredential;

    public String getOldCredential() {
        return oldCredential;
    }

    public String getNewCredential() {
        return newCredential;
    }
}
