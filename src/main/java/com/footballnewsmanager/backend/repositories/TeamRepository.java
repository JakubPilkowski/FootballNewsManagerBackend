package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TeamRepository extends PagingAndSortingRepository<Team, Long> {


    Page<Team> findByNameContainsIgnoreCase(String query, Pageable pageable);

    Page<Team> findDistinctByMarkersNameIn(Set<String> names, Pageable pageable);

    Page<Team> findByUserTeamsUser(User user, Pageable pageable);

    Page<Team> findDistinctByUserTeamsUserAndTeamNewsNewsAndUserTeamsFavouriteIsTrue(User user, News singleAllUserNews, Pageable pageable);
}
