package com.codeoftheweb.salvo;


import net.minidev.json.annotate.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    @JsonIgnore
    private GamePlayerRepository gpRepo;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ScoreRepository scoreRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ShipRepository shipRepo;


    @RequestMapping("/games")
    public Map<String, Object> getAllGames(Authentication authentication) {
        Map<String, Object> gamesDTO = new HashMap<>();
        gamesDTO.put("games", gameRepo.findAll()
                .stream()
                .map(gameOne -> gameOne.makeGameDTO(gameOne))
                .collect(Collectors.toList()));
        gamesDTO.put("player", authentication != null ? playerRepository.findByUserName(authentication.getName()).makePlayerDTO() : null);
        return gamesDTO;
    }

    private boolean isGuest(Authentication authentication){
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    @RequestMapping("/score_board")
    public Map<String, Object> getScores(){
        List<GamePlayer> gamePlayers = gpRepo.findAll();
        Map<String,Object> scoreBoard = new HashMap<>();
        for (GamePlayer gamePlayer : gamePlayers){
            Set<Score> scores = gamePlayer.getPlayer().getScore();
            if(scoreBoard.containsKey(gamePlayer.getPlayer().getUserName()) != true){
                Map<String,Object> scoreList = new LinkedHashMap<>();
                scoreList.put("userName", gamePlayer.getPlayer().getUserName());
                scoreList.put("w", scores.stream().filter(score -> score.getScore() == 1.0).count());
                scoreList.put("t", scores.stream().filter(score -> score.getScore() == 0.5).count());
                scoreList.put("l", scores.stream().filter(score -> score.getScore() == 0).count());
                scoreList.put("total", scores.stream().mapToDouble(score -> score.getScore()).sum());
                scoreBoard.put(gamePlayer.getPlayer().getUserName(), scoreList);
            }
        }
        return scoreBoard;
    }

    public Map<String, Object> authenticatedUser(Authentication authentication)  {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", playerRepository.findByUserName(authentication.getName()).getId());
        dto.put("userName", playerRepository.findByUserName(authentication.getName()).getUserName());
        return dto;
    }
    @RequestMapping(value = "/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public ResponseEntity<?> getGameView(@PathVariable long gamePlayerId, Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        GamePlayer gamePlayer = gpRepo.findById(gamePlayerId);
            Player authPlayer = playerRepository.findByUserName(authentication.getName());
           // GamePlayer gamePlayerOne = gpRepo.getGamePlayerById(player.getId());
            if (authPlayer.getId() != gamePlayer.getPlayer().getId()) {
                return new ResponseEntity<>("Error",HttpStatus.FORBIDDEN);
            }
        else {
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getDate());
        dto.put("gamePlayers", gamePlayer.getGame()
                .getGamePlayer().stream()
                .map(gp -> gp.makeGameViewDTO(gp))
                .collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips().stream().map(sp -> sp.makeShipDTO()).collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getSalvo().stream().map(sal -> sal.makeSalvoDTO(sal)).collect(Collectors.toList()));
        return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    };

    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createGames(Authentication authentication){
        Player authPlayer = playerRepository.findByUserName(authentication.getName());
        String date = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now());
        Game newGame = new Game(date);
        GamePlayer newGamePlayer = new GamePlayer(newGame,authPlayer);
        if (isGuest(authentication) == true) {
            return new ResponseEntity<>(makeMap("error", "Please log in"), HttpStatus.UNAUTHORIZED);
        }
        else
        {
            gameRepo.save(newGame);
            gpRepo.save(newGamePlayer);
            System.out.println(newGamePlayer.getId());
            return new ResponseEntity<>(makeMap("gpId", newGamePlayer.getId()),HttpStatus.CREATED);
        }
    }

    private Map<String,Object> makeMap(String key, Object value){
        Map<String, Object> dto = new HashMap<>();
        dto.put(key,value);
        return dto;
    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame (@PathVariable long gameId, Authentication authentication){
        Game game = gameRepo.findById(gameId).orElse(null);
        if(isGuest(authentication) == true){
            return new ResponseEntity<>(makeMap("error","Please Login"),HttpStatus.UNAUTHORIZED);
        }
        if (game == null){
            return new ResponseEntity<>(makeMap("error", "Game doesn't exist"), HttpStatus.FORBIDDEN);
        }
        if(game.getGamePlayer().size() == 2){
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = new GamePlayer(game,player);
        gpRepo.save(gamePlayer);
        return new ResponseEntity<>(makeMap("gpId",gamePlayer.getId()),HttpStatus.CREATED);
    }

    @RequestMapping(path = "/player", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestBody Player player) {
        if (player.getFirstName().isEmpty() || player.getLastName().isEmpty() || player.getUserName().isEmpty() || player.getPassword().isEmpty()) {
            return new ResponseEntity<>("Missing data entry", HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByUserName(player.getUserName()) != null) {
            return new ResponseEntity<>("Email has been used", HttpStatus.FORBIDDEN);
        }
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        playerRepository.save(player);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/games/players/{gpId}/ships", method = RequestMethod.POST)
    public ResponseEntity<String> shipLocations(@PathVariable Long gpId, @RequestBody Ship ship, Authentication authentication) {
        GamePlayer currentPlayer = gpRepo.findById(gpId).orElse(null);
        if ((authenticatedUser(authentication).get("id") == null) || (currentPlayer == null || currentPlayer.getPlayer().getId() != playerRepository.findByUserName(authentication.getName()).getId())) {
            return new ResponseEntity<>("not authorized", HttpStatus.UNAUTHORIZED);
        }
        else{
            Set<Ship> shipSet = currentPlayer.getShips();
            if (shipSet.size() != 0){
                return new ResponseEntity<>("Ship has been placed, try again.", HttpStatus.FORBIDDEN);
            }
            else {
                ship.stream().forEach(ships -> {
                    ships.setGamePlayer(currentPlayer);
                    shipRepo.save(ship);
                });
                return new ResponseEntity<>("Ship has been placed", HttpStatus.CREATED);
            }
        }
    }
    /*
    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.GET)
    public ResponseEntity<List<Ship>>

     */
}
