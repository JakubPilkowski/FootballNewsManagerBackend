package com.footballnewsmanager.backend.parsers.football_italia;

import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.ParserHelper;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.services.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class Football_Italia_Parser {


    private final SiteRepository siteRepository;
    private final NewsRepository newsRepository;
    private final TeamRepository teamRepository;
    private final MarkerRepository markerRepository;
    private final TagRepository tagRepository;
    private final TeamNewsRepository teamNewsRepository;
    private final NewsTagRepository newsTagRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final UserNewsRepository userNewsRepository;
    private final List<String> italianTeams = new ArrayList<>(Arrays.asList(
            "Atalanta",
            "Benevento",
            "Bologna",
            "Cagliari",
            "Crotone",
            "Fiorentina",
            "Genoa",
            "Inter",
            "Juventus",
            "Lazio",
            "Milan",
            "Napoli",
            "Parma",
            "Roma",
            "Sampdoria",
            "Sassuolo",
            "Spezia",
            "Torino",
            "Udinese",
            "Verona"
    ));

    public Football_Italia_Parser(SiteRepository siteRepository, NewsRepository newsRepository, TeamRepository teamRepository, MarkerRepository markerRepository, TagRepository tagRepository, TeamNewsRepository teamNewsRepository, NewsTagRepository newsTagRepository, UserService userService, UserRepository userRepository, UserTeamRepository userTeamRepository, UserNewsRepository userNewsRepository) {
        this.siteRepository = siteRepository;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.markerRepository = markerRepository;
        this.tagRepository = tagRepository;
        this.teamNewsRepository = teamNewsRepository;
        this.newsTagRepository = newsTagRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.userTeamRepository = userTeamRepository;
        this.userNewsRepository = userNewsRepository;
    }

    public void getNews(List<Marker> markers, List<User> users) {
        Site site = siteRepository.findById(1L).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej Strony"));
        for (String italianTeam : italianTeams) {
            Document footballItaliaMainDoc;
            try {
                footballItaliaMainDoc = Jsoup.connect("https://www.football-italia.net/clubs/" + italianTeam + "/news").get();
                List<String> tmpNewsUrls = footballItaliaMainDoc.body().getElementsByClass("news-idx-item-title").select("a").eachAttr("href");
                String footballItaliaSiteUrl = "https://www.football-italia.net";
                for (String tmpNewsUrl : tmpNewsUrls) {
                    Long newsId = Long.parseLong(tmpNewsUrl.split("/")[1]);
                    if (!newsRepository.existsBySiteIdAndId(site.getId(), newsId)) {
                        String articleLink = footballItaliaSiteUrl + tmpNewsUrl;
                        try {
                            Document doc = Jsoup.connect(articleLink).get();
                            parseNewsAndSave(site, doc, markers, newsId, articleLink, users);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void parseNewsAndSave(Site site, Document doc, List<Marker> markers, Long newsId, String newsUrl, List<User> users) {
        String title = doc.getElementsByClass("title").text();
        String imgUrl = doc.getElementsByClass("story-image-wrapper").select("img").first().attr("src");
        String[] date = doc.getElementsByClass("date").html().split(" ");
        LocalTime localTime = LocalTime.now();
        String formattedLocalTime = localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d yyyy HH:mm:ss", Locale.ENGLISH);
        LocalDateTime localDate = LocalDateTime.parse(date[1] + " " + date[2] + " " + date[3] + " " + formattedLocalTime, formatter);
        String content = doc.body().getElementsByClass("content").select("p").text();
        String footballItaliaEndNewsSyntax = "Watch Serie A live in the UK on Premier Sports for just £9.99 per month including live LaLiga, Eredivisie, Scottish Cup Football and more. Visit: https://www.premiersports.com/subscribenow";
        String endContent = content.replace(footballItaliaEndNewsSyntax, "");
        LocalDateTime currentLocalDate = LocalDateTime.now().minusDays(7);
        if (localDate.isAfter(currentLocalDate)) {
            Set<Tag> tagSet = new HashSet<>(ParserHelper.getTags(markers, endContent, tagRepository));
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
