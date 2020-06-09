package com.codeoftheweb.salvo;


import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne (fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn (name = "gameId")
    private Game game;

    @ManyToOne
    @JsonIgnore
    @JoinColumn (name = "playerId")
    private Player player;

    private double score;

    private String finishDate;

    public Score(){};

    public Score(Game game, Player player, Double score, String finishDate) {
        this.game = game;
        this.player = player;
        this.score = score;
        this.finishDate = finishDate;
    }

    public long getId() {
        return id;
    }
    @JsonIgnore
    public Game getGame() {
        return game;
    }

    @JsonIgnore
    public Player getPlayer() {
        return player;
    }

    @JsonIgnore
    public double getScore() {
        return score;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setFinishData(String finishData) {
        this.finishDate = finishData;
    }


    @JsonIgnore
    public Map<String, Object> makeScoreDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("score", getScore());
        dto.put("player", this.getPlayer().getUserName());
        return dto;
    };
}
