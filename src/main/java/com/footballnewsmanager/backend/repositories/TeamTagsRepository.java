package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.TeamTags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamTagsRepository extends JpaRepository<TeamTags, Integer> {

    Optional<TeamTags>findByName(String name);
}
