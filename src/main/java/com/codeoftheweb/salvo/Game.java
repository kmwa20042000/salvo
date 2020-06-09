package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String newDate;

    public Game(Date date) {
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set <GamePlayer> gamePlayer = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> score = new HashSet<>();

    public Game() {}

    public Game(String date) {
        this.newDate = date;
    }

    public String getDate() {
        return newDate;
    }

    public void setDate(String date) {
        this.newDate = date;
    }

    private Integer currentTurn;

    public String getNewDate() {
        return newDate;
    }

    @JsonIgnore
    public Set<GamePlayer> getPlayer(){
        return this.gamePlayer;
    }

    @JsonIgnore
    public Set<Score> getScore() {
        return score;
    }

    @JsonIgnore
    public void setPlayer(Set<GamePlayer> gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Integer getCurrentTurn() {
        return currentTurn;
    }

    @JsonIgnore
    public Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gameId", game.getId());
        dto.put("date", this.getDate());
        dto.put("gamePlayer", game.getGamePlayer()
                .stream()
                .map(gp -> gp.makePGamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("score", game.getGamePlayer()
                .stream()
                .map(g -> g.getPlayer().getScore(game) != null ? g.getPlayer().getScore(game).makeScoreDTO() : null));
        return dto;
    };

    public Map<String,Object> gameViewDTO(Game game){
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("gameId", getId());
        dto.put("date",this.getDate());
        return dto;
    };

    @JsonIgnore
    public Set<GamePlayer> getGamePlayer() {
        return gamePlayer;
    }
}




