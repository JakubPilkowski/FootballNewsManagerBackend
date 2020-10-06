package com.footballnewsmanager.backend.parsers.football_italia;

import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.ParserHelper;
import com.footballnewsmanager.backend.repositories.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
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

    public Football_Italia_Parser(SiteRepository siteRepository, NewsRepository newsRepository, TeamRepository teamRepository, MarkerRepository markerRepository, TagRepository tagRepository, TeamNewsRepository teamNewsRepository, NewsTagRepository newsTagRepository) {
        this.siteRepository = siteRepository;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.markerRepository = markerRepository;
        this.tagRepository = tagRepository;
        this.teamNewsRepository = teamNewsRepository;
        this.newsTagRepository = newsTagRepository;
    }

    public void getNews() {
        for (String italianTeam : italianTeams) {
            Document footballItaliaMainDoc;
            try {
                footballItaliaMainDoc = Jsoup.connect("https://www.football-italia.net/clubs/" + italianTeam + "/news").get();
                List<String> tmpNewsUrls = footballItaliaMainDoc.body().getElementsByClass("news-idx-item-title").select("a").eachAttr("href");
                List<String> newsUrls = new ArrayList<>();
                String footballItaliaSiteUrl = "https://www.football-italia.net";
                List<Document> docs = new ArrayList<>();
                List<Long> newsIds = new ArrayList<>();

                Optional<Site> site = siteRepository.findByName("Football Italia");
                if (site.isPresent()) {
                    for (String tmpNewsUrl : tmpNewsUrls) {
                        Long newsId = Long.parseLong(tmpNewsUrl.split("/")[1]);
                        if (!newsRepository.existsBySiteIdAndId(site.get().getId(), newsId)) {
                            String articleLink = footballItaliaSiteUrl + tmpNewsUrl;
                            newsUrls.add(articleLink);
                            newsIds.add(newsId);
                            try{
                                docs.add(Jsoup.connect(articleLink).get());
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }

                for (Document doc : docs) {
                    int index = docs.indexOf(doc);
                    String title = doc.getElementsByClass("title").text();
                    String imgUrl = doc.getElementsByClass("story-image-wrapper").select("img").first().attr("src");
                    String[] date = doc.getElementsByClass("date").html().split(" ");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH);
                    LocalDate localDate = LocalDate.parse(date[1] + " " + date[2] + " " + date[3], formatter);
                    String content = doc.body().getElementsByClass("content").select("p").text();
                    String footballItaliaEndNewsSyntax = "Watch Serie A live in the UK on Premier Sports for just £9.99 per month including live LaLiga, Eredivisie, Scottish Cup Football and more. Visit: https://www.premiersports.com/subscribenow";
                    String endContent = content.replace(footballItaliaEndNewsSyntax, "");
                    List<Marker> markers = markerRepository.findAll();
                    Set<Tag> tagSet = new HashSet<>(ParserHelper.getTags(markers, endContent, tagRepository));

                    if (!newsRepository.existsBySiteIdAndId(newsIds.get(index), site.get().getId())) {
                        News news = new News();
                        news.setSiteId(site.get().getId());
                        news.setId(newsIds.get(index));
                        news.setTitle(title);
                        news.setNewsUrl(newsUrls.get(index));
                        news.setImageUrl(imgUrl);
                        news.setSite(site.get());
                        news.setDate(localDate);
//                        news.setTags(tagSet);
                        newsRepository.save(news);
                        for (Tag tag :
                                tagSet) {
                            NewsTag newsTag = new NewsTag();
                            newsTag.setNews(news);
                            newsTag.setTag(tag);
                            newsTagRepository.save(newsTag);
                        }
//                        List<Team> teams = teamRepository.findAll();
                        ParserHelper.connectNewsWithTeams(tagSet, news, teamNewsRepository, markerRepository);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
