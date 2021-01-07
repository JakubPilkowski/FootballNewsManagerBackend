package com.footballnewsmanager.backend.sheduling;

import com.footballnewsmanager.backend.models.Marker;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import com.footballnewsmanager.backend.parsers.sport_pl.SportPlParser;
import com.footballnewsmanager.backend.parsers.transfery_info.TransferyInfoParser;
import com.footballnewsmanager.backend.repositories.MarkerRepository;
import com.footballnewsmanager.backend.repositories.NewsRepository;
import com.footballnewsmanager.backend.repositories.UserRepository;
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
    private final UserRepository userRepository;
    private final SportPlParser sportPlParser;

    public ParsersScheduling(Football_Italia_Parser footballItaliaParser, TransferyInfoParser transferyInfoParser, NewsRepository newsRepository, MarkerRepository markerRepository, UserRepository userRepository, SportPlParser sportPlParser) {
        this.footballItaliaParser = footballItaliaParser;
        this.transferyInfoParser = transferyInfoParser;
        this.newsRepository = newsRepository;
        this.markerRepository = markerRepository;
        this.userRepository = userRepository;
        this.sportPlParser = sportPlParser;
    }

    @Scheduled(cron = "0 0,20,40 0,8-23 * * *")
    public void uploadTransferyInfoNews() {
        List<Marker> markerList = markerRepository.findAll();
        List<User> users = userRepository.findAll();
//        transferyInfoParser.getNews(markerList, users);
//        footballItaliaParser.getNews(markerList, users);
//        sportPlParser.getNews(markerList, users);
    }

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void deleteOldNews(){
        LocalDate localDate = LocalDate.now().minusDays(7);
        LocalDateTime localDateTime= localDate.atStartOfDay();
        newsRepository.deleteByDateLessThan(localDateTime);
    }
}
