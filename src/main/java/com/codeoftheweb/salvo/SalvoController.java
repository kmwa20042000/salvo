package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GamePlayerRepository gpRepo;

    @Autowired ScoreRepository scoreRepo;

    @RequestMapping("/games")
    public List<Object> getAllGames() {
        return gameRepo.findAll()
                .stream()
                .map(game -> game.makeGameDTO(game))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public Map<String, Object> getGameView(@PathVariable long gamePlayerId){
        Map<String, Object> dto = new LinkedHashMap<>();

        GamePlayer gamePlayer = gpRepo.getGamePlayerById(gamePlayerId);
        dto.put("id",gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getDate());
        dto.put("gamePlayers", gamePlayer.getGame()
                .getGamePlayer().stream()
                .map(gp -> gp.makeGameViewDTO(gp))
                .collect(Collectors.toList()));
        dto.put("ships",  gamePlayer.getShips().stream().map(sp -> sp.makeShipDTO()).collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getSalvo().stream().map(sal -> sal.makeSalvoDTO(sal)).collect(Collectors.toList()));

        return dto;
    };

}

