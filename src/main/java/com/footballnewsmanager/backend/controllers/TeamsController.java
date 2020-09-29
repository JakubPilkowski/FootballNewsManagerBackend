package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.api_sports.TeamResponse;
import com.footballnewsmanager.backend.api.api_sports.TeamsResponse;
import com.footballnewsmanager.backend.api.in_fakt.CountriesResponse;
import com.footballnewsmanager.backend.api.in_fakt.CountriesResponseEntity;
import com.footballnewsmanager.backend.models.League;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.Marker;
import com.footballnewsmanager.backend.repositories.LeagueRepository;
import com.footballnewsmanager.backend.repositories.TeamRepository;
import com.footballnewsmanager.backend.repositories.MarkerRepository;
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


    @GetMapping("/tmpCreateTeam")
    public String createTeam() {
        WebClient apiSportWebClient = WebClient.create("https://v3.football.api-sports.io");
        WebClient inFaktWebClient = WebClient.create("https://api.infakt.pl/v3");
        List<League> leagues = leagueRepository.findAll();
        teamRepository.deleteAll();
        markerRepository.deleteAll();
//        TeamMarkers leagueMarker = new TeamMarkers();
//        Optional<League> league1 = leagueRepository.findByApisportid(61);
//        if(league1.isPresent()){
//            leagueMarker.setName(league1.get().getName());
//            teamMarkersRepository.save(leagueMarker);
//        }
        for (League league : leagues) {
            Marker leagueMarker = new Marker();
            leagueMarker.setName(league.getName());
            markerRepository.save(leagueMarker);
            if (league.getApisportid() != 0)
                apiSportWebClient
                        .get()
                        .uri("/teams?league=" + league.getApisportid() + "&season=2020")
                        .header("x-rapidapi-host", "v3.football.api-sports.io")
                        .header("x-rapidapi-key", "4a422f46f4332a3394fe3c02bec44f94")
                        .retrieve()
                        .bodyToMono(TeamsResponse.class)
                        .subscribe(teamsResponse -> {
                            for (TeamResponse teamRes : teamsResponse.getResponse()) {
                                Team team = new Team();
                                Set<Marker> markersList = new HashSet<>();
                                Marker teamNameMarker = new Marker();
                                teamNameMarker.setName(teamRes.getTeam().getName());
                                markersList.add(teamNameMarker);
                                markersList.add(leagueMarker);
                                markerRepository.save(teamNameMarker);
                                //                        MarkersList.add();
//                            Optional<League> league = leagueRepository.findByApisportid(61);
//                            league.ifPresent(team::setLeague);
                                team.setLeague(league);
                                team.setName(teamRes.getTeam().getName());
                                team.setLogoUrl(teamRes.getTeam().getLogo());
                                team.setMarkers(markersList);
                                teamRepository.save(team);
//                        team.setMarkers();
                            }
                        });
            else{
                inFaktWebClient.get()
                        .uri("/countries.json?offset=0&limit=216")
                        .header("X-inFakt-ApiKey","b4cfc792ab9a45bb3025fb8c73c4f093aaf6a70f")
                        .retrieve()
                        .bodyToMono(CountriesResponse.class)
                        .subscribe(countriesResponse -> {
                           for(CountriesResponseEntity countryRes: countriesResponse.getEntities()){
                               Team team = new Team();
                               Set<Marker> teamMarkers = new HashSet<>();
                               Marker teamNameMarker = new Marker();
                               teamNameMarker.setName(countryRes.getPolish_name());
                               teamMarkers.add(teamNameMarker);
                               teamMarkers.add(leagueMarker);
                               team.setLeague(league);
                               team.setLogoUrl("");
                               team.setName(countryRes.getPolish_name());
                               markerRepository.save(teamNameMarker);
                               team.setMarkers(teamMarkers);
                               teamRepository.save(team);
                           }
                        });
            }
        }

        return "success";
    }
}
