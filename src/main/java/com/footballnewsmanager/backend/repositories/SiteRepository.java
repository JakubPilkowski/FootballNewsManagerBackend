package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.models.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface SiteRepository extends PagingAndSortingRepository<Site, Long> {
}
