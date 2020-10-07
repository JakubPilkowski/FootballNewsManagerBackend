package com.footballnewsmanager.backend.api.response.auth;

import com.footballnewsmanager.backend.api.response.BaseResponse;

public class JwtTokenResponse extends BaseResponse {

    private String accessToken;
    private String tokenType = "Bearer";

    public JwtTokenResponse(boolean success, String message, String accessToken) {
        super(success, message);
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
