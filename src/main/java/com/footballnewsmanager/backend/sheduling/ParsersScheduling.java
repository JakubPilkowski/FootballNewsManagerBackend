package com.footballnewsmanager.backend.sheduling;

import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import com.footballnewsmanager.backend.parsers.interia_parser.InteriaParser;
import com.footballnewsmanager.backend.parsers.sport_pl.SportPlParser;
import com.footballnewsmanager.backend.parsers.sportowe_fakty.SportoweFaktyParser;
import com.footballnewsmanager.backend.parsers.transfery_info.TransferyInfoParser;
import com.footballnewsmanager.backend.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ParsersScheduling {

    private final Football_Italia_Parser footballItaliaParser;
    private final TransferyInfoParser transferyInfoParser;
    private final NewsRepository newsRepository;
    private final MarkerRepository markerRepository;
    private final TeamNewsRepository teamNewsRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final SiteRepository siteRepository;
    private final SportPlParser sportPlParser;
    private final SportoweFaktyParser sportoweFaktyParser;
    private final InteriaParser interiaParser;

    public ParsersScheduling(Football_Italia_Parser footballItaliaParser, TransferyInfoParser transferyInfoParser, NewsRepository newsRepository, MarkerRepository markerRepository, TeamNewsRepository teamNewsRepository, UserRepository userRepository, TeamRepository teamRepository, SiteRepository siteRepository, SportPlParser sportPlParser, SportoweFaktyParser sportoweFaktyParser, InteriaParser interiaParser) {
        this.footballItaliaParser = footballItaliaParser;
        this.transferyInfoParser = transferyInfoParser;
        this.newsRepository = newsRepository;
        this.markerRepository = markerRepository;
        this.teamNewsRepository = teamNewsRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.siteRepository = siteRepository;
        this.sportPlParser = sportPlParser;
        this.sportoweFaktyParser = sportoweFaktyParser;
        this.interiaParser = interiaParser;
    }

    @Scheduled(cron = "0 0,20,40 0,8-23 * * *")
    public void uploadTransferyInfoNews() {
        List<Marker> markerList = markerRepository.findAll();
        List<User> users = userRepository.findAll();
        footballItaliaParser.getNews(markerList, users);
        transferyInfoParser.getNews(markerList, users);
        sportPlParser.getNews(markerList, users);
        sportoweFaktyParser.getNews(markerList, users);
        interiaParser.getNews(markerList, users);
    }

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void deleteOldNews(){
        LocalDate localDate = LocalDate.now().minusDays(7);
        LocalDateTime localDateTime= localDate.atStartOfDay();

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<News> newsList = newsRepository.findByDateLessThan(localDateTime, pageable);

        for(News news: newsList){
            for(TeamNews teamNews: news.getTeamNews()){
                Team team = teamNews.getTeam();
                team.setNewsCount(team.getNewsCount()-1);
                team.measurePopularity();
                teamRepository.save(team);
            }
            Site site = news.getSite();
            site.setNewsCount(site.getNewsCount()-1);
            site.measurePopularity();
            siteRepository.save(site);
        }

        newsRepository.deleteByDateLessThan(localDateTime);

    }
}
