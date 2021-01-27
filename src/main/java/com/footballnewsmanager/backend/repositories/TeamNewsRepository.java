package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.TeamNews;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TeamNewsRepository extends PagingAndSortingRepository<TeamNews, Long> {

    boolean existsByTeamAndNews(Team team, News news);

}
