package com.footballnewsmanager.backend.services;


import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserNews;
import com.footballnewsmanager.backend.repositories.NewsRepository;
import com.footballnewsmanager.backend.repositories.TeamNewsRepository;
import com.footballnewsmanager.backend.repositories.UserNewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public class NewsService {

    public static void toggleNewsToFavourites(NewsRepository newsRepository, UserNewsRepository userNewsRepository,
                                              User user, Team team, boolean toggle){
        Long count = newsRepository.countDistinctByTeamNewsTeam(team);
        Pageable pageable = PageRequest.of(0, count.intValue());
        Page<News> news = newsRepository.findDistinctByTeamNewsTeam(team, pageable);
        Page<UserNews> userNews = userNewsRepository.findByUserAndNewsIn(user, news.getContent(), pageable)
                .orElseThrow(()-> new ResourceNotFoundException("Nie ma newsów dla użytkownika"));
        for(UserNews singleUserNews: userNews){
            singleUserNews.setInFavourites(toggle);
        }
        userNewsRepository.saveAll(userNews.getContent());
    }

    public static void initNewsForUser(NewsRepository newsRepository, UserNewsRepository userNewsRepository,
                                       User user){
        List<News> news = (List<News>) newsRepository.findAll();
        System.out.println("Długość listy" + news.size());
        List<UserNews> userNews = new ArrayList<>();
        for(News singleNews: news){
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
