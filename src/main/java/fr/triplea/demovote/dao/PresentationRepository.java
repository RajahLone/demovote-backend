package fr.triplea.demovote.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.model.Presentation;

public interface PresentationRepository extends JpaRepository<Presentation, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT MAX(p.numero_ordre) FROM vote.presentations AS p WHERE p.numero_categorie = :numero ")
  Integer countByCategorie(@Param("numero") int numeroCategorie);
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.presentations AS p WHERE p.numero_categorie = :numero_cat AND p.numero_production = :numero_prod ")
  Presentation findByCategorieAndProduction(@Param("numero_cat") int numeroCategorie, @Param("numero_prod") int numeroProduction);
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.presentations AS p WHERE p.numero_categorie = :numero ORDER BY p.numero_ordre ASC ")
  List<Presentation> findByCategorie(@Param("numero") int numeroCategorie);

}
