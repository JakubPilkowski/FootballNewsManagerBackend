package com.footballnewsmanager.backend.sheduling;


import com.footballnewsmanager.backend.helpers.LeaguesHelper;
import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import com.footballnewsmanager.backend.parsers.transfery_info.TransferyInfoParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LeaguesScheduling {

    private final LeaguesHelper leaguesHelper;

    public LeaguesScheduling(LeaguesHelper leaguesHelper) {
        this.leaguesHelper = leaguesHelper;
    }

    @Scheduled(cron = "0 0 0 30 7 *")
    public void updateLeagues(){
        leaguesHelper.updateTeams();
    }
}
