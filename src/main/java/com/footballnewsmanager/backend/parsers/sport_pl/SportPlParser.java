package com.footballnewsmanager.backend.parsers.sport_pl;


import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.ParserHelper;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.services.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SportPlParser {

    private final SiteRepository siteRepository;
    private final NewsRepository newsRepository;
    private final TeamRepository teamRepository;
    private final TeamNewsRepository teamNewsRepository;
    private final UserTeamRepository userTeamRepository;
    private final UserNewsRepository userNewsRepository;

    public SportPlParser(SiteRepository siteRepository, NewsRepository newsRepository, TeamRepository teamRepository,
                         TeamNewsRepository teamNewsRepository, UserTeamRepository userTeamRepository,
                         UserNewsRepository userNewsRepository) {
        this.siteRepository = siteRepository;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.teamNewsRepository = teamNewsRepository;
        this.userTeamRepository = userTeamRepository;
        this.userNewsRepository = userNewsRepository;
    }


    public void getNews(List<Marker> markers, List<User> users) {
        Document sportPlMainDoc;
        String sportPlMainUrl = "https://www.sport.pl/pilka/0,0.html#TRNavSST";
        Site site = siteRepository.findById(3L).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej strony"));
        try {
            sportPlMainDoc = Jsoup.connect(sportPlMainUrl).get();
            Elements elements = sportPlMainDoc.getElementsByTag("section");
            for (Element element : elements) {
                if (!element.hasClass("body")) {
                    element.remove();
                }
            }
            List<String> tmp = elements.get(4).select("a").eachAttr("href");
            for (String tmpNewsUrl : tmp) {
                Long newsId = Long.valueOf(tmpNewsUrl.split(",")[2]);
                if (!newsRepository.existsBySiteIdAndId(site.getId(), newsId)) {
                    try {
                        Document doc = Jsoup.connect(tmpNewsUrl).get();
                        parseNewsAndSave(site, doc, markers, newsId, tmpNewsUrl, users);
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
        Elements articleElement = doc.getElementById("article_wrapper").getAllElements();
        String title = articleElement.get(0).getElementById("article_title").text();
        String date = articleElement.select("time").attr("datetime");
        String imgUrl = articleElement.get(0).getElementsByClass("related_image_wrap").select("img").attr("src");
        String tags = articleElement.get(0).getElementsByClass("tags").select("li").text();
        String localTime = LocalTime.now().format(DateTimeFormatter.ofPattern(":ss"));
        LocalDateTime localDate = LocalDateTime.parse(date + localTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime currentLocalDate = LocalDateTime.now().minusDays(7);
        if (localDate.isAfter(currentLocalDate)) {
            Set<Marker> markerSet = new HashSet<>(ParserHelper.getMarkers(markers, tags));
            if (markerSet.size() > 0) {
                News news = ParserHelper.saveNews(site, newsId, title, newsUrl, imgUrl, localDate, siteRepository, newsRepository);
                ParserHelper.connectNewsWithTeams(markerSet, news, teamNewsRepository, teamRepository);
                ParserHelper.connectNewsWithUsers(users, news, teamNewsRepository,
                        userTeamRepository, userNewsRepository);
            }
        }
    }
}
