package com.codeoftheweb.salvo;


import net.minidev.json.annotate.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private SalvoRepository salvoRepository;

    private GamePlayer getOpponent (GamePlayer gpOne) {
        return gpOne.getGame().getGamePlayer().stream().filter(gp -> gp.getId() != gpOne.getId()).findFirst().orElse(null);
    }

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
    public ResponseEntity<Map<String,Object>> getGameView(@PathVariable long gamePlayerId, Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        GamePlayer gamePlayer = gpRepo.findById(gamePlayerId);
            Player authPlayer = playerRepository.findByUserName(authentication.getName());
            if (authPlayer.getId() != gamePlayer.getPlayer().getId()) {
                return new ResponseEntity<>(makeMap("Error","Don't Cheat"),HttpStatus.FORBIDDEN);
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
        dto.put("state", stateDTO(gamePlayer));
        if (getOpponent(gamePlayer) != null){
            dto.put("battlelog", makeGpShipStatusDTO(gamePlayer, getOpponent(gamePlayer)));
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }


    private String stateDTO(GamePlayer gamePlayer){
        String state = "";
        Double viewerPoints = 0.0;
        Double opponentPoints = 0.0;
        if (gamePlayer.getGame().getGamePlayer().size() < 2){
            state = "No Opponent";
        } else {
            state = "Place your ships!";
            if (gamePlayer.getShips().size()!= getOpponent(gamePlayer).getShips().size()) {
                state = "Opponets to place their ships";
            }
            if ((gamePlayer.getSalvo().size() == getOpponent(gamePlayer).getSalvo().size()) && (gamePlayer.getShips().size() != 0 && getOpponent(gamePlayer).getShips().size() != 0 )){
                state = "Your turn to fire!";
            }
            if (gamePlayer.getSalvo().size() != getOpponent(gamePlayer).getSalvo().size()){
                state = "Opponents turn to fire!";
            }
            if (hitCalculator(getOpponent(gamePlayer), true).size() == 17 && gamePlayer.getSalvo().size() == getOpponent(gamePlayer).getSalvo().size()){
                state = "Gameover, you win!";
                viewerPoints = 1.0;
                scoreRepo.save(new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), viewerPoints, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now())));
            }
            if (hitCalculator(gamePlayer, true).size() == 17 && gamePlayer.getSalvo().size() == getOpponent(gamePlayer).getSalvo().size()){
                state = "Gameover, you lost :(";
                opponentPoints = 1.0;
                scoreRepo.save(new Score(gamePlayer.getGame(), getOpponent(gamePlayer).getPlayer(), opponentPoints, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now())));
            }
            if (hitCalculator(gamePlayer, true).size() == 17 && hitCalculator(gamePlayer, true).size() == 17 && gamePlayer.getSalvo().size() == getOpponent(gamePlayer).getSalvo().size()){
                state = "It's a tie";
                viewerPoints = 0.5;
                opponentPoints = 0.5;
                scoreRepo.save(new Score(gamePlayer.getGame(), getOpponent(gamePlayer).getPlayer(), opponentPoints, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now())));
                scoreRepo.save(new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), viewerPoints, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now())));
            }
        }
        return state;
    };

    private Map<String, Object> makeGpShipStatusDTO(GamePlayer viewer, GamePlayer oneGP){
        Map<String, Object> dto = new LinkedHashMap<>();
        GamePlayer gamePlayer = getOpponent(oneGP);
        dto.put("gameplayer", oneGP.getId());
        dto.put("actions", makeHitDTO(viewer, oneGP));
        dto.put("fleet", fleetStatus(viewer, oneGP));
        return dto;
    }

    private Map<String, Object> makeHitDTO(GamePlayer viewer, GamePlayer oneGP){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turns", getTC(viewer));
        dto.put("hits", hitCalculator(oneGP, true));
        dto.put("misses", hitCalculator(oneGP, false));
        dto.put("opponentHits", hitCalculator(viewer, true));
        return dto;
    }

    private List<Long> getTC (GamePlayer oneGP){
            return oneGP.getSalvo().stream().map(Salvo::getTurnCount).collect(Collectors.toList());
    };

    private Set<String> getShipLocations (GamePlayer oneGP){
        return oneGP.getShips().stream().map(Ship::getLocation).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    private Set<String> hitCalculator(GamePlayer oneGP, Boolean successfulHit){
        Set<String> ownShipLocation = getShipLocations(oneGP);
        Set<String> opponentShots = getShots(getOpponent(oneGP));
        Set<String> dto = new LinkedHashSet<>();
        if (opponentShots != null){
            if (successfulHit) {
                dto = opponentShots.stream().filter(ownShipLocation::contains).collect(Collectors.toSet());
            }else {
                dto = opponentShots.stream().filter(oneShot -> !ownShipLocation.contains(oneShot)).collect(Collectors.toSet());
            }
            return dto;
        }
        return dto;
    }

    private Set<Map<String,Object>> fleetStatus(GamePlayer viewer, GamePlayer oneGP){
        Set<Map<String, Object>> dto = oneGP.getShips().stream().sorted(Comparator.comparing(ship -> ship.getLocation().size())).map(ship -> makeShipStatusDTO(viewer, ship)).collect(Collectors.toSet());
        return dto;
    };


    private Boolean isShipSunk (Ship oneShip){
        GamePlayer shipOwner = oneShip.getGamePlayer();
        Set<String> opponentShots = getShots(getOpponent(shipOwner));
        if (opponentShots != null){
            return oneShip.getLocation().stream().allMatch(opponentShots::contains);
        } else return false;
    }

    private Map<String, Object> makeShipStatusDTO (GamePlayer viewer, Ship oneShip){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("shipType", oneShip.getType());
        dto.put("isSunked", isShipSunk(oneShip));
        return dto;
    };
    private Set<String> getShots (GamePlayer oneGP){
        if (oneGP != null){
            return oneGP.getSalvo().stream().map(Salvo::getLocation).flatMap(Collection::stream).collect(Collectors.toSet());
        }
        else return null;
    }

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
    public ResponseEntity<String> shipLocations(@PathVariable Long gpId, @RequestBody Set<Ship> ships, Authentication authentication) {
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
                ships.stream().forEach(ship -> {
                    ship.setGamePlayer(currentPlayer);
                    shipRepo.save(ship);
                });
                return new ResponseEntity<>("Ship has been placed", HttpStatus.CREATED);
            }
        }
    }
    @RequestMapping(value = "/games/players/{gpId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<String> placeSalvoes(@PathVariable Long gpId, @RequestBody Salvo newSalvo, Authentication authentication) throws Exception{
        GamePlayer currentGamePlayer = gpRepo.findById(gpId).orElse(null);
        if ((authenticatedUser(authentication).get("id") == null) ||
                (currentGamePlayer == null) || currentGamePlayer.getPlayer().getId() != playerRepository.findByUserName(authentication.getName()).getId()){
            return new ResponseEntity<>("user not logged in", HttpStatus.FORBIDDEN);
        }else{
            if(currentGamePlayer.getGame().getGamePlayer().size() < 2){
                return new ResponseEntity<>("game will start when opponent joins", HttpStatus.FORBIDDEN);
            }
            if(newSalvo.getLocation().size() != 5) {
                return new ResponseEntity<>("5 salvo location needed!", HttpStatus.FORBIDDEN);
            }else if(currentGamePlayer.getSalvo().size() > getOpponent(currentGamePlayer).getSalvo().size()){
                return new ResponseEntity<>("its opponents turn", HttpStatus.FORBIDDEN);
            }
            else {
                newSalvo.setTurnCount(currentGamePlayer.getSalvo().size()+1);
                newSalvo.setGamePlayer(currentGamePlayer);
                salvoRepository.save(newSalvo);
                return new ResponseEntity<>("Salvo has been sent", HttpStatus.CREATED);
            }
        }
    }

}
