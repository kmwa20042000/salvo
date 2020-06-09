package com.codeoftheweb.salvo;


import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne
    @JoinColumn( name = "gamePlayerId")
    @JsonIgnore
    private GamePlayer gamePlayer;

    private long turnCount;

    @ElementCollection
    private List<String> location;

    public Salvo(){}

    public Map<String,Object> makeSalvoDTO(Salvo salvo){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", getTurnCount());
        dto.put("gamePlayerId", gamePlayer.getId());
        dto.put("location", getLocation());
        return dto;
    };



    public Salvo( GamePlayer gamePlayer, long turnCount, List<String> location) {
        this.gamePlayer = gamePlayer;
        this.turnCount = turnCount;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public long getTurnCount() {
        return turnCount;
    }

    public List<String> getLocation() {
        return location;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }


}
