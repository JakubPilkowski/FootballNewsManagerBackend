package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.api_sports.ApiSportCountriesResponse;
import com.footballnewsmanager.backend.api.api_sports.ApiSportTeamsResponse;
import com.footballnewsmanager.backend.api.api_sports.CountryResponse;
import com.footballnewsmanager.backend.api.api_sports.TeamResponse;
import com.footballnewsmanager.backend.api.google_translate.TranslateRequest;
import com.footballnewsmanager.backend.api.google_translate.TranslateResponse;
import com.footballnewsmanager.backend.api.in_fakt.CountriesResponse;
import com.footballnewsmanager.backend.api.in_fakt.CountriesResponseEntity;
import com.footballnewsmanager.backend.api.response.TeamsResponse;
import com.footballnewsmanager.backend.models.League;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.Marker;
import com.footballnewsmanager.backend.repositories.LeagueRepository;
import com.footballnewsmanager.backend.repositories.TeamRepository;
import com.footballnewsmanager.backend.repositories.MarkerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@RestController
@RequestMapping("/teams")
public class TeamsController {


    TeamRepository teamRepository;
    LeagueRepository leagueRepository;
    MarkerRepository markerRepository;

    public TeamsController(TeamRepository teamRepository, LeagueRepository leagueRepository,
                           MarkerRepository markerRepository) {
        this.teamRepository = teamRepository;
        this.leagueRepository = leagueRepository;
        this.markerRepository = markerRepository;
    }

    @GetMapping("/tmp")
    public String createTeamTmp() {
        Team team = new Team();
        Optional<League> league = leagueRepository.findByApisportid(61);
        Set<Marker> MarkersList = new HashSet<>();
        Marker teamNameMarker = new Marker();
        teamNameMarker.setName("PSG");
//        teamNameMarker.setTeam(team);
        Marker teamLigueMarker = new Marker();
        teamLigueMarker.setName("Ligue 1");
//        teamLigueMarker.setTeam(team);
//        MarkersList.add(teamNameMarker);
//        MarkersList.add(teamLigueMarker);
        league.ifPresent(team::setLeague);
        team.setName("PSG");
        team.setLogoUrl("jakieś logo");
        MarkersList.add(teamNameMarker);
        MarkersList.add(teamLigueMarker);
        team.setMarkers(MarkersList);
//        team.setMarkers(MarkersList);
        markerRepository.saveAll(MarkersList);
        teamRepository.save(team);

//        teamNameMarker.setTeam(team);
//        teamLigueMarker.setTeam(team);

        return "success";
    }

    @GetMapping("/tmp2")
    public String createTeamTmp2() {
        Team team = new Team();
        Optional<League> league = leagueRepository.findByApisportid(61);
        Set<Marker> markersList = new HashSet<>();
        Marker teamNameMarker = new Marker();
        teamNameMarker.setName("Marsylia");
//        teamNameMarker.setTeam(team);
        Optional<Marker> teamLigueMarker = markerRepository.findByName("Ligue 1");
//        teamLigueMarker.setTeam(team);
//        MarkersList.add(teamNameMarker);
//        MarkersList.add(teamLigueMarker);
        league.ifPresent(team::setLeague);
        team.setName("Marsylia");
        team.setLogoUrl("jakieś logo 2");
        markersList.add(teamNameMarker);
        teamLigueMarker.ifPresent(markersList::add);
        team.setMarkers(markersList);
//        team.setMarkers(MarkersList);
        markerRepository.save(teamNameMarker);
        teamRepository.save(team);

//        teamNameMarker.setTeam(team);
//        teamLigueMarker.setTeam(team);

        return "success";
    }


    @GetMapping("")
    public ResponseEntity<TeamsResponse> teams() {


        TeamsResponse teamsResponse = new TeamsResponse(true, "Drużyny", teamRepository.findAll());

        return ResponseEntity.ok().body(teamsResponse);
    }

    @GetMapping("/tmpCreateTeam")
    public String createTeam() {
        WebClient apiSportWebClient = WebClient.create("https://v3.football.api-sports.io");
        WebClient googleTranslateApi = WebClient.create("https://translation.googleapis.com/language/translate/v2?key=AIzaSyDMRqEVIopgCrTZrQtomuztXr3GZx4ZrgU");
        List<League> leagues = leagueRepository.findAll();
        teamRepository.deleteAll();
        markerRepository.deleteAll();
        for (League league : leagues) {
            Marker leagueMarker = new Marker();
            leagueMarker.setName(league.getName());
            markerRepository.save(leagueMarker);
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
                                Set<Marker> markersList = new HashSet<>();
                                Marker teamNameMarker = new Marker();
                                teamNameMarker.setName(teamRes.getTeam().getName());
                                switch (teamRes.getTeam().getName()) {
                                    case "Borussia Monchengladbach":
                                        markersList.add(additionalMarker("Borussia Mönchengladbach"));
                                        break;
                                    case "FC Rostov":
                                        markersList.add(additionalMarker("FK Rostów"));
                                        break;
                                    case "Paris Saint Germain":
                                        markersList.add(additionalMarker("PSG"));
                                        break;
                                    case "AS Roma":
                                        markersList.add(additionalMarker("Roma"));
                                        break;
                                    case "AC Milan":
                                        markersList.add(additionalMarker("Milan"));
                                        break;
                                    case "FC Schalke 04":
                                        markersList.add(additionalMarker("Schalke"));
                                        break;
                                }
                                markersList.add(teamNameMarker);
                                markersList.add(leagueMarker);
                                markerRepository.save(teamNameMarker);
                                TranslateRequest translateRequest = new TranslateRequest(teamNameMarker.getName(), "en", "pl");
                                googleTranslateApi
                                        .post()
                                        .uri("")
                                        .bodyValue(translateRequest)
                                        .retrieve()
                                        .bodyToMono(TranslateResponse.class)
                                        .subscribe(translateResponse -> {
                                            String polishLeagueTeamName = translateResponse.getData().getTranslations().get(0).getTranslatedText();
                                            String repairedTranslationName = translateExceptionsRepair(polishLeagueTeamName);
                                            if (!markerRepository.existsByName(repairedTranslationName)) {
                                                Marker polishTeamNameMarker = new Marker();
                                                polishTeamNameMarker.setName(repairedTranslationName);
                                                markersList.add(polishTeamNameMarker);
                                                markerRepository.save(polishTeamNameMarker);
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
                                    Marker teamNameMarker = new Marker();
                                    teamNameMarker.setName(countryRes.getName());
                                    markerRepository.save(teamNameMarker);
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
                                                String polishCountryName = translateResponse.getData().getTranslations().get(0).getTranslatedText();
                                                String repairedTranslationName = translateExceptionsRepair(polishCountryName);
                                                if (!markerRepository.existsByName(repairedTranslationName)) {
                                                    Marker polishNameMarker = new Marker();
                                                    polishNameMarker.setName(repairedTranslationName);
//                                                    polishNameMarker.setName(!polishCountryName.equals("indyk") ? polishCountryName : "Turcja");
                                                    teamMarkers.add(polishNameMarker);
                                                    markerRepository.save(polishNameMarker);
                                                    team.setName(polishNameMarker.getName());
                                                } else
                                                    team.setName(teamNameMarker.getName());
                                                if (countryRes.getName().equals("Moldova")){
                                                        teamMarkers.add(additionalMarker("Mołdawia"));
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

        return "success";
    }

    public Marker additionalMarker(String value){
        Marker additionalMarker = new Marker();
        additionalMarker.setName(value);
        markerRepository.save(additionalMarker);
        return additionalMarker;
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
            case "Zespół Wiener Linien":
                repairedValue = "Team Wiener Linien";
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
