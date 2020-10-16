package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.NewsResponse;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.UserNewsDislike;
import com.footballnewsmanager.backend.models.UserNewsLike;
import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import com.footballnewsmanager.backend.parsers.transfery_info.TransferyInfoParser;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.services.PaginationService;
import com.footballnewsmanager.backend.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@RestController
@RequestMapping("/news")
public class NewsController {


    private final Football_Italia_Parser footballItaliaParser;
    private final TransferyInfoParser transferyInfoParser;
    private final NewsRepository newsRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final UserNewsLikeRepository userNewsLikeRepository;
    private final UserNewsDislikesRepository userNewsDislikesRepository;
    private final TeamNewsRepository teamNewsRepository;
    private final UserRepository userRepository;

    public NewsController(Football_Italia_Parser footballItaliaParser, TransferyInfoParser transferyInfoParser, NewsRepository newsRepository, TeamRepository teamRepository, UserService userService, UserNewsLikeRepository userNewsLikeRepository, UserNewsDislikesRepository userNewsDislikesRepository, TeamNewsRepository teamNewsRepository, UserRepository userRepository) {
        this.footballItaliaParser = footballItaliaParser;
        this.transferyInfoParser = transferyInfoParser;
        this.newsRepository = newsRepository;
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.userNewsLikeRepository = userNewsLikeRepository;
        this.userNewsDislikesRepository = userNewsDislikesRepository;
        this.teamNewsRepository = teamNewsRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "",params = {"page"})
    public ResponseEntity<NewsResponse> getNews(@RequestParam("page") @Min(value = 0) int page){
        Pageable pageable = PageRequest.of(page, 20);
        Page<News> news = newsRepository.findAll(pageable);
        PaginationService.handlePaginationErrors(page, news);
        return ResponseEntity.ok().body(new NewsResponse(true, "newsy", news.getContent()));
    }

    @GetMapping(value = "/team={id}", params = {"page"})
    public ResponseEntity<NewsResponse> getNewsForTeam(@PathVariable("id") @Min(value = 0) Long id, @RequestParam("page") @Min(value = 0) int page)
    {
        Team team = teamRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Nie ma takiej drużyny"));
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "highlighted", "popularity"));
        Page<News> news = newsRepository.findByTeamNewsTeam(team, pageable).orElseThrow(()-> new ResourceNotFoundException("Nie ma takiej newsów dla tej drużyny"));
        PaginationService.handlePaginationErrors(page, news);
        return ResponseEntity.ok(new NewsResponse(true, "Wiadomości dla "+team.getName(), news.getContent()));
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("highlight/site={sid}/league={lid}")
    public ResponseEntity<BaseResponse> highlightNews(@PathVariable("sid") @Min(value = 0) Long sid,
                                                      @PathVariable("lid") @Min(value = 0) Long lid)
    {
        News news = newsRepository.findBySiteIdAndId(sid,lid).orElseThrow(()-> new ResourceNotFoundException("Nie ma takiej wiadomości"));
        news.setHighlighted(!news.isHighlighted());
        newsRepository.save(news);
        return ResponseEntity.ok(new BaseResponse(true, "Wyróżniono newsa"));
    }

    @PutMapping("like/site={sid}/league={lid}")
    public ResponseEntity<BaseResponse> likeNews(@PathVariable("sid") @Min(value = 0) Long sid,
                                                 @PathVariable("lid") @Min(value = 0) Long lid){
        News news = newsRepository.findBySiteIdAndId(sid,lid).orElseThrow(()-> new ResourceNotFoundException("Nie ma takiej wiadomości"));
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            UserNewsLike userNewsLike = new UserNewsLike();
            news.setLikes(news.getLikes()+1);
            newsRepository.save(news);
            userNewsLike.setNews(news);
            userNewsLike.setUser(user);
            userNewsLikeRepository.save(userNewsLike);
            return user;
        });
        return ResponseEntity.ok(new BaseResponse(true, "Polubiono wiadomość"));
    }

    @PutMapping("dislike/site={sid}/league={lid}")
    public ResponseEntity<BaseResponse> dislikeNews(@PathVariable("sid") @Min(value = 0) Long sid,
                                                    @PathVariable("lid") @Min(value = 0) Long lid){

        News news = newsRepository.findBySiteIdAndId(sid,lid).orElseThrow(()-> new ResourceNotFoundException("Nie ma takiej wiadomości"));
        userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            UserNewsDislike userNewsDislike = new UserNewsDislike();
            news.setLikes(news.getLikes()-1);
            newsRepository.save(news);
            userNewsDislike.setNews(news);
            userNewsDislike.setUser(user);
            userNewsDislikesRepository.save(userNewsDislike);
            return user;
        });
        return ResponseEntity.ok(new BaseResponse(true, "Dodano złą opinię do wiadomości"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/tmpAddNews")
    public String addNewsFromFootballItalia(){
        footballItaliaParser.getNews();
        transferyInfoParser.getNews();
        return "success";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/deleteLastNews")
    @Transactional
    public String deleteLastNews(){
//        System.out.println(newsRepository.findAll().size());
        LocalDate localDate = LocalDate.parse("2020-10-01");
        newsRepository.deleteByDateLessThan(localDate);
//        System.out.println(newsRepository.findAll().size());
        return "success";
    }
}
