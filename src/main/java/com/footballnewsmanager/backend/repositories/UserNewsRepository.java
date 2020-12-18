package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserNewsRepository extends PagingAndSortingRepository<UserNews, Long> {

    Optional<Page<UserNews>> findByUserAndInFavouritesIsTrue(User user, Pageable pageable);

    Long countDistinctByUserAndInFavouritesIsTrueAndNewsDateAfter(User user, LocalDateTime localDateTime);
    Long countDistinctByUserAndNewsDateAfter(User user, LocalDateTime localDateTime);

    Optional<Page<UserNews>> findByUserAndNewsIn(User user, List<News> newsList, Pageable pageable);

    Long countByUserAndInFavouritesIsTrue(User user);
    Long countByUserAndInFavouritesIsTrueAndBadgedIsFalse(User user);

    Optional<UserNews> findByUserAndNews(User user, News news);

    Optional<Page<UserNews>> findByUserAndInFavouritesIsTrueAndBadgedIsFalse(User user, Pageable pageable);

    Optional<Page<UserNews>> findAllByUser(User user, Pageable pageable);
}
