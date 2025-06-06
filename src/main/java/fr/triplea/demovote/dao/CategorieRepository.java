package fr.triplea.demovote.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.model.Categorie;

public interface CategorieRepository extends JpaRepository<Categorie, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT c.* FROM vote.categories AS c WHERE c.numero_categorie = :numero AND c.flag_actif IS TRUE ")
  Categorie findById(@Param("numero") int numeroCategorie);
  
  @NativeQuery("SELECT DISTINCT c.* FROM vote.categories AS c WHERE c.flag_actif IS TRUE AND ((:numero = 0 AND :admin IS TRUE) OR (:numero > 0 AND c.flag_affiche IS TRUE) OR (:admin IS FALSE AND c.flag_affiche IS TRUE)) ORDER BY c.numero_ordre ASC ")
  List<Categorie> findAll(@Param("numero") int numeroParticipant, @Param("admin") boolean admin);
  
  @NativeQuery("SELECT DISTINCT c.* FROM vote.presentations AS p INNER JOIN vote.categories c ON p.numero_categorie = c.numero_categorie WHERE p.numero_production = :numero AND c.flag_actif IS TRUE ")
  Categorie findByProductionPresentee(@Param("numero") int numeroProduction);

  @Override
  void delete(Categorie categorie);

}
