package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.League;
import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TeamRepository extends PagingAndSortingRepository<Team, Long>{

    Optional<Page<Team>> findByLeague(League league, Pageable pageable);

    Optional<Page<Team>> findByNameContainsIgnoreCase(String query, Pageable pageable);

    Optional<Page<Team>> findByTeamNewsNewsIn(List<News>news, Pageable pageable);

    Optional<Page<Team>> findDistinctByMarkersNameIn(Set<String> names, Pageable pageable);

    Optional<Page<Team>> findByUserTeamsUser(User user, Pageable pageable);

    Long countByUserTeamsUser(User user);
}
