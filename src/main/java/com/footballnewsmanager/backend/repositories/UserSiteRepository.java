package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSiteRepository extends JpaRepository<UserSite, Long> {
    Optional<UserSite> findByUserAndSite(User user, Site site);

    void deleteByUserAndSite(User user, Site site);

}
