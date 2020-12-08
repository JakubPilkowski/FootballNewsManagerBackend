package com.footballnewsmanager.backend.api.response.news;

import com.footballnewsmanager.backend.api.response.BaseResponse;

public class BadgesResponse extends BaseResponse {

    private Long amount;

    public BadgesResponse(boolean success, String message, Long amount) {
        super(success, message);
        this.amount = amount;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
