package com.footballnewsmanager.backend.parsers.interia_parser;

import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.ParserHelper;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.services.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Component
public class InteriaParser {

    private final SiteRepository siteRepository;
    private final NewsRepository newsRepository;
    private final TeamRepository teamRepository;
    private final TeamNewsRepository teamNewsRepository;
    private final UserTeamRepository userTeamRepository;
    private final UserNewsRepository userNewsRepository;

    public InteriaParser(SiteRepository siteRepository, NewsRepository newsRepository, TeamRepository teamRepository,
                         TeamNewsRepository teamNewsRepository, UserTeamRepository userTeamRepository, UserNewsRepository userNewsRepository) {
        this.siteRepository = siteRepository;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.teamNewsRepository = teamNewsRepository;
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
            Set<Marker> markerSet = ParserHelper.getMarkers(markers, title + tagsContent);
            if (markerSet.size() > 0) {
                News news = ParserHelper.saveNews(site, newsId, title, newsUrl, imgUrl, localDate, siteRepository, newsRepository);
                ParserHelper.connectNewsWithTeams(markerSet, news, teamNewsRepository, teamRepository);
                ParserHelper.connectNewsWithUsers(users, news, teamNewsRepository,
                        userTeamRepository, userNewsRepository);
            }
        }
    }
}
