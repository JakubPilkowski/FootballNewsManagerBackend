package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.League;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserTeam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface UserTeamRepository extends PagingAndSortingRepository<UserTeam, Pageable> {
    Optional<UserTeam> findByUserAndTeam(User user, Team team);

    Long countByUserAndFavouriteIsTrue(User user);

    Page<UserTeam> findByUserAndTeamLeague(User user, League league, Pageable pageable);

    Page<UserTeam> findByUserAndFavouriteIsTrue(User user, Pageable pageable);

    Page<UserTeam> findByUserAndTeamIn(User user, List<Team> teams, Pageable pageable);

    Page<UserTeam> findByUser(User user, Pageable pageable);

    Page<UserTeam> findByUserAndFavouriteIsFalse(User user, Pageable teamsPageable);

    Long countByUserAndFavouriteIsFalse(User user);
}
