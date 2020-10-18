package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserNewsLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNewsLikeRepository extends JpaRepository<UserNewsLike, Long> {

    boolean existsByUserAndNews(User user, News news);

    void deleteByUserAndNews(User user, News news);
}
