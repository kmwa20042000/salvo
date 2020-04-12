package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RepositoryRestResource
@RequestMapping("/salvoes")
public interface SalvoRepository extends JpaRepository<Salvo, Long> {
    @Autowired
    List<Salvo> getSalvoById (long id);
}
