package com.footballnewsmanager.backend.api.response.news;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.models.UserNews;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class AllNewsResponse<T extends BaseNewsAdjustment> extends NewsResponse{

    private T additionalContent;

    public T getAdditionalContent() {
        return additionalContent;
    }

    public void setAdditionalContent(T additionalContent) {
        this.additionalContent = additionalContent;
    }


}
