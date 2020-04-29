package com.codeoftheweb.salvo;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AppController {

    @Autowired
    private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    private ScoreRepository scoreRepository;
    private GamePlayerRepository gpRepo;


    /*
    @RequestMapping ("/games")
    public List<Game> getAll(Authentication authentication) {
        return (List<Game>) playerRepository.findByUserName(authentication.getName());
    }
*/

    @RequestMapping(path = "/web/game", method = RequestMethod.GET)
    public ResponseEntity<String> getGpId(@RequestParam Long gp, Authentication authentication){
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gpRepo.findById(player.getId());
        if (player.getId() != gamePlayer.getPlayer().getId()){
            return new ResponseEntity<>("No Id given", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("Authorized Player", HttpStatus.OK);
    };


    @RequestMapping("/scores")
    public List<Score> getAll() { return  scoreRepository.findAll();}
}




//.stream.order()