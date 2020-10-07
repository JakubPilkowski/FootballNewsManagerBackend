package com.footballnewsmanager.backend.api.response.auth;

public class UserError {
    private String error;
    private String errorType;

    public UserError(String error, String errorType) {
        this.error = error;
        this.errorType = errorType;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
