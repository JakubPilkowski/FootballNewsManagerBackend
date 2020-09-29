package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.parsers.football_italia.Football_Italia_Parser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
public class NewsController {


    private Football_Italia_Parser footballItaliaParser;

    public NewsController(Football_Italia_Parser footballItaliaParser) {
        this.footballItaliaParser = footballItaliaParser;
    }

    @GetMapping("/tmpAddNews")
    public String addNewsFromFootballItalia(){


        footballItaliaParser.getNews();

        return "success";
    }

}
