package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.api_sports.TeamResponse;
import com.footballnewsmanager.backend.api.api_sports.TeamsResponse;
import com.footballnewsmanager.backend.api.in_fakt.CountriesResponse;
import com.footballnewsmanager.backend.api.in_fakt.CountriesResponseEntity;
import com.footballnewsmanager.backend.models.League;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.TeamTags;
import com.footballnewsmanager.backend.repositories.LeagueRepository;
import com.footballnewsmanager.backend.repositories.TeamRepository;
import com.footballnewsmanager.backend.repositories.TeamTagsRepository;
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
    TeamTagsRepository teamTagsRepository;

    public TeamsController(TeamRepository teamRepository, LeagueRepository leagueRepository,
                           TeamTagsRepository teamTagsRepository) {
        this.teamRepository = teamRepository;
        this.leagueRepository = leagueRepository;
        this.teamTagsRepository = teamTagsRepository;
    }

    @GetMapping("/tmp")
    public String createTeamTmp() {
        Team team = new Team();
        Optional<League> league = leagueRepository.findByApisportid(61);
        Set<TeamTags> tagsList = new HashSet<>();
        TeamTags teamNameTag = new TeamTags();
        teamNameTag.setName("PSG");
//        teamNameTag.setTeam(team);
        TeamTags teamLigueTag = new TeamTags();
        teamLigueTag.setName("Ligue 1");
//        teamLigueTag.setTeam(team);
//        tagsList.add(teamNameTag);
//        tagsList.add(teamLigueTag);
        league.ifPresent(team::setLeague);
        team.setName("PSG");
        team.setLogoUrl("jakieś logo");
        tagsList.add(teamNameTag);
        tagsList.add(teamLigueTag);
        team.setTags(tagsList);
//        team.setTags(tagsList);
        teamTagsRepository.saveAll(tagsList);
        teamRepository.save(team);

//        teamNameTag.setTeam(team);
//        teamLigueTag.setTeam(team);

        return "success";
    }

    @GetMapping("/tmp2")
    public String createTeamTmp2() {
        Team team = new Team();
        Optional<League> league = leagueRepository.findByApisportid(61);
        Set<TeamTags> tagsList = new HashSet<>();
        TeamTags teamNameTag = new TeamTags();
        teamNameTag.setName("Marsylia");
//        teamNameTag.setTeam(team);
        Optional<TeamTags> teamLigueTag = teamTagsRepository.findByName("Ligue 1");
//        teamLigueTag.setTeam(team);
//        tagsList.add(teamNameTag);
//        tagsList.add(teamLigueTag);
        league.ifPresent(team::setLeague);
        team.setName("Marsylia");
        team.setLogoUrl("jakieś logo 2");
        tagsList.add(teamNameTag);
        teamLigueTag.ifPresent(tagsList::add);
        team.setTags(tagsList);
//        team.setTags(tagsList);
        teamTagsRepository.save(teamNameTag);
        teamRepository.save(team);

//        teamNameTag.setTeam(team);
//        teamLigueTag.setTeam(team);

        return "success";
    }


    @GetMapping("/tmpCreateTeam")
    public String createTeam() {
        WebClient apiSportWebClient = WebClient.create("https://v3.football.api-sports.io");
        WebClient inFaktWebClient = WebClient.create("https://api.infakt.pl/v3");
        List<League> leagues = leagueRepository.findAll();
        teamRepository.deleteAll();
        teamTagsRepository.deleteAll();
//        TeamTags leagueTag = new TeamTags();
//        Optional<League> league1 = leagueRepository.findByApisportid(61);
//        if(league1.isPresent()){
//            leagueTag.setName(league1.get().getName());
//            teamTagsRepository.save(leagueTag);
//        }
        for (League league : leagues) {
            TeamTags leagueTag = new TeamTags();
            leagueTag.setName(league.getName());
            teamTagsRepository.save(leagueTag);
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
                                Set<TeamTags> tagsList = new HashSet<>();
                                TeamTags teamNameTag = new TeamTags();
                                teamNameTag.setName(teamRes.getTeam().getName());
                                tagsList.add(teamNameTag);
                                tagsList.add(leagueTag);
                                teamTagsRepository.save(teamNameTag);
                                //                        tagsList.add();
//                            Optional<League> league = leagueRepository.findByApisportid(61);
//                            league.ifPresent(team::setLeague);
                                team.setLeague(league);
                                team.setName(teamRes.getTeam().getName());
                                team.setLogoUrl(teamRes.getTeam().getLogo());
                                team.setTags(tagsList);
                                teamRepository.save(team);
//                        team.setTags();
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
                               Set<TeamTags> teamTags = new HashSet<>();
                               TeamTags teamNameTag = new TeamTags();
                               teamNameTag.setName(countryRes.getPolish_name());
                               teamTags.add(teamNameTag);
                               teamTags.add(leagueTag);
                               team.setLeague(league);
                               team.setLogoUrl("");
                               team.setName(countryRes.getPolish_name());
                               teamTagsRepository.save(teamNameTag);
                               team.setTags(teamTags);
                               teamRepository.save(team);
                           }
                        });
            }
        }

        return "success";
    }
}
