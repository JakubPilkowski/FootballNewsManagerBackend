package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {

}
