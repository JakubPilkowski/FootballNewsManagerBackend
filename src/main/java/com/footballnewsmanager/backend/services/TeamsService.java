package com.footballnewsmanager.backend.services;

import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserTeam;
import com.footballnewsmanager.backend.repositories.TeamRepository;
import com.footballnewsmanager.backend.repositories.UserTeamRepository;

import java.util.ArrayList;
import java.util.List;

public class TeamsService {


    public static void initTeamsForUser(User user, TeamRepository teamRepository, UserTeamRepository userTeamRepository){
        List<Team> teams = (List<Team>) teamRepository.findAll();
        List<UserTeam> userTeams = new ArrayList<>();
        for(Team team: teams){
            UserTeam userTeam = new UserTeam();
            userTeam.setTeam(team);
            userTeam.setUser(user);
            userTeams.add(userTeam);
        }
        userTeamRepository.saveAll(userTeams);
    }
}
