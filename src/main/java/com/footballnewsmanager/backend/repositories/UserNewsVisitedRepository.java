package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserNewsVisited;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface UserNewsVisitedRepository extends JpaRepository<UserNewsVisited, Long> {

    boolean existsByUserAndNews(User user, News news);

    boolean existsByUserAndNewsDate(User user, LocalDate date);
}
