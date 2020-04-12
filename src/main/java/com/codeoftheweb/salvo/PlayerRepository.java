package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@RepositoryRestResource
public interface  PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUserName(@Param("name") String userName);
   // Player loadByUserName(String userName);

}
