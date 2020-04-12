package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AppController {

    @Autowired
    private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    private ScoreRepository scoreRepository;

    @RequestMapping ("/games")
    public List<Game> getAll(Authentication authentication) {
        return (List<Game>) playerRepository.findByUserName(authentication.getName());
    }{

    }

    @RequestMapping("/scores")
    public List<Score> getAll() { return  scoreRepository.findAll();}


}
