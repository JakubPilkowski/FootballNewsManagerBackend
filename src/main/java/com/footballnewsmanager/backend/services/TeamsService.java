package com.footballnewsmanager.backend.services;

import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.repositories.TeamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamsService extends BaseService{


    public List<Team> getFavouriteTeams(User user, TeamRepository teamRepository){
        Long count = teamRepository.countByUserTeamsUser(user);
        Pageable pageable = PageRequest.of(0, count.intValue());
        Page<Team> teams = teamRepository.findByUserTeamsUser(user, pageable)
                .orElseThrow(()-> new ResourceNotFoundException("Nie ma drużyn"));
        return teams.getContent();
    }
}
