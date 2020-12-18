package com.footballnewsmanager.backend.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.news.*;
import com.footballnewsmanager.backend.api.response.search.SearchResponse;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import com.footballnewsmanager.backend.parsers.transfery_info.TransferyInfoParser;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.services.PaginationService;
import com.footballnewsmanager.backend.services.UserService;
import com.footballnewsmanager.backend.views.Views;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/news")
@Validated
public class NewsController {


    private final Football_Italia_Parser footballItaliaParser;
    private final TransferyInfoParser transferyInfoParser;
    private final NewsRepository newsRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final MarkerRepository markerRepository;
    private final UserRepository userRepository;
    private final FavouriteTeamRepository favouriteTeamRepository;
    private final UserNewsRepository userNewsRepository;

    public NewsController(Football_Italia_Parser footballItaliaParser, TransferyInfoParser transferyInfoParser, NewsRepository newsRepository, TeamRepository teamRepository, UserService userService, MarkerRepository markerRepository, UserRepository userRepository, FavouriteTeamRepository favouriteTeamRepository, UserNewsRepository userNewsRepository) {
        this.footballItaliaParser = footballItaliaParser;
        this.transferyInfoParser = transferyInfoParser;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.markerRepository = markerRepository;
        this.userRepository = userRepository;
        this.favouriteTeamRepository = favouriteTeamRepository;
        this.userNewsRepository = userNewsRepository;
    }

    @GetMapping(value = "", params = {"page"})
    @JsonView(Views.Internal.class)
    public ResponseEntity<NewsResponse> getNewsForTeams(@RequestParam("page") int page) {
        NewsResponse newsResponse = new NewsResponse();
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            Pageable newsPageable = PageRequest.of(page, 15, Sort.by(Sort.Order.asc("badged"), Sort.Order.desc("news.date"),
                    Sort.Order.desc("news.highlighted"), Sort.Order.desc("news.popularity")));

            Page<UserNews> news = userNewsRepository.findByUserAndInFavouritesIsTrue(user, newsPageable)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma wiadomości dla wybranych drużyn"));
            PaginationService.handlePaginationErrors(page, news);

            newsResponse.setUserNews(news.getContent());
            newsResponse.setPages(news.getTotalPages());
            newsResponse.setNewsCount(news.getTotalElements());
            Long count = userNewsRepository.countDistinctByUserAndInFavouritesIsTrueAndNewsDateAfter
                    (user, LocalDate.now().atStartOfDay());
            newsResponse.setNewsToday(count);
            return user;
        });

        return ResponseEntity.ok(newsResponse);
    }


    @GetMapping(value = "all", params = {"page"})
    @JsonView(Views.Internal.class)
    public ResponseEntity<AllNewsResponse<BaseNewsAdjustment>> getNews(@RequestParam("page") @Min(value = 0) int page) {

        AllNewsResponse<BaseNewsAdjustment> allNewsResponse = new AllNewsResponse<>();

        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            List<Team> teams = new ArrayList<>();
            List<FavouriteTeam> favouriteTeams = favouriteTeamRepository.findByUser(user)
                    .orElse(new ArrayList<>());

            for (FavouriteTeam favouriteTeam : favouriteTeams) {
                teams.add(favouriteTeam.getTeam());
            }

            Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "news.date",
                    "news.highlighted", "news.popularity"));

            Page<UserNews> news = userNewsRepository.findAllByUser(user, pageable)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma newsów dla użytkownika"));
            PaginationService.handlePaginationErrors(page, news);

            switch ((page + 2) % 2) {
                case 0:
                    Pageable proposedNewsPageable = PageRequest.of(page, 1, Sort.by(Sort.Direction.DESC,
                            "highlighted",
                            "popularity"));
                    Pageable pageable1 = PageRequest.of(0, Integer.MAX_VALUE);
                    Page<News> allNewsForTeams = newsRepository.findDistinctByTeamNewsTeamIn(teams, pageable1).orElseThrow(() -> new ResourceNotFoundException(""));
                    Page<Team> pageableTeamsForSelectedNews = teamRepository.findByTeamNewsNewsIn(allNewsForTeams.getContent(), pageable1).orElseThrow(() -> new ResourceNotFoundException(""));

                    List<Team> teamsToRemove = new ArrayList<>();
                    List<Team> teamsForSelectedNews = new ArrayList<>();
                    teamsForSelectedNews.addAll(pageableTeamsForSelectedNews.getContent());

                    for (Team team : teamsForSelectedNews) {
                        for (Team requestTeam : teams) {
                            if (requestTeam.getName().equals(team.getName())) {
                                teamsToRemove.add(team);
                            }
                        }
                    }
                    for (Team teamToRemove : teamsToRemove) {
                        teamsForSelectedNews.remove(teamToRemove);
                    }
                    Random random = new Random();
                    System.out.println("Teams size" + teamsForSelectedNews.size());
                    NewsExtras proposedNewsExtras;
                    if(teamsForSelectedNews.size() > 0 ) {
                        int index = random.nextInt(teamsForSelectedNews.size());
                        Team randomizedTeam = teamsForSelectedNews.get(index);
                        Page<News> proposedNews = newsRepository.findByTeamNewsTeam(randomizedTeam, proposedNewsPageable).orElseThrow(() -> new ResourceNotFoundException(""));
                        proposedNewsExtras = new NewsExtras("To może ci się spodobać", NewsInfoType.PROPOSED_NEWS,
                                proposedNews.getContent().get(0));
                    }
                    else{
                        proposedNewsExtras = new NewsExtras("To może ci się spodobać", NewsInfoType.PROPOSED_NEWS,
                                news.getContent().get(0).getNews());
                    }
                    allNewsResponse.setAdditionalContent(proposedNewsExtras);
                    break;
                case 1:
                    Pageable teamsPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "popularity"));
                    Page<Team> hotTeams = teamRepository.findAll(teamsPageable);
                    TeamExtras teamExtras = new TeamExtras("Polecane drużyny", NewsInfoType.PROPOSED_TEAMS,
                            hotTeams.getContent());
                    allNewsResponse.setAdditionalContent(teamExtras);
                    break;
            }
            allNewsResponse.setPages(news.getTotalPages());
            allNewsResponse.setNewsCount(news.getTotalElements());
            Long count = userNewsRepository.countDistinctByUserAndNewsDateAfter
                    (user, LocalDate.now().atStartOfDay());
            allNewsResponse.setNewsToday(count);
            allNewsResponse.setUserNews(news.getContent());
            return user;
        });

        return ResponseEntity.ok().body(allNewsResponse);
    }

    @GetMapping("site={sid}/id={id}")
    @JsonView(Views.Public.class)
    public ResponseEntity<News> getNewsByIds(@PathVariable("sid") @Min(value = 0) Long sid,
                                             @PathVariable("id") @Min(value = 0) Long id) {
        News news = newsRepository.findBySiteIdAndId(sid, id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej wiadomości"));
        return ResponseEntity.ok(news);
    }

    @GetMapping(value = "/team={id}", params = {"page"})
    @JsonView(Views.Internal.class)
    public ResponseEntity<NewsResponse> getNewsForTeam(@PathVariable("id") @Min(value = 0) Long id, @RequestParam("page") @Min(value = 0) int page) {

        NewsResponse newsResponse = new NewsResponse();
        Team team = teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej drużyny"));
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "date", "highlighted", "popularity"));
            Page<News> news = newsRepository.findByTeamNewsTeam(team, pageable).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej newsów dla tej drużyny"));
            Pageable userNewsPageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "news.date",
                    "news.highlighted", "news.popularity"));
            PaginationService.handlePaginationErrors(page, news);
            Page<UserNews> userNews = userNewsRepository.findByUserAndNewsIn(user, news.getContent(), userNewsPageable)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma wiadomości dla podanej drużyny"));
            newsResponse.setUserNews(userNews.getContent());
            return user;
        });
        return ResponseEntity.ok().body(newsResponse);
    }

    @GetMapping("notifications")
    @JsonView(Views.Internal.class)
    public ResponseEntity<Long> getNotifications() {
        User user = userService.checkUserExistByTokenAndOnSuccess(userRepository, userRes -> userRes);
        Long count = userNewsRepository.countByUserAndInFavouritesIsTrueAndBadgedIsFalse(user);
        return ResponseEntity.ok(count);
    }

    @GetMapping("markAllAsVisited")
    public ResponseEntity<BaseResponse> markAllAsVisited() {
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            Long count = userNewsRepository.countByUserAndInFavouritesIsTrue(user);
            Pageable pageable = PageRequest.of(0, count.intValue());
            Page<UserNews> news = userNewsRepository.findByUserAndInFavouritesIsTrue(user, pageable)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma wiadomości"));
            PaginationService.handlePaginationErrors(0, news);
            for (UserNews singleNews : news.getContent()) {
                singleNews.setVisited(true);
                singleNews.setBadged(true);
                userNewsRepository.save(singleNews);
            }
            return user;
        });

        return ResponseEntity.ok(new BaseResponse(true, "Zaznaczono wszystkie jako przeczytane"));
    }

    @GetMapping("notVisitedNewsAmount")
    public ResponseEntity<BadgesResponse> getNotVisitedNewsAmount() {
        AtomicReference<BadgesResponse> badgesResponse = new AtomicReference<>();
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            Long amount = userNewsRepository.countByUserAndInFavouritesIsTrueAndBadgedIsFalse(user);
            badgesResponse.set(new BadgesResponse(true, "Ilość nieprzeczytanych wiadomości", amount));
            return user;
        });

        return ResponseEntity.ok(badgesResponse.get());
    }

    @PutMapping("visit/site={sid}/id={id}")
    public ResponseEntity<SingleNewsResponse> visitNews(@PathVariable("sid") @Min(value = 0) Long sid,
                                                        @PathVariable("id") @Min(value = 0) Long id) {
        AtomicReference<SingleNewsResponse> singleNewsResponse = new AtomicReference<>();
        News news = newsRepository.findBySiteIdAndId(sid, id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej wiadomości"));
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            UserNews userNews = userNewsRepository.findByUserAndNews(user, news)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej wiadomości"));
            news.setClicks(news.getClicks() + 1);
            news.measurePopularity();
            newsRepository.save(news);
            if (!userNews.isVisited()) {
                userNews.setVisited(true);
                if (userNews.isInFavourites() && !userNews.isBadged()) {
                    userNews.setBadged(true);
                }
                userNewsRepository.save(userNews);
            }
            singleNewsResponse.set(new SingleNewsResponse(true, "Odwiedzono wiadomość", userNews));
            return user;
        });
        return ResponseEntity.ok(singleNewsResponse.get());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("highlight/site={sid}/id={id}")
    public ResponseEntity<BaseResponse> highlightNews(@PathVariable("sid") @Min(value = 0) Long sid,
                                                      @PathVariable("id") @Min(value = 0) Long id) {
        News news = newsRepository.findBySiteIdAndId(sid, id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej wiadomości"));
        news.setHighlighted(!news.isHighlighted());
        newsRepository.save(news);
        String message = news.isHighlighted() ? "Wyróżniono newsa" : "Wycofano wyróżnienie";
        return ResponseEntity.ok(new BaseResponse(true, message));
    }

    @PutMapping("like/site={sid}/id={id}")
    @Transactional
    public ResponseEntity<SingleNewsResponse> likeNews(@PathVariable("sid") @Min(value = 0) Long sid,
                                                       @PathVariable("id") @Min(value = 0) Long id) {
        AtomicReference<SingleNewsResponse> singleNewsResponse = new AtomicReference<>();
        News news = newsRepository.findBySiteIdAndId(sid, id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej wiadomości"));
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            UserNews userNews = userNewsRepository.findByUserAndNews(user, news)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej wiadomości"));

            userNews.setLiked(!userNews.isLiked());
            news.setLikes(news.getLikes() + (userNews.isLiked() ? 1 : -1));
            news.measurePopularity();
            userNewsRepository.save(userNews);
            newsRepository.save(news);
            String message = userNews.isLiked() ? "Poljakowano Wiadomość" : "Odlajkowano Wiadomość";
            singleNewsResponse.set(new SingleNewsResponse(true, message, userNews));
            return user;
        });
        return ResponseEntity.ok(singleNewsResponse.get());
    }

    @GetMapping(value = "hot", params = {"count"})
    @JsonView(Views.Public.class)
    public ResponseEntity<NewsResponse> hotNews(@RequestParam(value = "count", defaultValue = "5") @NotNull @Range(min = 5, max = 10) int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "news.date", "news.popularity"));
        Page<UserNews> news = userNewsRepository.findAll(pageable);
        PaginationService.handlePaginationErrors(0, news);
        NewsResponse newsResponse = new NewsResponse();
        newsResponse.setUserNews(news.getContent());
        return ResponseEntity.ok(newsResponse);
    }

    @GetMapping(value = "query={query}")
    @JsonView(Views.Public.class)
    public ResponseEntity<SearchResponse> getNewsByQuery(@PathVariable("query") @NotNull() String query) {
        Pageable newsPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "date", "popularity"));
        Pageable teamPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "popularity"));

        Page<News> news = newsRepository.findByTitleContainsIgnoreCase(query, newsPageable).orElseThrow(() -> new ResourceNotFoundException("Dla podanego hasła nie ma żadnej drużyny"));
        Page<Team> teams = teamRepository.findByNameContainsIgnoreCase(query, teamPageable).orElseThrow(() -> new ResourceNotFoundException("Dla podanego hasła nie ma żadnej drużyny"));

        List<SearchResult> results = new ArrayList<>();

        for (News queryNews : news) {
            SearchResult searchResult = new SearchResult();
            searchResult.setName(queryNews.getTitle());
            searchResult.setType(SearchType.NEWS);
            searchResult.setId(queryNews.getSiteId() + queryNews.getId());
            searchResult.setImgUrl(queryNews.getImageUrl());
            searchResult.setNewsUrl(queryNews.getNewsUrl());
            results.add(searchResult);
        }
        for (Team team : teams) {
            SearchResult searchResult = new SearchResult();
            searchResult.setId(team.getId());
            searchResult.setImgUrl(team.getLogoUrl());
            searchResult.setName(team.getName());
            searchResult.setNewsUrl("");
            searchResult.setType(SearchType.TEAM);
            results.add(searchResult);
        }

        if (teams.getTotalElements() == 0 && news.getTotalElements() == 0)
            throw new ResourceNotFoundException("Dla podanej frazy nie ma żadnej drużyny");
        return ResponseEntity.ok(new SearchResponse(results));
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/tmpAddNews")
    @Transactional()
    public String addNewsFromFootballItalia() {
        List<Marker> markers = markerRepository.findAll();
        List<User> users = userRepository.findAll();
        footballItaliaParser.getNews(markers, users);
        transferyInfoParser.getNews(markers, users);
        return "success";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/tmp")
    @Transactional()
    public void deleteOldNews(){
//        LocalDateTime localDateTime = LocalDateTime.now();
        LocalTime localTime = LocalTime.now();
        String formattedLocalTime = localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println(formattedLocalTime);
//        LocalDate localDate = LocalDate.now().minusDays(7);
//        LocalDateTime localDateTime= localDate.atStartOfDay();
//        newsRepository.deleteByDateLessThan(localDateTime);
    }

}
