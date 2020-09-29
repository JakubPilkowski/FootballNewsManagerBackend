package com.footballnewsmanager.backend.parsers.football_italia;

import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Component
public class Football_Italia_Parser {


    private SiteRepository siteRepository;
    private NewsRepository newsRepository;
    private MarkerRepository markerRepository;
    private TagRepository tagRepository;

    private String footballItaliaEndNewsSyntax = "Watch Serie A live in the UK on Premier Sports for just £9.99 per month including live LaLiga, Eredivisie, Scottish Cup Football and more. Visit: https://www.premiersports.com/subscribenow";

    private List<String> italianTeams = new ArrayList<>(Arrays.asList(
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

    public Set<News> getNews(){
        Set<News> newsList = new HashSet<>();
//        Optional<League> league = leagueRepository.findByName("Serie A");
//        if(league.isPresent()){
//            Optional<List<Team>> italianTeams = teamRepository.findByLeague(league.get());
//            if(italianTeams.isPresent()){
//                for (Team italianTeam : italianTeams.get()) {
//                    System.out.println(italianTeam.getName());
//                }
//            }
//        }

        Document footballItaliaMainDoc;
        try {
            footballItaliaMainDoc = Jsoup.connect("https://www.football-italia.net/clubs/Napoli/news").get();
            List<String> titles = footballItaliaMainDoc.body().getElementsByClass("news-idx-item-title").eachText();
            List<String> descs = footballItaliaMainDoc.body().getElementsByClass("news-idx-item-body").eachText();
            List<String> newsUrls = footballItaliaMainDoc.body().getElementsByClass("news-idx-item-title").select("a").eachAttr("href");
//            System.out.println(newsUrl);
            String footballItaliaSiteUrl = "https://www.football-italia.net";
            List<String> imageUrls = new ArrayList<>();
            List<Document> docs = new ArrayList<>();
            List<Integer> news_ids = new ArrayList<>();
            List<String> contents = new ArrayList<>();
//            for (String title: titles) {
//                System.out.println(title);
//            }
//            for (String desc: descs) {
//                System.out.println(desc);
//            }
            for(int i=0;i<newsUrls.size();i++){
                String news_id = newsUrls.get(i).split("/")[1];
                news_ids.add(Integer.parseInt(news_id));
                String fullUrl = footballItaliaSiteUrl+ newsUrls.get(i);
                newsUrls.set(i, fullUrl);
                docs.add(Jsoup.connect(fullUrl).get());
            }
            for (Document doc: docs){
                imageUrls.add(doc.body().getElementsByClass("story-image-wrapper").select("img").first().attr("src"));
//                System.out.println(doc.body().getElementsByClass("story-image-wrapper").select("img").first().attr("src"));
//                System.out.println(doc.body().html());
                contents.add(doc.body().getElementsByClass("content").select("p").text());
//                System.out.println(doc.body().getElementsByClass("content").select("p").text());
            }
            for(int i=0; i<titles.size(); i++){
                Set<Tag> tags = new HashSet<>();


                String content = contents.get(i).replace(footballItaliaEndNewsSyntax, "");
//                System.out.println(content);
                List<Marker>markers = markerRepository.findAll();
//
                for (Marker marker: markers) {
                    if(content.contains(marker.getName())){
                        Tag tag = new Tag();
                        tag.setName(marker.getName());
                        if(!tagRepository.existsByName(tag.getName())){
                            tagRepository.save(tag);
                            tags.add(tag);
                        }
                        else{
                            Optional<Tag> tmpTag = tagRepository.findByName(tag.getName());
                            tmpTag.ifPresent(tags::add);
                        }
                    }
                }

//                for (Tag tag:tags) {
//                    System.out.println("Tagi "+ tag.getName());
//                }


                Optional<Site> site = siteRepository.findByName("Football Italia");
                String title = titles.get(i);
                String desc = descs.get(i);
                String imageUrl = imageUrls.get(i);
                String newsUrl = newsUrls.get(i);
                News news = new News();
                site.ifPresent(news::setSite);
                site.ifPresent(value -> news.setNews_site_id(value.getId()));
                news.setNews_id(news_ids.get(i));
                news.setTitle(title);
                news.setNewsUrl(newsUrl);
                news.setImageUrl(imageUrl);
                news.setDate(LocalDate.now());
                news.setTags(tags);
                newsRepository.save(news);
//                News parsedNews = new News(title, desc, newsUrl, imageUrl);
//                news.add(parsedNews);
            }
//            System.out.println(news.get(0).toString());

        }
        catch (IOException e){
            e.printStackTrace();
        }

        return newsList;

    }
}
