package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.League;
import com.footballnewsmanager.backend.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {


    Optional<List<Team>> findByLeague(League league);
}
