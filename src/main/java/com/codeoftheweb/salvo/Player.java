package com.codeoftheweb.salvo;


import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Player {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String firstName;
    private String lastName;
    private  String userName;

    private String password;

    public  Player() {}

    public  Player(String first, String last, String email, String password){
        firstName = first;
        lastName = last;
        userName = email;
    }

    @OneToMany (mappedBy = "player", fetch = FetchType.EAGER)
    private Set <GamePlayer> gamePlayer = new HashSet<>();

    @OneToMany (mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> score = new HashSet<>();

    @JsonIgnore
    public Set<Score> getScore() {
        return score;
    }

    public Score getScore(Game game){
        return this.score.stream()
                .filter(s -> s.getGame().equals(game))
                .findFirst()
                .orElse(null);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public Set<GamePlayer> getGamePlayer() {
        return gamePlayer;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public  String getLastName(){
        return  lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String toString(){
        return firstName+ " " + lastName;
    }

    public long getId() {
        return id;
    }
/*
    public Map <String, Object> calcPoints (Set<Score> scores){
        for (Score score1 : scores) {

        }
    };
    
  */

    public Map<String, Object> makePlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("email", getUserName());
        dto.put("name", toString());
        return dto;
    }


}
