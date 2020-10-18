package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserNewsDislike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNewsDislikesRepository extends JpaRepository<UserNewsDislike, Long> {
    boolean existsByUserAndNews(User user, News news);

    void deleteByUserAndNews(User user, News news);
}
