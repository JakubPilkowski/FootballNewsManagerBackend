package com.footballnewsmanager.backend.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.TeamsResponse;
import com.footballnewsmanager.backend.helpers.LeaguesHelper;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.repositories.LeagueRepository;
import com.footballnewsmanager.backend.repositories.TeamRepository;
import com.footballnewsmanager.backend.repositories.MarkerRepository;
import com.footballnewsmanager.backend.views.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/teams")
public class TeamsController {


    TeamRepository teamRepository;
    LeagueRepository leagueRepository;
    MarkerRepository markerRepository;
    LeaguesHelper leaguesHelper;

    public TeamsController(TeamRepository teamRepository, LeagueRepository leagueRepository, MarkerRepository markerRepository, LeaguesHelper leaguesHelper) {
        this.teamRepository = teamRepository;
        this.leagueRepository = leagueRepository;
        this.markerRepository = markerRepository;
        this.leaguesHelper = leaguesHelper;
    }

    @GetMapping("")
    @JsonView(Views.Internal.class)
    public ResponseEntity<TeamsResponse> teams() {
        List<Team> teams = teamRepository.findAll();
        TeamsResponse teamsResponse = new TeamsResponse(true, "Drużyny", teams);
        return ResponseEntity.ok().body(teamsResponse);
    }

    //has role admin
    @GetMapping("updateTeams")
    public ResponseEntity<BaseResponse> updateTeams(){
        leaguesHelper.updateTeams();
        BaseResponse baseResponse = new BaseResponse(true, "Zaktualizowano drużyny");
        return ResponseEntity.ok().body(baseResponse);
    }

}
