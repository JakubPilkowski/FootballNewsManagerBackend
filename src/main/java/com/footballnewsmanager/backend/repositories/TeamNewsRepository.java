package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.TeamNews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamNewsRepository extends JpaRepository<TeamNews, Long> {


    boolean existsByTeamAndNews(Team team, News news);

}
