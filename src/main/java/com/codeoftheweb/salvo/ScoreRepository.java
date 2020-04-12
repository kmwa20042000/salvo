package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Arrays;
import java.util.Optional;

@RepositoryRestResource
public interface ScoreRepository extends JpaRepository<Score, Long> {

}
