package com.footballnewsmanager.backend.api.response.auth;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public class ArgumentNotValidResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Map<String, String> messages;

    public ArgumentNotValidResponse(LocalDateTime timestamp, int status, String error, Map<String, String> messages) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.messages = messages;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Map<String, String> getMessages() {
        return messages;
    }
}
