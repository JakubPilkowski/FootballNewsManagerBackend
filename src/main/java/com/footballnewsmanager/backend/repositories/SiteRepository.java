package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Long> {

    Optional<Site> findByName(String name);
}
