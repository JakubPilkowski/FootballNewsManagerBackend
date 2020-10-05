package com.footballnewsmanager.backend.sheduling;


import com.footballnewsmanager.backend.helpers.LeaguesHelper;
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
