package com.footballnewsmanager.backend.api.request.auth;

public class ValidationMessage {

    public static final String DEFAULT_NOT_BLANK = "Pole jest wymagane";
    public static final String EMAIL_NOT_BLANK = "Adres mailowy jest wymagany";
    public static final String USERNAME_NOT_BLANK = "Nazwa użytkownika jest wymagana";
    public static final String PASSWORD_NOT_BLANK = "Hasło jest wymagane";
    public static final String EMAIL_VALID = "Niepoprawny adres mailowy";
    public static final String EMAIL_SIZE = "Email nie może składać się z więcej niż 40 znaków";
    public static final String USERNAME_SIZE = "Login musi zawierać od 4 do 20 znaków";
    public static final String PASSWORD_SIZE = "Hasło musi zawierać od 8 do 30 znaków";
    public static final String TOKEN_NOT_BLANK = "Token nie może być pusty";
    public static final String ADMIN_GRANTED = "Wartość nie może być pusta";
    public static final String PROPOSED_NEWS_NOT_BLANK = "Pole polecane wiadomości nie może być puste";
    public static final String LEAGUE_NAME_NOT_BLANK = "Nazwa ligi nie może być pusta";
    public static final String LEAGUE_NAME_SIZE = "Nazwa ligi musi zawierać od 5 do 50 znaków";
    public static final String LOGO_NOT_BLANK = "Pole logo nie może być puste";
    public static final String IMAGE_NOT_BLANK = "Pole zdjęcie nie może być puste";
    public static final String LEAGUE_TYPE_NOT_BLANK = "Pole typ ligi nie może być pusty";
    public static final String API_SPORT_ID_NOT_BLANK = "ID ligi z api sport nie może być puste";
    public static final String MARKER_NAME_NOT_BLANK = "Nazwa markera nie może być pusta";
    public static final String NEWS_TITLE_NOT_BLANK = "Tytuł wiadomości nie może być pusty";
    public static final String NEWS_TITLE_SIZE = "Tytuł wiadomości musi zawierać od 5 do 100 znaków";
    public static final String NEWS_URL_NOT_BLANK = "Link nie może być pusty";
    public static final String DATE_NOT_BLANK = "Data nie może być pusta";
    public static final String ROLE_NOT_BLANK = "Pole rola nie może być pusta";
    public static final String SITE_NAME_NOT_BLANK = "Nazwa strona nie może być pusta";
    public static final String SITE_NAME_SIZE = "Nazwa strony musi zawierać od 4 do 100 znaków";
    public static final String SITE_DESCRIPTION_NOT_BLANK = "Opis strony nie może być pusty";
    public static final String SITE_DESCRIPTION_SIZE = "Opis strony musi zawierać od 10 do 150 znaków";
    public static final String CLICKS_NOT_BLANK = "Kliknięcia nie mogą być wartością pustą";
    public static final String TAG_NAME_NOT_BLANK = "Nazwa tagu nie może być pusta";
    public static final String TEAM_NAME_NOT_BLANK = "Nazwa drużyny nie może być pusta";
    public static final String TEAM_NAME_SIZE = "Nazwa drużyny musi zawierać od 3 do 50 znaków";
    public static final String CLICKS_LESS_THAN_ZERO = "Kliknięcia nie mogą być wartością ujemną";
    public static final String ID_NOT_NULL = "Id nie może być puste";
    public static final String ID_LESS_THAN_ZERO = "Id nie może być wartością ujemną";
    public static final String POPULARITY_LESS_THAN_ZERO = "Popularność nie może być wartością pustą";
    public static final String SITE_LANGUAGE_NOT_BLANK = "Strona musi zawierać język";
    public static final String SITE_LANGUAGE_SIZE = "Język strony musi zawierać od 2 do 80 znaków";
}
