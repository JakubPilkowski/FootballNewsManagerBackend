package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.api_sports.TeamResponse;
import com.footballnewsmanager.backend.api.api_sports.TeamsResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public String createTeamTmp(){
        Team team = new Team();
        Optional<League> league = leagueRepository.findByApisportid(61);
        List<TeamTags> tagsList = new ArrayList<>();
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
//        team.setTags(tagsList);
        teamRepository.save(team);
        teamNameTag.setTeam(team);
        teamLigueTag.setTeam(team);
        tagsList.add(teamNameTag);
        tagsList.add(teamLigueTag);
        teamTagsRepository.saveAll(tagsList);
        return "success";
    }


    @GetMapping("/tmpCreateTeam")
    public String createTeam(){
        WebClient webClient = WebClient.create("https://v3.football.api-sports.io");
        List<League> leagues = leagueRepository.findAll();
//        for (League league : leagues) {
//
//        }
        webClient
                .get()
                .uri("/teams?league=61&season=2020")
                .header("x-rapidapi-host","v3.football.api-sports.io")
                .header("x-rapidapi-key","4a422f46f4332a3394fe3c02bec44f94")
                .retrieve()
                .bodyToMono(TeamsResponse.class)
                .subscribe(teamsResponse -> {
                    for (TeamResponse teamRes:teamsResponse.getResponse()) {
                        Team team = new Team();
                        List<TeamTags> tagsList = new ArrayList<>();
                        TeamTags teamNameTag = new TeamTags();
                        teamNameTag.setName(teamRes.getTeam().getName());

                        //                        tagsList.add();
                        Optional<League> league = leagueRepository.findByApisportid(61);
                        league.ifPresent(team::setLeague);
                        team.setName(teamRes.getTeam().getName());
                        team.setLogoUrl(teamRes.getTeam().getLogo());
                        teamRepository.save(team);
//                        team.setTags();
                    }
                });

        return "success";
    }
}
