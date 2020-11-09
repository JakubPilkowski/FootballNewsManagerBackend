package com.footballnewsmanager.backend.api.request.teams;

import com.footballnewsmanager.backend.models.Tag;

import java.util.List;

public class TeamsFromTagsRequest {

    private List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }
}
