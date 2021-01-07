package com.footballnewsmanager.backend.parsers.sportowe_fakty;

import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.ParserHelper;
import com.footballnewsmanager.backend.repositories.*;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Component
public class SportoweFaktyParser {
    private final SiteRepository siteRepository;
    private final NewsRepository newsRepository;
    private final TeamRepository teamRepository;
    private final MarkerRepository markerRepository;
    private final TagRepository tagRepository;
    private final TeamNewsRepository teamNewsRepository;
    private final NewsTagRepository newsTagRepository;
    private final UserTeamRepository userTeamRepository;
    private final UserNewsRepository userNewsRepository;

    public SportoweFaktyParser(SiteRepository siteRepository, NewsRepository newsRepository, TeamRepository teamRepository,
                               MarkerRepository markerRepository, TagRepository tagRepository, TeamNewsRepository teamNewsRepository,
                               NewsTagRepository newsTagRepository, UserTeamRepository userTeamRepository,
                               UserNewsRepository userNewsRepository) {
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
        Document sportoweFaktyMainDoc;
        String sportoweFaktyMainUrl = "https://sportowefakty.wp.pl";
        String sportoweFaktySportUrl = "https://sportowefakty.wp.pl/pilka-nozna";
        Site site = siteRepository.findById(4L).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej strony"));
        try {
            sportoweFaktyMainDoc = Jsoup.connect(sportoweFaktySportUrl).get();

            List<String> tmp = sportoweFaktyMainDoc.getElementsByClass("streamshort__title").select("a").eachAttr("href");
            System.out.println(tmp.get(3));

            Long newsId = Long.valueOf(tmp.get(3).split("/")[2]);
            System.out.println(newsId);
            if (!newsRepository.existsBySiteIdAndId(site.getId(), newsId)) {
                String fullNewsUrl = sportoweFaktyMainUrl + tmp.get(3);
                try {
                    Document doc = Jsoup.connect(fullNewsUrl).get();
//                parseNewsAndSave(site, doc, markers, newsId, tmpNewsUrl, users);
                    Elements mainElement = doc.getElementsByTag("article");
                    String title = mainElement.select("h1").text();
                    String img = mainElement.get(0).getElementsByClass("image").select("img").attr("data-lsrc");
                    String date = mainElement.select("time").attr("datetime");
                    System.out.println(title);
                    System.out.println(img);
                    LocalDateTime localDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    System.out.println(localDate);
                    Elements elements = mainElement.get(0).getElementsByTag("p");
                    for(Element element: elements.select("strong")) {
                        element.remove();
                    }
                    for(Element element: elements.select("a")){
                        element.remove();
                    }
                    String tagsContent = elements.text();
                    String secondaryTitle = mainElement.select("span").attr("class","h5").text();
                    String tagsContentFull = title+" "+secondaryTitle+" "+tagsContent;
                    System.out.println(tagsContent);
                    System.out.println(tagsContentFull);
                    Set<Tag> tagSet = new HashSet<>(ParserHelper.getTags(markers, tagsContentFull.toLowerCase(), tagRepository));
//                    tagSet.addAll(ParserHelper.getTags(markers, tagsContentFromA.replaceAll("[/-]", " "), tagRepository));
                    for (Tag tag : tagSet) {
                        System.out.println(tag.getName());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
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
            Set<Tag> tagSet = new HashSet<>(ParserHelper.getTags(markers, tags, tagRepository));
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
                    userTeamRepository, userNewsRepository);
        }
    }
}
