package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserNewsRepository extends PagingAndSortingRepository<UserNews, Long> {


    Long countDistinctByUserAndInFavouritesIsTrueAndNewsDateAfter(User user, LocalDateTime localDateTime);

    Long countDistinctByUserAndNewsDateAfter(User user, LocalDateTime localDateTime);

    Long countDistinctByUserAndLikedIsTrue(User user);

    Long countByUserAndInFavouritesIsTrue(User user);

    Long countByUserAndInFavouritesIsTrueAndBadgedIsFalse(User user);

    Optional<UserNews> findByUserAndNews(User user, News news);

    Page<UserNews> findByUserAndNewsTeamNewsTeam(User user, Team team, Pageable pageable);

    Page<UserNews> findByUserAndInFavouritesIsTrue(User user, Pageable pageable);

    Page<UserNews> findByUserAndLikedIsTrue(User user, Pageable pageable);

    Page<UserNews> findAllByUser(User user, Pageable pageable);
}
