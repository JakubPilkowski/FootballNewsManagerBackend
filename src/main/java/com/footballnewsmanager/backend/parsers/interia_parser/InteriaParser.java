package com.footballnewsmanager.backend.parsers.interia_parser;

import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.ParserHelper;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.services.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class InteriaParser {

    private final SiteRepository siteRepository;
    private final NewsRepository newsRepository;
    private final TeamRepository teamRepository;
    private final MarkerRepository markerRepository;
    private final TagRepository tagRepository;
    private final TeamNewsRepository teamNewsRepository;
    private final NewsTagRepository newsTagRepository;
    private final UserTeamRepository userTeamRepository;
    private final UserNewsRepository userNewsRepository;

    public InteriaParser(SiteRepository siteRepository, NewsRepository newsRepository, TeamRepository teamRepository,
                         MarkerRepository markerRepository, TagRepository tagRepository, TeamNewsRepository teamNewsRepository,
                         NewsTagRepository newsTagRepository, UserService userService, UserRepository userRepository,
                         UserTeamRepository userTeamRepository, UserNewsRepository userNewsRepository) {
        this.siteRepository = siteRepository;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.markerRepository = markerRepository;
        this.tagRepository = tagRepository;
        this.teamNewsRepository = teamNewsRepository;
        this.newsTagRepository = newsTagRepository;
        this.userTeamRepository = userTeamRepository;
        this.userNewsRepository = userNewsRepository;
    }


    public void getNews(List<Marker> markers, List<User> users) {
        Document interiaMainDoc;
        String interiaPlMainUrl = "https://sport.interia.pl/pilka-nozna";
        Site site = siteRepository.findById(5L).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej strony"));
        try {
            interiaMainDoc = Jsoup.connect(interiaPlMainUrl).get();
            List<String> tmp = interiaMainDoc.getElementsByClass("tile-magazine-thumb").eachAttr("href");
            for (String tmpNewsUrl : tmp) {
                Long newsId = Long.valueOf(tmpNewsUrl.split(",")[2]);
                String fullArticleLink = interiaPlMainUrl.replace("/pilka-nozna", "") + tmpNewsUrl;
                if (!fullArticleLink.contains("/video,")&& !fullArticleLink.contains(",nzId,") && !newsRepository.existsBySiteIdAndId(site.getId(), newsId)) {
                    try {
                        Document doc = Jsoup.connect(fullArticleLink).get();
                        parseNewsAndSave(site, doc, markers, newsId, fullArticleLink, users);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseNewsAndSave(Site site, Document doc, List<Marker> markers, Long newsId, String newsUrl, List<User> users) {
        Element element = doc.getElementsByTag("article").get(0);
        String date = element.getElementsByTag("meta").attr("content").replace("T", " ");
        String title = element.getElementsByTag("header").text();
        String imgUrl = element.getElementsByClass("embed-photo").select("meta").attr("content");
        if (imgUrl.isEmpty()) {
            imgUrl = "No image";
        }
        LocalDateTime localDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime currentLocalDate = LocalDateTime.now().minusDays(7);
        if (localDate.isAfter(currentLocalDate)) {
            String tagsContent = element.getElementsByTag("p").text().replace("Interia", "");
            Set<Tag> tagSet = ParserHelper.getTags(markers, tagsContent, tagRepository);
            if (tagSet.size() > 0) {
                News news = ParserHelper.saveNews(site, newsId, title, newsUrl, imgUrl, localDate, siteRepository, newsRepository);
                ParserHelper.saveNewsTags(tagSet, news, newsTagRepository);
                ParserHelper.connectNewsWithTeams(tagSet, news, teamNewsRepository, markerRepository, teamRepository);
                ParserHelper.connectNewsWithUsers(users, news, teamNewsRepository,
                        userTeamRepository, userNewsRepository);
            }
        }
    }
}
