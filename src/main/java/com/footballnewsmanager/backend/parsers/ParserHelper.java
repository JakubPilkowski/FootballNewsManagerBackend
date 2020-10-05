package com.footballnewsmanager.backend.parsers;

import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class ParserHelper {


    public static void connectNewsWithTeams(Set<Tag> tagSet, News news, TeamNewsRepository teamNewsRepository, MarkerRepository markerRepository) {

        for (Tag tag : tagSet) {
            Optional<Marker> marker = markerRepository.findByName(tag.getName());
            if (marker.isPresent()) {
                Set<Team> teamSet = marker.get().getTeams();
                for (Team teamFromMarker : teamSet) {
                    if (!teamNewsRepository.existsByTeamAndNews(teamFromMarker, news)) {
                        TeamNews teamNews = new TeamNews();
                        teamNews.setNews(news);
                        teamNews.setTeam(teamFromMarker);
                        teamNewsRepository.save(teamNews);
                    }
                }
            }
        }

//        for (Team team : teams) {
//            Set<Marker> markerList = team.getMarkers();
//            for (Marker marker :
//                    markerList) {
//                for (Tag tag : tagSet) {
//                    if (tag.getName().equals(marker.getName())) {
//                        Set<Team> teamSet = marker.getTeams();
//                        for (Team teamFromMarker : teamSet) {
//                            if (!teamNewsRepository.existsByTeamAndNews(teamFromMarker, news)) {
//                                TeamNews teamNews = new TeamNews();
//                                teamNews.setNews(news);
//                                teamNews.setTeam(teamFromMarker);
//                                teamNewsRepository.save(teamNews);
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    public static Set<Tag> getTags(List<Marker> markers, String article, TagRepository tagRepository) {
        Set<Tag> tagSet = new HashSet<>();
        for (Marker marker :
                markers) {
            if (article.contains(marker.getName())) {
                if (!tagRepository.existsByName(marker.getName())) {
                    Tag tag = new Tag();
                    tag.setName(marker.getName());
                    tagSet.add(tag);
                    tagRepository.save(tag);
                } else {
                    Optional<Tag> tagOptional = tagRepository.findByName(marker.getName());
                    tagOptional.ifPresent(tagSet::add);
                }
            }
        }
        return tagSet;
    }


}
