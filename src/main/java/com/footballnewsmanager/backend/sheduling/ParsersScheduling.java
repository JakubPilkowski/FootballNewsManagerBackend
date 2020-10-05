package com.footballnewsmanager.backend.sheduling;

import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import com.footballnewsmanager.backend.parsers.transfery_info.TransferyInfoParser;
import com.footballnewsmanager.backend.repositories.NewsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;

@Component
public class ParsersScheduling {

    private final Football_Italia_Parser footballItaliaParser;
    private final TransferyInfoParser transferyInfoParser;
    private final NewsRepository newsRepository;

    public ParsersScheduling(Football_Italia_Parser footballItaliaParser, TransferyInfoParser transferyInfoParser, NewsRepository newsRepository) {
        this.footballItaliaParser = footballItaliaParser;
        this.transferyInfoParser = transferyInfoParser;
        this.newsRepository = newsRepository;
    }

    @Scheduled(cron = "0 0 11,15,19,22 * * *")
    public void uploadFootballItaliaNews() {
        footballItaliaParser.getNews();
    }

    @Scheduled(cron = "0 0 0,8,10,12,14,16,18,20,22 * * *")
    public void uploadTransferyInfoNews() {
        transferyInfoParser.getNews();
    }

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void deleteOldNews(){
        newsRepository.deleteByDateLessThan(LocalDate.now().minus(Period.ofDays(7)));
    }
}
