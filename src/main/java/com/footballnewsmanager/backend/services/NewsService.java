package com.footballnewsmanager.backend.services;


import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.models.UserNews;
import com.footballnewsmanager.backend.repositories.NewsRepository;
import com.footballnewsmanager.backend.repositories.UserNewsDislikesRepository;
import com.footballnewsmanager.backend.repositories.UserNewsLikeRepository;
import com.footballnewsmanager.backend.repositories.UserNewsVisitedRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService extends BaseService{



    public UserNews createUserNews(User user, News news, UserNewsLikeRepository userNewsLikeRepository,
                                   UserNewsVisitedRepository userNewsVisitedRepository, UserNewsDislikesRepository userNewsDislikesRepository,
                                   NewsRepository newsRepository){
        UserNews userNews = new UserNews();
        boolean isLiked = userNewsLikeRepository.existsByUserAndNews(user, news);
        boolean isDisliked = userNewsDislikesRepository.existsByUserAndNews(user, news);
        boolean isVisited = userNewsVisitedRepository.existsByUserAndNews(user, news);
        boolean isBadgeVisited = isVisited || newsRepository.existsBySiteIdAndIdAndDateBefore(news.getSiteId()
                ,news.getId(),user.getAddedDate());
        userNews.setNews(news);
        userNews.setLiked(isLiked);
        userNews.setVisited(isVisited);
        userNews.setDisliked(isDisliked);
        userNews.setBadgeVisited(isBadgeVisited);
        return userNews;
    }

    public List<UserNews> createUserNewsTable(User user, Page<News> news, UserNewsLikeRepository userNewsLikeRepository,
                                              UserNewsVisitedRepository userNewsVisitedRepository, UserNewsDislikesRepository userNewsDislikesRepository,
                                              NewsRepository newsRepository){
        List<UserNews> userNewsList = new ArrayList<>();
        for (News singleNews : news.getContent()) {
            userNewsList.add(createUserNews(user, singleNews, userNewsLikeRepository,
                    userNewsVisitedRepository, userNewsDislikesRepository, newsRepository));
        }
        return userNewsList;
    }
}
