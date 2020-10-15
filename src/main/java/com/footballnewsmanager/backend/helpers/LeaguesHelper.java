package com.footballnewsmanager.backend.helpers;

import com.footballnewsmanager.backend.api.api_sports.ApiSportCountriesResponse;
import com.footballnewsmanager.backend.api.api_sports.ApiSportTeamsResponse;
import com.footballnewsmanager.backend.api.api_sports.CountryResponse;
import com.footballnewsmanager.backend.api.api_sports.TeamResponse;
import com.footballnewsmanager.backend.api.google_translate.TranslateRequest;
import com.footballnewsmanager.backend.api.google_translate.TranslateResponse;
import com.footballnewsmanager.backend.models.League;
import com.footballnewsmanager.backend.models.Marker;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.repositories.LeagueRepository;
import com.footballnewsmanager.backend.repositories.MarkerRepository;
import com.footballnewsmanager.backend.repositories.TeamRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LeaguesHelper {

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final MarkerRepository markerRepository;

    public LeaguesHelper(LeagueRepository leagueRepository, TeamRepository teamRepository, MarkerRepository markerRepository) {
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.markerRepository = markerRepository;
    }

    public void updateTeams() {
        WebClient apiSportWebClient = WebClient.create("https://v3.football.api-sports.io");
        WebClient googleTranslateApi = WebClient.create("https://translation.googleapis.com/language/translate/v2?key=AIzaSyDMRqEVIopgCrTZrQtomuztXr3GZx4ZrgU");
        List<League> leagues = leagueRepository.findAll();
        teamRepository.deleteAll();
        markerRepository.deleteAll();
        for (League league : leagues) {
            Marker leagueMarker = addMarker(league.getName());
            if (league.getApisportid() != 0) {
                apiSportWebClient
                        .get()
                        .uri("/teams?league=" + league.getApisportid() + "&season=2020")
                        .header("x-rapidapi-host", "v3.football.api-sports.io")
                        .header("x-rapidapi-key", "4a422f46f4332a3394fe3c02bec44f94")
                        .retrieve()
                        .bodyToMono(ApiSportTeamsResponse.class)
                        .subscribe(teamsResponse -> {
                            for (TeamResponse teamRes : teamsResponse.getResponse()) {
                                Team team = new Team();
                                Marker teamNameMarker = addMarker(teamRes.getTeam().getName());
                                Set<Marker> markersList = listInitWithExceptions(teamRes);
                                markersList.add(teamNameMarker);
                                markersList.add(leagueMarker);
                                TranslateRequest translateRequest = new TranslateRequest(teamNameMarker.getName(), "en", "pl");
                                googleTranslateApi
                                        .post()
                                        .uri("")
                                        .bodyValue(translateRequest)
                                        .retrieve()
                                        .bodyToMono(TranslateResponse.class)
                                        .subscribe(translateResponse -> {
                                            String repairedTranslationName = translateExceptionsRepair(getTranslatedText(translateResponse));
                                            if (!markerRepository.existsByName(repairedTranslationName)) {
                                                Marker polishTeamNameMarker = addMarker(repairedTranslationName);
                                                markersList.add(polishTeamNameMarker);
                                                team.setName(polishTeamNameMarker.getName());
                                            } else
                                                team.setName(teamNameMarker.getName());
                                            team.setLeague(league);
                                            team.setLogoUrl(teamRes.getTeam().getLogo());
                                            team.setMarkers(markersList);
                                            teamRepository.save(team);
                                        });
                            }
                        });
            } else {
                apiSportWebClient
                        .get()
                        .uri("/countries")
                        .header("x-rapidapi-host", "v3.football.api-sports.io")
                        .header("x-rapidapi-key", "4a422f46f4332a3394fe3c02bec44f94")
                        .retrieve()
                        .bodyToMono(ApiSportCountriesResponse.class)
                        .subscribe(countriesResponse -> {
                            for (CountryResponse countryRes : countriesResponse.getResponse()) {
                                if (!countryRes.getName().equals("World")) {
                                    Team team = new Team();
                                    Set<Marker> teamMarkers = new HashSet<>();
                                    Marker teamNameMarker = addMarker(countryRes.getName());
                                    teamMarkers.add(teamNameMarker);
                                    teamMarkers.add(leagueMarker);
                                    TranslateRequest translateRequest = new TranslateRequest(countryRes.getName(), "en", "pl");
                                    googleTranslateApi
                                            .post()
                                            .uri("")
                                            .bodyValue(translateRequest)
                                            .retrieve()
                                            .bodyToMono(TranslateResponse.class)
                                            .subscribe(translateResponse -> {
                                                String repairedTranslationName = translateExceptionsRepair(getTranslatedText(translateResponse));
                                                if (!markerRepository.existsByName(repairedTranslationName)) {
                                                    Marker polishNameMarker = addMarker(repairedTranslationName);
                                                    teamMarkers.add(polishNameMarker);
                                                    team.setName(polishNameMarker.getName());
                                                } else
                                                    team.setName(teamNameMarker.getName());
                                                if (countryRes.getName().equals("Moldova")) {
                                                    teamMarkers.add(addMarker("Mołdawia"));
                                                    team.setName("Mołdawia");
                                                }
                                                team.setLeague(league);
                                                team.setLogoUrl(countryRes.getFlag());
                                                team.setMarkers(teamMarkers);
                                                teamRepository.save(team);
                                            });

                                }
                            }
                        });
            }
        }
    }


    public Set<Marker> listInitWithExceptions(TeamResponse teamRes) {
        Set<Marker> markersList = new HashSet<>();
        switch (teamRes.getTeam().getName()) {
            case "Borussia Monchengladbach":
                markersList.add(addMarker("Borussia Mönchengladbach"));
                break;
            case "FC Rostov":
                markersList.add(addMarker("FK Rostów"));
                break;
            case "Paris Saint Germain":
                markersList.add(addMarker("PSG"));
                break;
            case "WSG Wattens":
                markersList.add(addMarker("Tirol"));
                break;
            case "AS Roma":
                markersList.add(addMarker("Roma"));
                break;
            case "AC Milan":
                markersList.add(addMarker("Milan"));
                break;
            case "FC Schalke 04":
                markersList.add(addMarker("Schalke"));
                break;
        }
        return markersList;
    }


    public Marker addMarker(String value) {
        Marker additionalMarker = new Marker();
        additionalMarker.setName(value);
        markerRepository.save(additionalMarker);
        return additionalMarker;
    }


    public String getTranslatedText(TranslateResponse translateResponse) {
        return translateResponse.getData().getTranslations().get(0).getTranslatedText();
    }


    public String translateExceptionsRepair(String translatedValue) {
        String repairedValue = "";
        switch (translatedValue) {
            case "Genua":
                repairedValue = "Genoa";
                break;
            case "Bolonia":
                repairedValue = "Bologna";
                break;
            case "Pochować":
                repairedValue = "Inter";
                break;
            case "Strasburg":
                repairedValue = "Strasbourg";
                break;
            case "Walencja":
                repairedValue = "Valencia";
                break;
            case "Obiektyw":
                repairedValue = "Lens";
                break;
            case "PFC Soczi":
                repairedValue = "PFK Soczi";
                break;
            case "bordeaux":
                repairedValue = "Bordeaux";
                break;
            case "Ładny":
                repairedValue = "Nice";
                break;
            case "Wilki":
                repairedValue = "Wolverhampton";
                break;
            case "Arsenał":
                repairedValue = "Arsenal";
                break;
            case "Machester":
                repairedValue = "Manchester City";
                break;
            case "Kryształowy Pałac":
                repairedValue = "Crystal Palace";
                break;
            case "Szybki Wiedeń":
                repairedValue = "Rapid Wiedeń";
                break;
            case "Wcierać":
                repairedValue = "Rubin Kazań";
                break;
            case "Święta Clara":
                repairedValue = "Santa Clara";
                break;
            case "Pan":
                repairedValue = "Gent";
                break;
            case "Republika Czeska":
                repairedValue = "Czechy";
                break;
            case "Klub atletyczny":
                repairedValue = "Athletic Bilbao";
                break;
            case "Monako":
                repairedValue = "Monaco";
                break;
            case "indyk":
                repairedValue = "Turcja";
                break;
            case "Chińsko-Tajpej":
                repairedValue = "Chińskie Tajpej";
                break;
            default:
                repairedValue = translatedValue;
        }
        return repairedValue;
    }
}
