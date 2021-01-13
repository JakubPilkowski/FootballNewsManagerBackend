package com.footballnewsmanager.backend.parsers;

import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ParserHelper {


    public static void connectNewsWithTeams(Set<Marker> markerSet, News news,
                                            TeamNewsRepository teamNewsRepository,
                                            TeamRepository teamRepository) {
        List<TeamNews> teamNewsList = new ArrayList<>();
        Set<Team> teams = new HashSet<>();
        for (Marker marker : markerSet) {
            Team team = marker.getTeam();
            if (!teams.contains(team)) {
                TeamNews teamNews = new TeamNews();
                teamNews.setNews(news);
                teamNews.setTeam(team);
                teamNewsList.add(teamNews);
                team.setNewsCount(team.getNewsCount() + 1);
                team.measurePopularity();
                teamRepository.save(team);
            }
            teams.add(team);
        }
        teamNewsRepository.saveAll(teamNewsList);
    }

    public static void connectNewsWithUsers(
            List<User> users, News news,
            TeamNewsRepository teamNewsRepository, UserTeamRepository userTeamRepository,
            UserNewsRepository userNewsRepository) {
        List<UserNews> userNewsList = new ArrayList<>();
        for (User user : users) {
            boolean exists = false;
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            Page<UserTeam> teams = userTeamRepository.findByUserAndFavouriteIsTrue(user, pageable);
            for (UserTeam team : teams) {
                if (teamNewsRepository.existsByTeamAndNews(team.getTeam(), news)) {
                    exists = true;
                    break;
                }
            }
            boolean isBadgeVisited = news.getDate().isBefore(user.getAddedDate()) || !exists;
            UserNews userNews = new UserNews();
            userNews.setNews(news);
            userNews.setUser(user);
            userNews.setInFavourites(exists);
            userNews.setBadged(isBadgeVisited);
            userNewsList.add(userNews);
        }
        userNewsRepository.saveAll(userNewsList);
    }

    public static Set<Marker> getMarkers(List<Marker> markers, String article) {
        Set<Marker> markerSet = new HashSet<>();
        for (Marker marker : markers) {
            if (article.contains(marker.getName())) {
                markerSet.add(marker);
            }
        }
        return markerSet;
    }

    public static News saveNews(Site site, Long newsId, String title, String newsUrl,
                                String imgUrl, LocalDateTime localDate, SiteRepository siteRepository,
                                NewsRepository newsRepository) {
        News news = new News();
        news.setSiteId(site.getId());
        news.setId(newsId);
        news.setTitle(title);
        news.setNewsUrl(newsUrl);
        news.setImageUrl(imgUrl);
        news.setSite(site);
        news.setDate(localDate);
        site.setNewsCount(site.getNewsCount() + 1);
        site.measurePopularity();
        siteRepository.save(site);
        return newsRepository.save(news);
    }

}
