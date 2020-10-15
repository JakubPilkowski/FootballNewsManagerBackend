package com.footballnewsmanager.backend.parsers.transfery_info;

import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.ParserHelper;
import com.footballnewsmanager.backend.repositories.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
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

    public TransferyInfoParser(SiteRepository siteRepository, NewsRepository newsRepository, TeamRepository teamRepository, MarkerRepository markerRepository, TagRepository tagRepository, TeamNewsRepository teamNewsRepository, NewsTagRepository newsTagRepository) {
        this.siteRepository = siteRepository;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.markerRepository = markerRepository;
        this.tagRepository = tagRepository;
        this.teamNewsRepository = teamNewsRepository;
        this.newsTagRepository = newsTagRepository;
    }


    public void getNews() {
        Document transferyInfoMainDoc;
        String tranferyInfoMainUrl = "https://transfery.info";
        try {
            transferyInfoMainDoc = Jsoup.connect("https://transfery.info/aktualnosci").get();
            List<String> tmpNewsUrls = transferyInfoMainDoc.getElementsByClass("article-links").select("a").eachAttr("href");
            List<String> newsUrls = new ArrayList<>();
            List<Long> newsIds = new ArrayList<>();
            List<Document> docs = new ArrayList<>();
            Optional<Site> site = siteRepository.findByName("Transfery.info");
            if (site.isPresent()) {
                for (String tmpNewsUrl : tmpNewsUrls) {
                    Long newsId = Long.parseLong(tmpNewsUrl.split("/")[3]);
                    if (!newsRepository.existsBySiteIdAndId(site.get().getId(), newsId)) {
                        String articleLink = tranferyInfoMainUrl + tmpNewsUrl;
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

            for (Document doc :
                    docs) {
                int index = docs.indexOf(doc);
                Elements articleElement = doc.getElementsByTag("article");
                String title = articleElement.get(0).select("h1").text();
                String imgUrl = tranferyInfoMainUrl+"/"+articleElement.select("picture").get(0).select("source").get(1).attr("srcset");
                String date = articleElement.select("time").text().split(" ")[0];
                LocalDate localDate = LocalDate.parse(date);
                String articleTagSection = doc.getElementsByClass("d-inline").text();

                List<Marker> markers = markerRepository.findAll();
                Set<Tag> tagSet = new HashSet<>(ParserHelper.getTags(markers, articleTagSection, tagRepository));

                if (!newsRepository.existsBySiteIdAndId(newsIds.get(index), site.get().getId())) {
                    News news = new News();
                    news.setSiteId(site.get().getId());
                    news.setId(newsIds.get(index));
                    news.setTitle(title);
                    news.setNewsUrl(newsUrls.get(index));
                    news.setImageUrl(imgUrl);
                    news.setSite(site.get());
                    news.setDate(localDate);
                    System.out.println(news.getTitle());
                    newsRepository.save(news);

                    for (Tag tag :
                            tagSet) {
                        NewsTag newsTag = new NewsTag();
                        newsTag.setNews(news);
                        newsTag.setTag(tag);
                        newsTagRepository.save(newsTag);
                    }

//                    List<Team> teams = teamRepository.findAll();
                    ParserHelper.connectNewsWithTeams(tagSet, news, teamNewsRepository, markerRepository);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
