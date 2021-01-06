package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.League;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueRepository extends JpaRepository<League, Long> {
}
