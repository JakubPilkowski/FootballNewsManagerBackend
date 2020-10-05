package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.api_sports.ApiSportCountriesResponse;
import com.footballnewsmanager.backend.api.api_sports.ApiSportTeamsResponse;
import com.footballnewsmanager.backend.api.api_sports.CountryResponse;
import com.footballnewsmanager.backend.api.api_sports.TeamResponse;
import com.footballnewsmanager.backend.api.google_translate.TranslateRequest;
import com.footballnewsmanager.backend.api.google_translate.TranslateResponse;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.TeamsResponse;
import com.footballnewsmanager.backend.helpers.LeaguesHelper;
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
    LeaguesHelper leaguesHelper;

    public TeamsController(TeamRepository teamRepository, LeagueRepository leagueRepository, MarkerRepository markerRepository, LeaguesHelper leaguesHelper) {
        this.teamRepository = teamRepository;
        this.leagueRepository = leagueRepository;
        this.markerRepository = markerRepository;
        this.leaguesHelper = leaguesHelper;
    }

    @GetMapping("")
    public ResponseEntity<TeamsResponse> teams() {
        TeamsResponse teamsResponse = new TeamsResponse(true, "Drużyny", teamRepository.findAll());
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
