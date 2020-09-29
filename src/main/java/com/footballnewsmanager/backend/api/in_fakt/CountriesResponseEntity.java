package com.footballnewsmanager.backend.api.in_fakt;

public class CountriesResponseEntity {
    private int id;
    private String alpha_2;
    private String alpha_3;
    private String english_name;
    private String polish_name;
    private String continent;
    private boolean european_union;
    private String currency;

    public int getId() {
        return id;
    }

    public String getAlpha_2() {
        return alpha_2;
    }

    public String getAlpha_3() {
        return alpha_3;
    }

    public String getEnglish_name() {
        return english_name;
    }

    public String getPolish_name() {
        return polish_name;
    }

    public String getContinent() {
        return continent;
    }

    public boolean isEuropean_union() {
        return european_union;
    }

    public String getCurrency() {
        return currency;
    }
}
