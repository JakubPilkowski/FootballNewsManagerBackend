package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.response.NewsResponse;
import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import com.footballnewsmanager.backend.parsers.transfery_info.TransferyInfoParser;
import com.footballnewsmanager.backend.repositories.NewsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {


    private final Football_Italia_Parser footballItaliaParser;
    private final TransferyInfoParser transferyInfoParser;
    private final NewsRepository  newsRepository;

    public NewsController(Football_Italia_Parser footballItaliaParser, TransferyInfoParser transferyInfoParser, NewsRepository newsRepository) {
        this.footballItaliaParser = footballItaliaParser;
        this.transferyInfoParser = transferyInfoParser;
        this.newsRepository = newsRepository;
    }

    @GetMapping("")
    public ResponseEntity<NewsResponse> getNews(){
        NewsResponse newsResponse = new NewsResponse(true, "newsy", newsRepository.findAll());
        return ResponseEntity.ok().body(newsResponse);
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
        System.out.println(newsRepository.findAll().size());
        LocalDate localDate = LocalDate.parse("2020-10-01");
        newsRepository.deleteByDateLessThan(localDate);
        System.out.println(newsRepository.findAll().size());
        return "success";
    }
}
