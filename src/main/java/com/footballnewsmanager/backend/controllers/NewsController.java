package com.footballnewsmanager.backend.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.news.*;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import com.footballnewsmanager.backend.parsers.transfery_info.TransferyInfoParser;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.services.NewsService;
import com.footballnewsmanager.backend.services.PaginationService;
import com.footballnewsmanager.backend.services.TeamsService;
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
    private final NewsService newsService;
    private final UserNewsLikeRepository userNewsLikeRepository;
    private final UserNewsDislikesRepository userNewsDislikesRepository;
    private final MarkerRepository markerRepository;
    private final UserRepository userRepository;
    private final UserNewsVisitedRepository userNewsVisitedRepository;
    private final UserNewsSendedRepository userNewsSendedRepository;
    private final TeamsService teamsService;

    public NewsController(Football_Italia_Parser footballItaliaParser, TransferyInfoParser transferyInfoParser, NewsRepository newsRepository, TeamRepository teamRepository, UserService userService, NewsService newsService, UserNewsLikeRepository userNewsLikeRepository, UserNewsDislikesRepository userNewsDislikesRepository, TeamNewsRepository teamNewsRepository, MarkerRepository markerRepository, UserRepository userRepository, UserNewsVisitedRepository userNewsVisitedRepository, UserNewsSendedRepository userNewsSendedRepository, FavouriteTeamRepository favouriteTeamRepository, TeamsService teamsService) {
        this.footballItaliaParser = footballItaliaParser;
        this.transferyInfoParser = transferyInfoParser;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.newsService = newsService;
        this.userNewsLikeRepository = userNewsLikeRepository;
        this.userNewsDislikesRepository = userNewsDislikesRepository;
        this.markerRepository = markerRepository;
        this.userRepository = userRepository;
        this.userNewsVisitedRepository = userNewsVisitedRepository;
        this.userNewsSendedRepository = userNewsSendedRepository;
        this.teamsService = teamsService;
    }

    @GetMapping(value = "", params = {"page"})
    @JsonView(Views.Internal.class)
    public ResponseEntity<NewsResponse> getNewsForTeams(@RequestParam("page") int page) {
        NewsResponse newsResponse = new NewsResponse();

        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            List<Team> teams = new ArrayList<>(teamsService.getFavouriteTeams(user, teamRepository));

            Pageable newsPageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "date", "highlighted", "popularity"));
            Page<News> news = newsRepository.findDistinctByTeamNewsTeamIn(teams, newsPageable)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma newsów dla podanych drużyn"));
            PaginationService.handlePaginationErrors(page, news);
            List<UserNews> userNews = new ArrayList<>(newsService.createUserNewsTable(
                    user, news, userNewsLikeRepository, userNewsVisitedRepository, userNewsDislikesRepository,
                    newsRepository
            ));
            userNews.sort((o1, o2) -> Boolean.compare(o1.isBadgeVisited(), o2.isBadgeVisited()));
            newsResponse.setNews(userNews);
            return user;
        });

        return ResponseEntity.ok(newsResponse);
    }


    @GetMapping(value = "all", params = {"page"})
    @JsonView(Views.Internal.class)
    public ResponseEntity<NewsForTeamsResponse<BaseNewsAdjustment>> getNews(@RequestParam("page") @Min(value = 0) int page) {

        NewsForTeamsResponse<BaseNewsAdjustment> newsForTeamsResponse = new NewsForTeamsResponse<>();

        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            List<Team> teams = new ArrayList<>(teamsService.getFavouriteTeams(user, teamRepository));

            Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "date", "highlighted", "popularity"));
            Page<News> news = newsRepository.findAll(pageable);
            PaginationService.handlePaginationErrors(page, news);

            List<UserNews> userNews = new ArrayList<>(newsService.createUserNewsTable(
                    user, news, userNewsLikeRepository, userNewsVisitedRepository, userNewsDislikesRepository,
                    newsRepository
            ));

            switch ((page + 3) % 3) {
                case 0:
                    Pageable hotNewsPagable = PageRequest.of(page, 1, Sort.by(Sort.Direction.DESC, "date", "highlighted", "popularity"));
                    Page<News> hotNewsList = newsRepository.findAll(hotNewsPagable);
                    System.out.println(hotNewsList.getContent().get(0).getTitle());
                    News hotNews = hotNewsList.getContent().get(0);
                    NewsExtras newsExtras = new NewsExtras("Ostatnio popularne", NewsInfoType.HOT_NEWS,
                            hotNews);
                    newsForTeamsResponse.setAdditionalContent(newsExtras);
                    break;
                case 1:
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
                    int index = random.nextInt(teamsForSelectedNews.size());

                    Team randomizedTeam = teamsForSelectedNews.get(index);
                    Page<News> proposedNews = newsRepository.findByTeamNewsTeam(randomizedTeam, proposedNewsPageable).orElseThrow(() -> new ResourceNotFoundException(""));
                    NewsExtras proposedNewsExtras = new NewsExtras("To może ci się spodobać", NewsInfoType.PROPOSED_NEWS,
                            proposedNews.getContent().get(0));

                    newsForTeamsResponse.setAdditionalContent(proposedNewsExtras);
                    break;
                case 2:
                    Pageable teamsPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "popularity"));
                    Page<Team> hotTeams = teamRepository.findAll(teamsPageable);
                    TeamExtras teamExtras = new TeamExtras("Polecane drużyny", NewsInfoType.PROPOSED_TEAMS,
                            hotTeams.getContent());
                    newsForTeamsResponse.setAdditionalContent(teamExtras);
                    break;
            }
            newsForTeamsResponse.setPages(news.getTotalPages());
            newsForTeamsResponse.setNewsCount(news.getTotalElements());
            Long count = newsRepository.countDistinctByTeamNewsTeamInAndDate(teams, LocalDate.now());
            newsForTeamsResponse.setNewsToday(count);
            newsForTeamsResponse.setNews(userNews);

            return user;
        });

        return ResponseEntity.ok().body(newsForTeamsResponse);
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
            PaginationService.handlePaginationErrors(page, news);

            List<UserNews> userNews = new ArrayList<>(newsService.createUserNewsTable(
                    user, news, userNewsLikeRepository, userNewsVisitedRepository, userNewsDislikesRepository,
                    newsRepository
            ));
            newsResponse.setNews(userNews);
            return user;
        });
        return ResponseEntity.ok().body(newsResponse);
    }

    @GetMapping("notifications")
    @JsonView(Views.Internal.class)
    public ResponseEntity<NotificationResponse> getNotifications() {
        List<Notification> notifications = new ArrayList<>();
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            List<Team> teams = new ArrayList<>(teamsService.getFavouriteTeams(user, teamRepository));
            Long count = newsRepository.countDistinctByTeamNewsTeamInAndDateAfter(teams, user.getAddedDate().minusDays(1));
            Pageable pageable = PageRequest.of(0, count.intValue(), Sort.by(Sort.Direction.DESC, "date", "highlighted", "popularity"));
            Page<News> news = newsRepository.findDistinctByTeamNewsTeamIn(teams, pageable)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma newsów dla podanych drużyn"));
            PaginationService.handlePaginationErrors(0, news);
            Long amountBefore = 0L;
            Long amountAfter = 0L;
            for (News singleNews : news.getContent()) {
                if (!userNewsVisitedRepository.existsByUserAndNews(user, singleNews)) {
                    if (!userNewsSendedRepository.existsByUserAndNews(user, singleNews)) {
                        UserNewsSended userNewsSended = new UserNewsSended();
                        userNewsSended.setUser(user);
                        userNewsSended.setNews(singleNews);
                        userNewsSendedRepository.save(userNewsSended);
                    } else {
                        amountBefore++;
                    }
                    amountAfter++;
                }
            }
            Notification notification = new Notification();
            notification.setAmountBefore(amountBefore);
            notification.setAmountAfter(amountAfter);
            notifications.add(notification);
            return user;
        });
        return ResponseEntity.ok(new NotificationResponse(notifications));
    }

    @GetMapping("markAllAsVisited")
    public ResponseEntity<BaseResponse> markAllAsVisited() {
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            List<Team> teams = new ArrayList<>(teamsService.getFavouriteTeams(user, teamRepository));
            Long count = newsRepository.countDistinctByTeamNewsTeamInAndDateAfter(teams, user.getAddedDate().minusDays(1));
            Pageable pageable = PageRequest.of(0, count.intValue(), Sort.by(Sort.Direction.DESC, "date", "highlighted", "popularity"));
            Page<News> news = newsRepository.findDistinctByTeamNewsTeamIn(teams, pageable)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma newsów dla podanych drużyn"));
            PaginationService.handlePaginationErrors(0, news);
            for (News singleNews : news.getContent()) {
                if (!userNewsVisitedRepository.existsByUserAndNews(user, singleNews)) {
                    UserNewsVisited userNewsVisited = new UserNewsVisited();
                    userNewsVisited.setNews(singleNews);
                    userNewsVisited.setUser(user);
                    userNewsVisitedRepository.save(userNewsVisited);
                }
            }
            return user;
        });

        return ResponseEntity.ok(new BaseResponse(true, "Zaznaczono wszystkie jako przeczytane"));
    }

    @GetMapping("notVisitedNewsAmount")
    public ResponseEntity<BadgesResponse> getNotVisitedNewsAmount() {
        AtomicReference<BadgesResponse> badgesResponse = new AtomicReference<>();
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            List<Team> teams = new ArrayList<>(teamsService.getFavouriteTeams(user, teamRepository));
            Long count = newsRepository.countDistinctByTeamNewsTeamInAndDateAfter(teams, user.getAddedDate().minusDays(1));
            Pageable pageable = PageRequest.of(0, count.intValue(), Sort.by(Sort.Direction.DESC, "date", "highlighted", "popularity"));
            Page<News> news = newsRepository.findDistinctByTeamNewsTeamIn(teams, pageable)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie ma newsów dla podanych drużyn"));
            PaginationService.handlePaginationErrors(0, news);
            Long amount = 0L;
            for (News singleNews : news.getContent()) {
                if (!userNewsVisitedRepository.existsByUserAndNews(user, singleNews)) {
                    amount++;
                }
            }
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
            if (!userNewsVisitedRepository.existsByUserAndNews(user, news)) {
                news.setClicks(news.getClicks() + 1);
                newsRepository.save(news);
                UserNewsVisited userNewsVisited = new UserNewsVisited();
                userNewsVisited.setUser(user);
                userNewsVisited.setNews(news);
                userNewsVisitedRepository.save(userNewsVisited);
            }
            UserNews userNews = newsService.createUserNews(user, news, userNewsLikeRepository,
                    userNewsVisitedRepository, userNewsDislikesRepository, newsRepository);
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
            String message;
            if (!userNewsLikeRepository.existsByUserAndNews(user, news)) {
                UserNewsLike userNewsLike = new UserNewsLike();
                news.setLikes(news.getLikes() + 1);
                message = "Polajkowano Wiadomość";
                if (userNewsDislikesRepository.existsByUserAndNews(user, news)) {
                    userNewsDislikesRepository.deleteByUserAndNews(user, news);
                    news.setDislikes(news.getDislikes() - 1);
                }
                news.measurePopularity();
                newsRepository.save(news);
                userNewsLike.setNews(news);
                userNewsLike.setUser(user);
                userNewsLikeRepository.save(userNewsLike);
            } else {
                userNewsLikeRepository.deleteByUserAndNews(user, news);
                news.setLikes(news.getLikes() - 1);
                news.measurePopularity();
                newsRepository.save(news);
                message = "Odlajkowano wiadomość";
            }
            UserNews userNews = newsService.createUserNews(
                    user, news, userNewsLikeRepository, userNewsVisitedRepository, userNewsDislikesRepository,
                    newsRepository
            );
            singleNewsResponse.set(new SingleNewsResponse(true, message, userNews));
            return user;
        });
        return ResponseEntity.ok(singleNewsResponse.get());
    }

    @PutMapping("dislike/site={sid}/id={id}")
    @Transactional
    public ResponseEntity<SingleNewsResponse> dislikeNews(@PathVariable("sid") @Min(value = 0) Long sid,
                                                          @PathVariable("id") @Min(value = 0) Long id) {
        AtomicReference<SingleNewsResponse> singleNewsResponse = new AtomicReference<>();
        News news = newsRepository.findBySiteIdAndId(sid, id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej wiadomości"));
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            String message;
            if (!userNewsDislikesRepository.existsByUserAndNews(user, news)) {
                UserNewsDislike userNewsDislike = new UserNewsDislike();
                news.setDislikes(news.getDislikes() + 1);
                news.measurePopularity();
                message = "Dodano dislajka";
                if (userNewsLikeRepository.existsByUserAndNews(user, news)) {
                    userNewsLikeRepository.deleteByUserAndNews(user, news);
                    news.setLikes(news.getLikes() - 1);
                }
                news.measurePopularity();
                newsRepository.save(news);
                userNewsDislike.setNews(news);
                userNewsDislike.setUser(user);
                userNewsDislikesRepository.save(userNewsDislike);
            } else {
                userNewsDislikesRepository.deleteByUserAndNews(user, news);
                news.setDislikes(news.getDislikes() - 1);
                news.measurePopularity();
                newsRepository.save(news);
                message = "Usunięto dislajka";
            }
            UserNews userNews = newsService.createUserNews(
                    user, news, userNewsLikeRepository, userNewsVisitedRepository, userNewsDislikesRepository,
                    newsRepository
            );
            singleNewsResponse.set(new SingleNewsResponse(true, message, userNews));
            return user;
        });
        return ResponseEntity.ok(singleNewsResponse.get());
    }

    @GetMapping(value = "hot", params = {"count"})
    @JsonView(Views.Public.class)
    public ResponseEntity<NewsResponse> hotNews(@RequestParam(value = "count", defaultValue = "5") @NotNull @Range(min = 5, max = 10) int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "date", "popularity"));
        Page<News> news = newsRepository.findAll(pageable);
        PaginationService.handlePaginationErrors(0, news);
        NewsResponse newsResponse = new NewsResponse();
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            List<UserNews> userNews = new ArrayList<>(newsService.createUserNewsTable(
                    user, news, userNewsLikeRepository, userNewsVisitedRepository, userNewsDislikesRepository,
                    newsRepository
            ));
            newsResponse.setNews(userNews);
            return user;
        });

        return ResponseEntity.ok(newsResponse);
    }


    @GetMapping(value = "query={query}", params = {"page"})
    @JsonView(Views.Public.class)
    public ResponseEntity<NewsResponse> getNewsByQuery(@RequestParam(value = "page", defaultValue = "0") @Min(value = 0) int page, @PathVariable("query") @NotNull() String query) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "highlighted", "popularity"));
        Page<News> news = newsRepository.findByTitleContainsIgnoreCase(query, pageable).orElseThrow(() -> new ResourceNotFoundException("Dla podanego hasła nie ma żadnej drużyny"));
        PaginationService.handlePaginationErrors(page, news);

        NewsResponse newsResponse = new NewsResponse();

        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            List<UserNews> userNews = new ArrayList<>(newsService.createUserNewsTable(
                    user, news, userNewsLikeRepository, userNewsVisitedRepository, userNewsDislikesRepository,
                    newsRepository
            ));
            newsResponse.setNews(userNews);
            return user;
        });

        return ResponseEntity.ok(newsResponse);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/tmpAddNews")
    @Transactional()
    public String addNewsFromFootballItalia() {
        List<Marker> markers = markerRepository.findAll();
        footballItaliaParser.getNews(markers);
        transferyInfoParser.getNews(markers);
        return "success";
    }

}
