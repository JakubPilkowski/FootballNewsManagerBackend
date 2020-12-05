package com.footballnewsmanager.backend.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.teams.TeamsFromTagsRequest;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.TeamsResponse;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.helpers.LeaguesHelper;
import com.footballnewsmanager.backend.models.League;
import com.footballnewsmanager.backend.models.Tag;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.repositories.LeagueRepository;
import com.footballnewsmanager.backend.repositories.TeamRepository;
import com.footballnewsmanager.backend.repositories.MarkerRepository;
import com.footballnewsmanager.backend.views.Views;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequestMapping("/teams")
@Validated
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

    @GetMapping(value = "", params = {"page"})
    @JsonView(Views.Internal.class)
    public ResponseEntity<TeamsResponse> teams(@RequestParam(value = "page", defaultValue = "0") @Min(value = 0) int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.ASC, "name"));
        Page<Team> teams = teamRepository.findAll(pageable);
        if(page+1 > teams.getTotalPages()) throw new ResourceNotFoundException("Nie ma już więcej wyników");
        return ResponseEntity.ok().body(new TeamsResponse(true, "Drużyny", teams.getContent()));
    }

    @GetMapping(value = "league={id}", params = {"page"})
    @JsonView(Views.Internal.class)
    public ResponseEntity<TeamsResponse> teamsByLeague(@RequestParam(value = "page", defaultValue = "0") @Min(value = 0) int page,
                                                       @PathVariable("id") @Min(value = 1) Long id){
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.ASC, "name"));
        League league = leagueRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Nie ma takiej ligi"));
        Page<Team> teams = teamRepository.findByLeague(league, pageable)
                .orElseThrow(()-> new ResourceNotFoundException("Nie ma drużyn dla podanej ligi"));
        if(page+1 > teams.getTotalPages()) throw new ResourceNotFoundException("Nie ma już więcej wyników");
        return ResponseEntity.ok(new TeamsResponse(true, "Drużyny dla: "+league.getName(), teams.getContent()));
    }

    @GetMapping("{id}")
    @JsonView(Views.Internal.class)
    public ResponseEntity<TeamsResponse> teamById(@PathVariable("id") @Min(value = 1) Long id){
        Team team  = teamRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Nie ma takiej drużyny!"));
        return ResponseEntity.ok(new TeamsResponse(true, "Drużyna", Collections.singletonList(team)));
    }


    @PostMapping("/findByTags")
    @JsonView(Views.Public.class)
    public ResponseEntity<TeamsResponse> findByTags(@RequestBody TeamsFromTagsRequest request){
        Set<String> names = new HashSet<>();
        for (Tag tag: request.getTags()) {
            names.add(tag.getName());
        }
        Pageable pageable = PageRequest.of(0, names.size(), Sort.by(Sort.Direction.DESC, "popularity"));
        Page<Team> teams = teamRepository.findDistinctByMarkersNameIn(names,pageable)
                .orElseThrow(()->new ResourceNotFoundException("Nie ma drużyn dla podanych tagów"));
        return ResponseEntity.ok(new TeamsResponse(true, "Drużyny dla podanych tagów", teams.getContent()));
    }


    @GetMapping(value = "hot", params = {"count"})
    @JsonView(Views.Public.class)
    public ResponseEntity<TeamsResponse> hotTeams(@RequestParam(value = "count", defaultValue = "5") @NotNull @Range(min = 5, max = 10) int count){
        Pageable pageable = PageRequest.of(0,count, Sort.by(Sort.Direction.DESC, "popularity"));
        Page<Team> teams = teamRepository.findAll(pageable);
        return ResponseEntity.ok(new TeamsResponse(true, "Popularne drużyny", teams.getContent()));
    }

    @GetMapping("addClick/{id}")
    public ResponseEntity<BaseResponse> addClickToTeam(@PathVariable("id") @Min(value = 1) Long id){
        Team team = teamRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Nie ma takiej drużyny"));
        team.setClicks(team.getClicks()+1);
        team.measurePopularity();
        teamRepository.save(team);
        return ResponseEntity.ok(new BaseResponse(true, "Dodano kliknięcie"));
    }

    @GetMapping(value = "query={query}", params = {"page"})
    public ResponseEntity<TeamsResponse> getTeamsByQuery(@RequestParam(value = "page", defaultValue = "0") @Min(value = 0) int page, @PathVariable("query") @NotNull() String query){
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "popularity"));
        Page<Team> pages = teamRepository.findByNameContainsIgnoreCase(query, pageable).orElseThrow(()->new ResourceNotFoundException("Dla podanego hasła nie ma żadnej drużyny"));
        if(page +1 >pages.getTotalPages())
            throw new ResourceNotFoundException("Nie ma już więcej wyników");
        if(pages.getTotalElements()==0)
            throw new ResourceNotFoundException("Dla podanej frazy nie ma żadnej drużyny");
        return ResponseEntity.ok(new TeamsResponse(true, "Znalezione drużyny", pages.getContent()));
    }

    //has role admin
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("updateTeams")
    public ResponseEntity<BaseResponse> updateTeams(){
        leaguesHelper.updateTeams();
        BaseResponse baseResponse = new BaseResponse(true, "Zaktualizowano drużyny");
        return ResponseEntity.ok().body(baseResponse);
    }



}
