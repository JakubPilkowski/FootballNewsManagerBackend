package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.models.SiteClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SiteClickRepository extends JpaRepository<SiteClick, Integer> {

    Optional<SiteClick> findBySiteAndDate(Site site, LocalDate date);

    int countBySite(Site site);

    @Query(value = "select sum(c.clicks) from SiteClick c where c.site = :site and c.date between :startDate and :endDate", nativeQuery = true)
    Optional<Integer> sumFromLastWeek(@Param("site") Site site, @Param("startDate") LocalDate dateBegin, @Param("endDate") LocalDate dateEnd);
}
