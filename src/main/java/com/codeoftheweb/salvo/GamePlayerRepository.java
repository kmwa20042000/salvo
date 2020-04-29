package com.codeoftheweb.salvo;


import net.minidev.json.annotate.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@RepositoryRestResource
@RequestMapping("/gameplayers")
public interface GamePlayerRepository extends JpaRepository <GamePlayer, Long> {
    GamePlayer findById(long id);
}


