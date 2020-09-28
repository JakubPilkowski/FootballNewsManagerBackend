package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.TeamTags;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamTagsRepository extends JpaRepository<TeamTags, Integer> {

}
