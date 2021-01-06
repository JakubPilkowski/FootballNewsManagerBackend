package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface NewsRepository extends PagingAndSortingRepository<News, Long> {


    boolean existsBySiteIdAndId(Long siteId, Long id);

    void deleteByDateLessThan(LocalDateTime localDateTime);

    Optional<News> findBySiteIdAndId(Long sid, Long lid);

    Page<News> findByTitleContainsIgnoreCase(String query, Pageable pageable);

    Long countDistinctByTeamNewsTeamAndDateAfter(Team team, LocalDateTime localDateTime);

}
