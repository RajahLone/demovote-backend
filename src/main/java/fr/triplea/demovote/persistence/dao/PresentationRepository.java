package fr.triplea.demovote.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.model.Categorie;
import fr.triplea.demovote.persistence.model.Presentation;

public interface PresentationRepository extends JpaRepository<Presentation, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.presentations AS p WHERE p.numero_categorie = :categorie ")
  List<Presentation> findByCategorie(@Param("categorie") Categorie categorie);

}
