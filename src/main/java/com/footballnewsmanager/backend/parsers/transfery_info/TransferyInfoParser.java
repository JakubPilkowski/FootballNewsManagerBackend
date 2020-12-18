package com.footballnewsmanager.backend.parsers.transfery_info;

import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.ParserHelper;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.services.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class TransferyInfoParser {

    private final SiteRepository siteRepository;
    private final NewsRepository newsRepository;
    private final TeamRepository teamRepository;
    private final MarkerRepository markerRepository;
    private final TagRepository tagRepository;
    private final TeamNewsRepository teamNewsRepository;
    private final NewsTagRepository newsTagRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final FavouriteTeamRepository favouriteTeamRepository;
    private final UserNewsRepository userNewsRepository;

    public TransferyInfoParser(SiteRepository siteRepository, NewsRepository newsRepository, TeamRepository teamRepository, MarkerRepository markerRepository, TagRepository tagRepository, TeamNewsRepository teamNewsRepository, NewsTagRepository newsTagRepository, UserService userService, UserRepository userRepository, FavouriteTeamRepository favouriteTeamRepository, UserNewsRepository userNewsRepository) {
        this.siteRepository = siteRepository;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.markerRepository = markerRepository;
        this.tagRepository = tagRepository;
        this.teamNewsRepository = teamNewsRepository;
        this.newsTagRepository = newsTagRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.favouriteTeamRepository = favouriteTeamRepository;
        this.userNewsRepository = userNewsRepository;
    }


    public void getNews(List<Marker> markers, List<User> users) {
        Document transferyInfoMainDoc;
        String tranferyInfoMainUrl = "https://transfery.info";
        Site site = siteRepository.findById(2L).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej strony"));
        try {
            transferyInfoMainDoc = Jsoup.connect("https://transfery.info/aktualnosci").get();
            List<String> tmpNewsUrls = transferyInfoMainDoc.getElementsByClass("article-links").select("a").eachAttr("href");
            for (String tmpNewsUrl : tmpNewsUrls) {
                Long newsId = Long.parseLong(tmpNewsUrl.split("/")[3]);
                if (!newsRepository.existsBySiteIdAndId(site.getId(), newsId)) {
                    String articleLink = tranferyInfoMainUrl + tmpNewsUrl;
                    try {
                        Document doc = Jsoup.connect(articleLink).get();
                        parseNewsAndSave(tranferyInfoMainUrl, site, doc, markers, newsId, articleLink, users);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void parseNewsAndSave(String tranferyInfoMainUrl, Site site, Document doc, List<Marker> markers, Long newsId, String newsUrl, List<User> users) {
        Elements articleElement = doc.getElementsByTag("article");
        String title = articleElement.get(0).select("h1").text();
        String imgUrl = tranferyInfoMainUrl + "/" + articleElement.select("picture").get(0).select("source").get(1).attr("srcset");
        String date = articleElement.select("time").text().substring(0, 19);
        LocalDateTime localDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String articleTagSection = doc.getElementsByClass("d-inline").text();
        Set<Tag> tagSet = new HashSet<>(ParserHelper.getTags(markers, articleTagSection, tagRepository));
        News news = ParserHelper.saveNews(site, newsId, title, newsUrl, imgUrl, localDate, siteRepository, newsRepository);
        for (Tag tag :
                tagSet) {
            NewsTag newsTag = new NewsTag();
            newsTag.setNews(news);
            newsTag.setTag(tag);
            newsTagRepository.save(newsTag);
        }
        ParserHelper.connectNewsWithTeams(tagSet, news, teamNewsRepository, markerRepository, teamRepository);
        ParserHelper.connectNewsWithUsers(users, news, teamNewsRepository,
                favouriteTeamRepository, userNewsRepository);
    }

}
