package com.footballnewsmanager.backend.sheduling;

import com.footballnewsmanager.backend.models.Marker;
import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import com.footballnewsmanager.backend.parsers.transfery_info.TransferyInfoParser;
import com.footballnewsmanager.backend.repositories.MarkerRepository;
import com.footballnewsmanager.backend.repositories.NewsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
public class ParsersScheduling {

    private final Football_Italia_Parser footballItaliaParser;
    private final TransferyInfoParser transferyInfoParser;
    private final NewsRepository newsRepository;
    private final MarkerRepository markerRepository;

    public ParsersScheduling(Football_Italia_Parser footballItaliaParser, TransferyInfoParser transferyInfoParser, NewsRepository newsRepository, MarkerRepository markerRepository) {
        this.footballItaliaParser = footballItaliaParser;
        this.transferyInfoParser = transferyInfoParser;
        this.newsRepository = newsRepository;
        this.markerRepository = markerRepository;
    }

    @Scheduled(cron = "0 0,30 8-23 * * *")
    public void uploadTransferyInfoNews() {
        List<Marker> markerList = markerRepository.findAll();
        transferyInfoParser.getNews(markerList);
        footballItaliaParser.getNews(markerList);
    }

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void deleteOldNews(){
        newsRepository.deleteByDateLessThan(LocalDate.now().minus(Period.ofDays(14)));
    }
}
