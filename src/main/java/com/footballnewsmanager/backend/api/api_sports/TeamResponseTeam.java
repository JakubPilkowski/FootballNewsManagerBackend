package com.footballnewsmanager.backend.api.api_sports;

public class TeamResponseTeam {
    private int id;
    private String name;
    private String country;
    private int founded;
    private boolean national;
    private String logo;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public int getFounded() {
        return founded;
    }

    public boolean isNational() {
        return national;
    }

    public String getLogo() {
        return logo;
    }
}
