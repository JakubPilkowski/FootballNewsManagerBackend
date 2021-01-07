package com.footballnewsmanager.backend.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.news.*;
import com.footballnewsmanager.backend.api.response.search.SearchResponse;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import com.footballnewsmanager.backend.parsers.sport_pl.SportPlParser;
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
import java.util.*;

@RestController
@RequestMapping("/news")
@Validated
public class NewsController {


    private final Football_Italia_Parser footballItaliaParser;
    private final TransferyInfoParser transferyInfoParser;
    private final SportPlParser sportPlParser;
    private final NewsRepository newsRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final MarkerRepository markerRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final UserNewsRepository userNewsRepository;

    public NewsController(Football_Italia_Parser footballItaliaParser, TransferyInfoParser transferyInfoParser, SportPlParser sportPlParser, NewsRepository newsRepository, TeamRepository teamRepository, UserService userService, MarkerRepository markerRepository, UserRepository userRepository, UserTeamRepository userTeamRepository, UserNewsRepository userNewsRepository) {
        this.footballItaliaParser = footballItaliaParser;
        this.transferyInfoParser = transferyInfoParser;
        this.sportPlParser = sportPlParser;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.markerRepository = markerRepository;
        this.userRepository = userRepository;
        this.userTeamRepository = userTeamRepository;
        this.userNewsRepository = userNewsRepository;
    }

    @GetMapping(value = "", params = {"page"})
    @JsonView(Views.Internal.class)
    public ResponseEntity<NewsResponse> getNewsForTeams(@RequestParam("page") @Min(value = 0) int page) {
        NewsResponse newsResponse = new NewsResponse();
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            Pageable newsPageable = PageRequest.of(page, 15, Sort.by(Sort.Order.asc("badged"), Sort.Order.asc("visited"), Sort.Order.desc("news.date"), Sort.Order.desc("news.popularity")));

            Page<UserNews> news = userNewsRepository.findByUserAndInFavouritesIsTrue(user, newsPageable);
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


    @GetMapping(value = "all", params = {"page", "proposed"})
    @JsonView(Views.Internal.class)
    public ResponseEntity<AllNewsResponse> getNews(@RequestParam("page") @Min(value = 0) int page,
                                                   @RequestParam("proposed") boolean proposed) {
        AllNewsResponse allNewsResponse = new AllNewsResponse();

        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Order.asc("visited"), Sort.Order.desc("news.date"),Sort.Order.desc("news.popularity")));

            Page<UserNews> news = userNewsRepository.findAllByUser(user, pageable);
            PaginationService.handlePaginationErrors(page, news);

            if (proposed) {
                Long count = userTeamRepository.countByUserAndFavouriteIsFalse(user);
                Pageable teamsPageable = PageRequest.of((int) ((page+count) % count), 5, Sort.by(Sort.Direction.DESC, "team.popularity", "team.id"));
                Page<UserTeam> hotTeams = userTeamRepository.findByUserAndFavouriteIsFalse(user, teamsPageable);
                allNewsResponse.setProposedTeams(hotTeams.getContent());
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

    @GetMapping(value = "/liked", params = {"page"})
    @JsonView(Views.Public.class)
    public ResponseEntity<NewsResponse> getLikedNews(@RequestParam("page") @Min(value = 0) int page) {
        NewsResponse newsResponse = new NewsResponse();
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "news.date"));
            Page<UserNews> liked = userNewsRepository.findByUserAndLikedIsTrue(user, pageable);
            newsResponse.setUserNews(liked.getContent());
            newsResponse.setPages(liked.getTotalPages());
            return user;
        });
        return ResponseEntity.ok(newsResponse);
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
            Pageable userNewsPageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "news.date",
                    "news.popularity"));
            Page<UserNews> userNews = userNewsRepository.findByUserAndNewsTeamNewsTeam(user, team, userNewsPageable);
            PaginationService.handlePaginationErrors(page, userNews);
            newsResponse.setUserNews(userNews.getContent());
            newsResponse.setPages(userNews.getTotalPages());
            newsResponse.setNewsCount(userNews.getTotalElements());
            Long count = newsRepository.countDistinctByTeamNewsTeamAndDateAfter(team, LocalDate.now().atStartOfDay());
            newsResponse.setNewsToday(count);
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
            Page<UserNews> news = userNewsRepository.findByUserAndInFavouritesIsTrue(user, pageable);
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

    @PutMapping("badge/site={sid}/id={id}")
    public ResponseEntity<SingleNewsResponse> badgeNews(@PathVariable("sid") @Min(value = 0) Long sid,
                                                        @PathVariable("id") @Min(value = 0) Long id) {
        SingleNewsResponse singleNewsResponse = new SingleNewsResponse();
        News news = newsRepository.findBySiteIdAndId(sid, id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej wiadomości"));
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            UserNews userNews = userNewsRepository.findByUserAndNews(user, news)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej wiadomości"));
            userNews.setVisited(true);
            userNews.setBadged(true);
            userNewsRepository.save(userNews);
            singleNewsResponse.setNews(userNews);
            singleNewsResponse.setType(SingleNewsType.MARK);
            return user;
        });
        return ResponseEntity.ok(singleNewsResponse);
    }


    @PutMapping("visit/site={sid}/id={id}")
    public ResponseEntity<SingleNewsResponse> visitNews(@PathVariable("sid") @Min(value = 0) Long sid,
                                                        @PathVariable("id") @Min(value = 0) Long id) {
        SingleNewsResponse singleNewsResponse = new SingleNewsResponse();
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
            singleNewsResponse.setNews(userNews);
            singleNewsResponse.setType(SingleNewsType.NEWS);
            return user;
        });
        return ResponseEntity.ok(singleNewsResponse);
    }

    @PutMapping("like/site={sid}/id={id}")
    @Transactional
    public ResponseEntity<SingleNewsResponse> likeNews(@PathVariable("sid") @Min(value = 0) Long sid,
                                                       @PathVariable("id") @Min(value = 0) Long id) {
        SingleNewsResponse singleNewsResponse = new SingleNewsResponse();
        News news = newsRepository.findBySiteIdAndId(sid, id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej wiadomości"));
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            UserNews userNews = userNewsRepository.findByUserAndNews(user, news)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej wiadomości"));

            userNews.setLiked(!userNews.isLiked());
            news.setLikes(news.getLikes() + (userNews.isLiked() ? 1 : -1));
            news.measurePopularity();
            userNewsRepository.save(userNews);
            newsRepository.save(news);
            singleNewsResponse.setNews(userNews);
            singleNewsResponse.setType(SingleNewsType.LIKE);
            return user;
        });
        return ResponseEntity.ok(singleNewsResponse);
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

        Page<News> news = newsRepository.findByTitleContainsIgnoreCase(query, newsPageable);
        Page<Team> teams = teamRepository.findByNameContainsIgnoreCase(query, teamPageable);

        List<SearchResult> results = new ArrayList<>();

        for (News queryNews : news) {
            SearchResult searchResult = new SearchResult();
            searchResult.setName(queryNews.getTitle());
            searchResult.setType(SearchType.NEWS);
            searchResult.setId(queryNews.getSiteId() + " " + queryNews.getId());
            searchResult.setImgUrl(queryNews.getImageUrl());
            searchResult.setNewsUrl(queryNews.getNewsUrl());
            results.add(searchResult);
        }
        User user = userService.checkUserExistByTokenAndOnSuccess(userRepository, user1 -> user1);
        for (Team team : teams) {
            UserTeam userTeam = userTeamRepository.findByUserAndTeam(user,team).orElse(new UserTeam());
            SearchResult searchResult = new SearchResult();
            searchResult.setId(String.valueOf(team.getId()));
            searchResult.setImgUrl(team.getLogoUrl());
            searchResult.setName(team.getName());
            searchResult.setNewsUrl("");
            searchResult.setType(SearchType.TEAM);
            searchResult.setFavourite(userTeam.isFavourite());
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
//        sportPlParser.getNews(markers, users);
//        footballItaliaParser.getNews(markers, users);
//        transferyInfoParser.getNews(markers, users);
        return "success";
    }

}
