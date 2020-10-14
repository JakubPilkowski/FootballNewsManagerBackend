package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface UserSettingsRepository<T> extends JpaRepository<T, Long> {

    Optional<List<T>> findByUser(User user);

    boolean existsByUser(User user);

    void deleteByUser(User user);

}
