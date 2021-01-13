package com.footballnewsmanager.backend.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.teams.TeamsFromTagsRequest;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.teams.TeamsResponse;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.helpers.LeaguesHelper;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.services.PaginationService;
import com.footballnewsmanager.backend.services.UserService;
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


    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;
    private final MarkerRepository markerRepository;
    private final LeaguesHelper leaguesHelper;
    private final UserService userService;
    private final UserTeamRepository userTeamRepository;
    private final UserRepository userRepository;

    public TeamsController(TeamRepository teamRepository, LeagueRepository leagueRepository,
                           MarkerRepository markerRepository, LeaguesHelper leaguesHelper,
                           UserService userService, UserTeamRepository userTeamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.leagueRepository = leagueRepository;
        this.markerRepository = markerRepository;
        this.leaguesHelper = leaguesHelper;
        this.userService = userService;
        this.userTeamRepository = userTeamRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "league={id}", params = {"page"})
    @JsonView(Views.Internal.class)
    public ResponseEntity<TeamsResponse> teamsByLeague(@RequestParam(value = "page", defaultValue = "0") @Min(value = 0) int page,
                                                       @PathVariable("id") @Min(value = 1) Long id) {
        TeamsResponse teamsResponse = new TeamsResponse();
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {

            Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.ASC, "team.name"));
            League league = leagueRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej ligi"));

            Page<UserTeam> teams = userTeamRepository.findByUserAndTeamLeague(user, league, pageable);
            PaginationService.handlePaginationErrors(page, teams);
            teamsResponse.setPages(teams.getTotalPages());
            teamsResponse.setTeams(teams.getContent());
            return user;
        });
        return ResponseEntity.ok(teamsResponse);
    }


    @GetMapping(value = "/favouriteTeams")
    @JsonView(Views.Public.class)
    public ResponseEntity<TeamsResponse> getFavouriteTeams() {

        TeamsResponse teamsResponse = new TeamsResponse();

        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {

            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "team.popularity", "team.id"));

            Page<UserTeam> teams = userTeamRepository.findByUserAndFavouriteIsTrue(user, pageable);

            PaginationService.handlePaginationErrors(0, teams);
            teamsResponse.setTeams(teams.getContent());
            return user;
        });

        return ResponseEntity.ok(teamsResponse);
    }


    @PostMapping("/findByTags")
    @JsonView(Views.Public.class)
    public ResponseEntity<TeamsResponse> findByTags(@RequestBody TeamsFromTagsRequest request) {

        TeamsResponse teamsResponse = new TeamsResponse();
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            List<Long> ids = new ArrayList<>();
            for (TeamNews team : request.getTeamNews()) {
                ids.add(team.getTeam().getId());
            }
            Pageable pageable = PageRequest.of(0, request.getTeamNews().size(), Sort.by(Sort.Direction.DESC, "favourite"));
            Page<UserTeam> userTeams = userTeamRepository.findByUserAndTeamIdIn(user, ids, pageable);
            teamsResponse.setTeams(userTeams.getContent());
            return user;
        });

        return ResponseEntity.ok(teamsResponse);
    }


    @GetMapping(value = "hot", params = {"page"})
    @JsonView(Views.Public.class)
    public ResponseEntity<TeamsResponse> hotTeams(@RequestParam(value = "page", defaultValue = "0") @NotNull @Range(min = 0) int page) {

        TeamsResponse teamsResponse = new TeamsResponse();

        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "team.popularity", "team.id"));
            Page<UserTeam> teams = userTeamRepository.findByUser(user, pageable);

            PaginationService.handlePaginationErrors(page, teams);
            teamsResponse.setTeams(teams.getContent());
            teamsResponse.setPages(teams.getTotalPages());
            return user;
        });
        return ResponseEntity.ok(teamsResponse);
    }

    @GetMapping("addClick/{id}")
    public ResponseEntity<BaseResponse> addClickToTeam(@PathVariable("id") @Min(value = 1) Long id) {
        Team team = teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej drużyny"));
        team.setClicks(team.getClicks() + 1);
        team.measurePopularity();
        teamRepository.save(team);
        return ResponseEntity.ok(new BaseResponse(true, "Dodano kliknięcie"));
    }

    //has role admin
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("updateTeams")
    public ResponseEntity<BaseResponse> updateTeams() {
        leaguesHelper.updateTeams();
        BaseResponse baseResponse = new BaseResponse(true, "Zaktualizowano drużyny");
        return ResponseEntity.ok().body(baseResponse);
    }


}
