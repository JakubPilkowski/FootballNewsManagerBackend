package com.footballnewsmanager.backend.parsers.transfery_info;

import com.footballnewsmanager.backend.repositories.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransferyInfoParser {

    private final SiteRepository siteRepository;
    private final NewsRepository newsRepository;
    private final TeamRepository teamRepository;
    private final MarkerRepository markerRepository;
    private final TagRepository tagRepository;
    private final TeamNewsRepository teamNewsRepository;

    public TransferyInfoParser(SiteRepository siteRepository, NewsRepository newsRepository, TeamRepository teamRepository, MarkerRepository markerRepository, TagRepository tagRepository, TeamNewsRepository teamNewsRepository) {
        this.siteRepository = siteRepository;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.markerRepository = markerRepository;
        this.tagRepository = tagRepository;
        this.teamNewsRepository = teamNewsRepository;
    }


    public void getNews(){
        Document transferyInfoMainDoc;
        String tranferyInfoMainUrl = "https://transfery.info";
        try{
            transferyInfoMainDoc = Jsoup.connect("https://transfery.info/aktualnosci").get();
//            System.out.println(transferyInfoMainDoc.getElementsByClass("article-links").html());
            List<String> titles = transferyInfoMainDoc.getElementsByClass("article-link-description").eachText();
            List<String> newsUrls = transferyInfoMainDoc.getElementsByClass("article-links").select("a").eachAttr("href");
            List<Integer> newsIds = new ArrayList<>();
            List<Document> docs = new ArrayList<>();
            List<String> imgUrls = new ArrayList<>();
            //            for (String title :
//                    titles) {
//                System.out.println(title);
//            }
            for (int i=0; i<newsUrls.size(); i++) {
                newsIds.add(Integer.parseInt(newsUrls.get(i).split("/")[3]));
                String articleLink = tranferyInfoMainUrl+newsUrls.get(i);
                newsUrls.set(i, articleLink);
                docs.add(Jsoup.connect(articleLink).get());
            }
            for (Document doc :
                    docs) {
                String imgUrl = doc.getElementsByTag("article").select("picture").get(0).select("source").get(1).attr("srcset");
                imgUrls.add(tranferyInfoMainUrl+"/"+imgUrl);
                System.out.println(tranferyInfoMainUrl+"/"+imgUrl);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }


}
