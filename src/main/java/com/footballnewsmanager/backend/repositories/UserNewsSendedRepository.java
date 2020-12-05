package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserNewsSended;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNewsSendedRepository extends JpaRepository<UserNewsSended, Long> {
    boolean existsByUserAndNews(User user, News news);

}
