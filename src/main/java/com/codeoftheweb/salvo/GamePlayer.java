package com.codeoftheweb.salvo;


import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne (fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "playerId")
    private Player player;

    @ManyToOne (fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "gameId")
    private Game game;

    @OneToMany (mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set <Ship> ship = new HashSet<>();

    @OneToMany (mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set <Salvo> salvo = new HashSet<>();

    private String newDate;

    public GamePlayer(){}

    public GamePlayer(Game game, Player player){
        this.game = game;
        this.player = player;
    }

    public Map<String, Object> makePGamePlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("player", player.makePlayerDTO());
        return dto;
    }

    public  Map<String, Object> makeGameViewDTO(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("player", makePGamePlayerDTO());
        return dto;
    }
    public Set <Ship> getShip() {return ship;};
    public Set<Salvo> getSalvo() {return  salvo;}
    public String getNewDate() {
        return newDate;
    }
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    public Score getScore() {
        return player.getScore(game);
    };

    public long getId() {
        return id;
    }
    public Player getPlayer(){
        return this.player;
    }
    Set<Ship> getShips(){
        return ship;
    }




}
