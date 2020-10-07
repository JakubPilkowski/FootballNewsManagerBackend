package com.footballnewsmanager.backend.api.response.auth;

import com.footballnewsmanager.backend.api.response.BaseResponse;

import java.util.List;

public class BadCredentialsResponse extends BaseResponse {

    private List<UserError> userErrorList;

    public BadCredentialsResponse(boolean success, String message, List<UserError> userErrorList) {
        super(success, message);
        this.userErrorList = userErrorList;
    }

    public List<UserError> getErrorList() {
        return userErrorList;
    }
}
