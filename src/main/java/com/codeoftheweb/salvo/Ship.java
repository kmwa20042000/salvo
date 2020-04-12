package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name ="native", strategy = "native")
    private long id;
    private String type;
//(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    @ManyToOne
    @JoinColumn (name = "gamePlayerId")
    private GamePlayer gamePlayer;

    @ElementCollection
    private List<String> location;

    public Ship(){

    }

    public Ship(String type, GamePlayer gamePlayer, List<String> location) {
        this.type = type;
        this.gamePlayer = gamePlayer;
        this.location = location;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @JsonIgnore
    public List<String> getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
    public GamePlayer getGamePlayer(){
        return gamePlayer;
    }

    public Map<String,Object> makeShipDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", getType());
        dto.put("location", getLocation());
        return dto;
    };

}
