package fr.triplea.demovote.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.model.Categorie;

public interface CategorieRepository extends JpaRepository<Categorie, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT c.* FROM vote.categories AS c WHERE c.numero_categorie = :id AND c.flag_actif IS TRUE ")
  Categorie findById(@Param("id") int id);
  
  @NativeQuery("SELECT DISTINCT c.* FROM vote.categories AS c WHERE c.flag_actif IS TRUE ORDER BY c.numero_ordre ASC ")
  List<Categorie> findAll();

  @Override
  void delete(Categorie categorie);

}
