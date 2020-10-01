package com.footballnewsmanager.backend.parsers.football_italia;

import com.footballnewsmanager.backend.models.*;
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
    private final MarkerRepository markerRepository;
    private final TagRepository tagRepository;

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

    public Football_Italia_Parser(SiteRepository siteRepository, NewsRepository newsRepository, MarkerRepository markerRepository, TagRepository tagRepository) {
        this.siteRepository = siteRepository;
        this.newsRepository = newsRepository;
        this.markerRepository = markerRepository;
        this.tagRepository = tagRepository;
    }

    public void getNews(){
        for (String italianTeam : italianTeams) {
            Document footballItaliaMainDoc;
            try {
                footballItaliaMainDoc = Jsoup.connect("https://www.football-italia.net/clubs/"+italianTeam+"/news").get();
                List<String> titles = footballItaliaMainDoc.body().getElementsByClass("news-idx-item-title").eachText();
                List<String> newsUrls = footballItaliaMainDoc.body().getElementsByClass("news-idx-item-title").select("a").eachAttr("href");
                String footballItaliaSiteUrl = "https://www.football-italia.net";
                List<String> imageUrls = new ArrayList<>();
                List<Document> docs = new ArrayList<>();
                List<Integer> news_ids = new ArrayList<>();
                List<LocalDate> dates = new ArrayList<>();
                List<String> contents = new ArrayList<>();

                for (int i = 0; i < newsUrls.size(); i++) {
                    String news_id = newsUrls.get(i).split("/")[1];
                    news_ids.add(Integer.parseInt(news_id));
                    String fullUrl = footballItaliaSiteUrl + newsUrls.get(i);
                    newsUrls.set(i, fullUrl);
                    docs.add(Jsoup.connect(fullUrl).get());
                }
                for (Document doc : docs) {
                    imageUrls.add(doc.body().getElementsByClass("story-image-wrapper").select("img").first().attr("src"));
                    String[] date = doc.body().getElementsByClass("date").html().split(" ");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH);
                    LocalDate localDate = LocalDate.parse(date[1] + " " + date[2] + " " + date[3], formatter);
                    dates.add(localDate);
                    contents.add(doc.body().getElementsByClass("content").select("p").text());
                }
                for (int i = 0; i < titles.size(); i++) {
                    Set<Tag> tags = new HashSet<>();
                    String footballItaliaEndNewsSyntax = "Watch Serie A live in the UK on Premier Sports for just £9.99 per month including live LaLiga, Eredivisie, Scottish Cup Football and more. Visit: https://www.premiersports.com/subscribenow";
                    String content = contents.get(i).replace(footballItaliaEndNewsSyntax, "");
                    List<Marker> markers = markerRepository.findAll();
                    for (Marker marker : markers) {
                        if (content.contains(marker.getName())) {
                            Tag tag = new Tag();
                            tag.setName(marker.getName());
                            if (!tagRepository.existsByName(tag.getName())) {
                                tagRepository.save(tag);
                                tags.add(tag);
                            } else {
                                Optional<Tag> tmpTag = tagRepository.findByName(tag.getName());
                                tmpTag.ifPresent(tags::add);
                            }
                        }
                    }

                    Optional<Site> site = siteRepository.findByName("Football Italia");
                    String title = titles.get(i);
                    String imageUrl = imageUrls.get(i);
                    String newsUrl = newsUrls.get(i);
                    News news = new News();
                    site.ifPresent(news::setSite);
                    site.ifPresent(value -> news.setNewsSiteId(value.getId()));
                    news.setNewsId(news_ids.get(i));
                    if(newsRepository.existsByNewsSiteId(news.getNewsSiteId()) && newsRepository.existsByNewsId(news.getNewsId())){
                        break;
                    }
                    else{
                        news.setTitle(title);
                        news.setNewsUrl(newsUrl);
                        news.setImageUrl(imageUrl);
                        news.setDate(dates.get(i));
                        news.setTags(tags);
                        newsRepository.save(news);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
