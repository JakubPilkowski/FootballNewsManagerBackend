package com.footballnewsmanager.backend.api.response.search;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.models.SearchResult;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class SearchResponse {
     private  List<SearchResult> results;

    public SearchResponse(List<SearchResult> results) {
        this.results = results;
    }

    public List<SearchResult> getResults() {
        return results;
    }
}
