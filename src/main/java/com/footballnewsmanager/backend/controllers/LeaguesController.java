package com.footballnewsmanager.backend.controllers;

import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.leagues.LeagueResponse;
import com.footballnewsmanager.backend.models.League;
import com.footballnewsmanager.backend.repositories.LeagueRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/leagues")
public class LeaguesController {


    private final LeagueRepository leagueRepository;

    public LeaguesController(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    @GetMapping("")
    public ResponseEntity<LeagueResponse> getAllLeagues(){
        List<League> leagues = leagueRepository.findAll();
        LeagueResponse leagueResponse = new LeagueResponse(true, "Ligi oraz Reprezentacje", leagues);
        return ResponseEntity.ok().body(leagueResponse);
    }


}
