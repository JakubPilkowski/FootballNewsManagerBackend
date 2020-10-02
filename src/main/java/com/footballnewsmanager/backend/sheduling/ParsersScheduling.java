package com.footballnewsmanager.backend.sheduling;

import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import com.footballnewsmanager.backend.parsers.transfery_info.TransferyInfoParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ParsersScheduling {

    private final Football_Italia_Parser footballItaliaParser;
    private final TransferyInfoParser transferyInfoParser;

    public ParsersScheduling(Football_Italia_Parser footballItaliaParser, TransferyInfoParser transferyInfoParser) {
        this.footballItaliaParser = footballItaliaParser;
        this.transferyInfoParser = transferyInfoParser;
    }

    @Scheduled(cron = "0 0 11,15,19,22 * * *")
    public void uploadFootballItaliaNews() {
        footballItaliaParser.getNews();
    }

    @Scheduled(cron = "0 0 0,8,10,12,14,16,18,20,22 * * *")
    public void uploadTransferyInfoNews() {
        transferyInfoParser.getNews();
    }


//    @Scheduled(cron = "* 0 0 * * *")
//    public void deleteOldNews(){
//
//    }
}
