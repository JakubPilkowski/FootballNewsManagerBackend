package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.League;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeagueRepository extends JpaRepository<League, Integer> {


    Optional<League> findByApisportid(int id);

    Optional<League> findByName(String name);
}
