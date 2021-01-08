package com.footballnewsmanager.backend.helpers;

import com.footballnewsmanager.backend.api.api_sports.ApiSportCountriesResponse;
import com.footballnewsmanager.backend.api.api_sports.ApiSportTeamsResponse;
import com.footballnewsmanager.backend.api.api_sports.CountryResponse;
import com.footballnewsmanager.backend.api.api_sports.TeamResponse;
import com.footballnewsmanager.backend.api.google_translate.TranslateRequest;
import com.footballnewsmanager.backend.api.google_translate.TranslateResponse;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
public class LeaguesHelper {

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final MarkerRepository markerRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private Iterator<League> iterator;
    private Iterator<TeamResponse> teamsIterator;
    private Iterator<CountryResponse> countriesIterator;
    private WebClient apiSportWebClient;
    private WebClient googleTranslateApi;

    public LeaguesHelper(LeagueRepository leagueRepository, TeamRepository teamRepository, MarkerRepository markerRepository, UserRepository userRepository, UserTeamRepository userTeamRepository) {
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.markerRepository = markerRepository;
        this.userRepository = userRepository;
        this.userTeamRepository = userTeamRepository;
    }

    public Mono<ApiSportTeamsResponse> getTeamsFromLeague(WebClient apiSportWebClient, int leagueId) {
        return apiSportWebClient
                .get()
                .uri("/teams?league=" + leagueId + "&season=2020")
                .header("x-rapidapi-host", "v3.football.api-sports.io")
                .header("x-rapidapi-key", "4a422f46f4332a3394fe3c02bec44f94")
                .retrieve()
                .bodyToMono(ApiSportTeamsResponse.class);
    }

    public Mono<ApiSportCountriesResponse> getCountries(WebClient apiSportWebClient) {
        return apiSportWebClient
                .get()
                .uri("/countries")
                .header("x-rapidapi-host", "v3.football.api-sports.io")
                .header("x-rapidapi-key", "4a422f46f4332a3394fe3c02bec44f94")
                .retrieve()
                .bodyToMono(ApiSportCountriesResponse.class);
    }


    public Mono<TranslateResponse> getTranslationMono(WebClient googleTranslateApi, String teamNameMarker) {
        TranslateRequest translateRequest = new TranslateRequest(teamNameMarker, "en", "pl");
        return googleTranslateApi
                .post()
                .uri("")
                .bodyValue(translateRequest)
                .retrieve()
                .bodyToMono(TranslateResponse.class);
    }

    public void fetchTeamsFromTranslateApi(TeamResponse teamResponse, League league) {
        Team team = new Team();
        String teamNameMarker = teamResponse.getTeam().getName();
        if (teamNameMarker.equals("Inter"))
            teamNameMarker = "Inter ";
        Set<String> markersList = listInitWithExceptions(teamResponse);
        markersList.add(teamNameMarker);
        String finalTeamNameMarker = teamNameMarker;
        getTranslationMono(googleTranslateApi, teamNameMarker)
                .subscribe(translateResponse -> {
                    String repairedTranslationName = translateExceptionsRepair(getTranslatedText(translateResponse));
                    team.setName(!markersList.contains(repairedTranslationName) ?
                            repairedTranslationName : finalTeamNameMarker);
                    markersList.add(repairedTranslationName);
                    team.setLeague(league);
                    team.setLogoUrl(teamResponse.getTeam().getLogo());
                    teamRepository.save(team);
                    List<Marker> markers = new ArrayList<>();
                    for (String name : markersList)
                        markers.add(addMarker(name, team));
                    markerRepository.saveAll(markers);
                    connectUsersWithTeam(team);
                    if (teamsIterator.hasNext()) {
                        fetchTeamsFromTranslateApi(teamsIterator.next(), league);
                    } else if (iterator.hasNext()) {
                        fetchAnotherTeamData(iterator.next());
                    }
                });
    }


    public void fetchCountriesFromTranslateApi(CountryResponse countryResponse, League league) {
        if (!countryResponse.getName().equals("World")) {
            Team team = new Team();
            Set<String> teamMarkers = new HashSet<>();
            String teamNameMarker = countryResponse.getName();
            teamMarkers.add(teamNameMarker);
            getTranslationMono(googleTranslateApi, teamNameMarker)
                    .subscribe(translateResponse -> {
                        String repairedTranslationName = translateExceptionsRepair(getTranslatedText(translateResponse));
                        team.setName(!teamMarkers.contains(repairedTranslationName) ?
                                repairedTranslationName : teamNameMarker);
                        teamMarkers.add(repairedTranslationName);
                        if (countryResponse.getName().equals("Moldova")) {
                            teamMarkers.add("Mołdawia");
                            team.setName("Mołdawia");
                        }
                        team.setLeague(league);
                        team.setLogoUrl(countryResponse.getFlag());
                        teamRepository.save(team);
                        List<Marker> markerList = new ArrayList<>();
                        for (String name : teamMarkers)
                            markerList.add(addMarker(name, team));
                        markerRepository.saveAll(markerList);
                        connectUsersWithTeam(team);
                        if (countriesIterator.hasNext()) {
                            fetchCountriesFromTranslateApi(countriesIterator.next(), league);
                        } else if (iterator.hasNext()) {
                            fetchAnotherTeamData(iterator.next());
                        }
                    });
        } else if (countriesIterator.hasNext()) {
            fetchCountriesFromTranslateApi(countriesIterator.next(), league);
        }

    }

    public void fetchAnotherTeamData(League league) {
        if (league.getApisportid() != 0)
            getTeamsFromLeague(apiSportWebClient, league.getApisportid())
                    .subscribe(teamsResponse -> {
                                teamsIterator = teamsResponse.getResponse().iterator();
                                fetchTeamsFromTranslateApi(teamsIterator.next(), league);
                            }
                    );

        else {
            getCountries(apiSportWebClient)
                    .subscribe(countriesResponse -> {
                        countriesIterator = countriesResponse.getResponse().iterator();
                        fetchCountriesFromTranslateApi(countriesIterator.next(), league);
                    });
        }
    }


    public void updateTeams() {
        apiSportWebClient = WebClient.create("https://v3.football.api-sports.io");
        googleTranslateApi = WebClient.create("https://translation.googleapis.com/language/translate/v2?key=AIzaSyDtvSVg7RueDM_blJGdWqEb0_X9NRQV_GI");
        List<League> leagues = leagueRepository.findAll();
        teamRepository.deleteAll();
        markerRepository.deleteAll();
        iterator = leagues.iterator();
        fetchAnotherTeamData(iterator.next());
    }


    public Set<String> listInitWithExceptions(TeamResponse teamRes) {
        Set<String> markersList = new HashSet<>();
        switch (teamRes.getTeam().getName()) {
            case "Borussia Monchengladbach":
                markersList.add("Borussia Mönchengladbach");
                break;
            case "FC Rostov":
                markersList.add("FK Rostów");
                break;
            case "Paris Saint Germain":
                markersList.add("PSG");
                break;
            case "WSG Wattens":
                markersList.add("Tirol");
                break;
            case "AS Roma":
                markersList.add("Roma");
                break;
            case "AC Milan":
                markersList.add("Milan");
                break;
            case "FC Schalke 04":
                markersList.add("Schalke");
                break;
            case "Atletico Madrid":
                markersList.add("Atletico Madryt");
                break;
        }
        return markersList;
    }


    public Marker addMarker(String value, Team team) {
        Marker marker = new Marker();
        marker.setName(value);
        marker.setTeam(team);
        return marker;
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
                repairedValue = "Inter ";
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
            case "Miły":
                repairedValue = "Nice";
                break;
            case "Wilki":
                repairedValue = "Wolverhampton";
                break;
            case "Arsenał":
                repairedValue = "Arsenal";
                break;
            case "Manchester":
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

    public void connectUsersWithTeam(Team team) {
        List<User> users = userRepository.findAll();

        List<UserTeam> userTeams = new ArrayList<>();
        for (User user : users) {
            UserTeam userTeam = new UserTeam();
            userTeam.setUser(user);
            userTeam.setTeam(team);
            userTeams.add(userTeam);
        }
        userTeamRepository.saveAll(userTeams);
    }
}
