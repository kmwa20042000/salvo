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
    @JsonIgnore
    private long id;

    @JsonIgnore
    @ManyToOne (fetch = FetchType.EAGER)
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

    @JsonIgnore
    public Map<String, Object> makePGamePlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("player", player.makePlayerDTO());
        dto.put("gpid", getId());
        return dto;
    }

    public  Map<String, Object> makeGameViewDTO(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("player", makePGamePlayerDTO());
        return dto;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setShip(Set<Ship> ship) {
        this.ship = ship;
    }

    public void setSalvo(Set<Salvo> salvo) {
        this.salvo = salvo;
    }

    public void setNewDate(String newDate) {
        this.newDate = newDate;
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

    @JsonIgnore
    public Score getScore() {
        return player.getScore(game);
    };

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public Player getPlayer(){
        return this.player;
    }
    Set<Ship> getShips(){
        return ship;
    }

}
