package com.footballnewsmanager.backend.parsers;

import com.footballnewsmanager.backend.exceptions.BadRequestException;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class ParserHelper {


    public static void connectNewsWithTeams(Set<Tag> tagSet, News news,
                                            TeamNewsRepository teamNewsRepository,
                                            MarkerRepository markerRepository,
                                            TeamRepository teamRepository) {

        for (Tag tag : tagSet) {
            Marker marker = markerRepository.findByName(tag.getName()).orElseThrow(() -> new ResourceNotFoundException(""));
            Team team = marker.getTeam();
            if (!teamNewsRepository.existsByTeamAndNews(team, news)) {
                TeamNews teamNews = new TeamNews();
                teamNews.setNews(news);
                teamNews.setTeam(team);
                team.setNewsCount(team.getNewsCount() + 1);
                team.measurePopularity();
                teamRepository.save(team);
                teamNewsRepository.save(teamNews);
            }
        }
    }

    public static void connectNewsWithUsers(
            List<User> users, News news,
            TeamNewsRepository teamNewsRepository, UserTeamRepository userTeamRepository,
            UserNewsRepository userNewsRepository) {

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
            userNewsRepository.save(userNews);
        }
    }

    public static Set<Tag> getTags(List<Marker> markers, String article, TagRepository tagRepository) {
        Set<Tag> tagSet = new HashSet<>();
        for (Marker marker : markers) {
            if (article.contains(marker.getName())) {
                Tag tag = tagRepository.findByName(marker.getName()).orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(marker.getName());
                    newTag = tagRepository.save(newTag);
                    return newTag;
                });
                tagSet.add(tag);
            }
        }
        return tagSet;
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
