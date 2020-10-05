package com.footballnewsmanager.backend.api.response.sites;

public class SiteWithClicks {


    private int id;
    private String name;
    private String logoUrl;
    private boolean highlighted;
    private int clicks;

    public SiteWithClicks(int id, String name, String logoUrl, boolean highlighted, int clicks) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.highlighted = highlighted;
        this.clicks = clicks;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public int getClicks() {
        return clicks;
    }
}
