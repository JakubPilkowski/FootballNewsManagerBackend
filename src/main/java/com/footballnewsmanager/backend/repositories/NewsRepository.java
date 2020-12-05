package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NewsRepository extends PagingAndSortingRepository<News, Long> {


    boolean existsBySiteIdAndId(Long siteId, Long id);

    void deleteByDateLessThan(LocalDate localDate);

    Optional<News> findBySiteIdAndId(Long sid, Long lid);

    Optional<Page<News>> findByTeamNewsTeam(Team team, Pageable pageable);

    Optional<Page<News>> findByTitleContainsIgnoreCase(String query, Pageable pageable);

    Optional<Page<News>> findDistinctByTeamNewsTeamIn(List<Team> teams, Pageable pageable);

    Long countDistinctByTeamNewsTeamInAndDate(List<Team> teams, LocalDate date);

    Long countDistinctByTeamNewsTeamIn(List<Team>teams);

}
