package com.footballnewsmanager.backend.services;


import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserNews;
import com.footballnewsmanager.backend.repositories.NewsRepository;
import com.footballnewsmanager.backend.repositories.TeamRepository;
import com.footballnewsmanager.backend.repositories.UserNewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class NewsService {

    public static void addNewsToFavourites(UserNewsRepository userNewsRepository,
                                           User user, Team team) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<UserNews> allUserNews = userNewsRepository.findByUserAndNewsTeamNewsTeam(user, team, pageable);

        for (UserNews singleAllUserNews : allUserNews) {
            if (!singleAllUserNews.isInFavourites())
                singleAllUserNews.setInFavourites(true);
        }
        userNewsRepository.saveAll(allUserNews.getContent());
    }

    public static void deleteNewsFromFavourites(UserNewsRepository userNewsRepository,
                                                TeamRepository teamRepository,
                                                User user, Team team) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<UserNews> allUserNews = userNewsRepository.findByUserAndInFavouritesIsTrue(user, pageable);

        for (UserNews singleAllUserNews : allUserNews) {
            Page<Team> teams = teamRepository.findDistinctByUserTeamsUserAndTeamNewsNewsAndUserTeamsFavouriteIsTrue
                    (user, singleAllUserNews.getNews(), pageable);
            if (teams.getContent().contains(team) && teams.getTotalElements() == 1) {
                singleAllUserNews.setInFavourites(false);
                singleAllUserNews.setBadged(true);
            }
        }
        userNewsRepository.saveAll(allUserNews.getContent());
    }

    public static void initNewsForUser(NewsRepository newsRepository, UserNewsRepository userNewsRepository,
                                       User user) {
        List<News> news = (List<News>) newsRepository.findAll();
        List<UserNews> userNews = new ArrayList<>();
        for (News singleNews : news) {
            UserNews singleUserNews = new UserNews();
            singleUserNews.setUser(user);
            singleUserNews.setNews(singleNews);
            singleUserNews.setLiked(false);
            singleUserNews.setVisited(false);
            singleUserNews.setInFavourites(false);
            singleUserNews.setBadged(true);
            userNews.add(singleUserNews);
        }
        userNewsRepository.saveAll(userNews);
    }

}
