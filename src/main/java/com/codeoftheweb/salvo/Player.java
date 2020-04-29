package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    @JsonIgnore
    private long id;

    private String firstName;
    private String lastName;
    private String userName;

    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public  Player() {}

    public Player(String first, String last, String email, String password){
        firstName = first;
        lastName = last;
        userName = email;
        this.password = password;
    }
    @JsonIgnore
    @OneToMany (mappedBy = "player", fetch = FetchType.EAGER)
    private Set <GamePlayer> gamePlayer = new HashSet<>();


    @OneToMany (mappedBy = "player", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Score> score = new HashSet<>();

    public Set<Score> getScore() {
        return score;
    }

    public Score getScore(Game game){
        return this.score.stream()
                .filter(s -> s.getGame().equals(game))
                .findFirst()
                .orElse(null);
    }


    @JsonIgnore
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

    @JsonIgnore
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String toString(){
        return firstName+ " " + lastName;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public Map<String, Object> makePlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("email", getUserName());
        dto.put("name", toString());
       dto.put("gpid", this.gamePlayer.stream().map(GamePlayer::getId).collect(Collectors.toSet()));
        return dto;
    }

}
