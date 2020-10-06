package com.footballnewsmanager.backend.api.response.sites;

public class SiteWithClicks {


    private Long id;
    private String name;
    private String logoUrl;
    private boolean highlighted;
    private int clicks;

    public SiteWithClicks(Long id, String name, String logoUrl, boolean highlighted, int clicks) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.highlighted = highlighted;
        this.clicks = clicks;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public Long getId() {
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
