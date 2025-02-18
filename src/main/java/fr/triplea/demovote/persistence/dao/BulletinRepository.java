package fr.triplea.demovote.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.model.Bulletin;

public interface BulletinRepository extends JpaRepository<Bulletin, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT u.* FROM vote.bulletins AS u WHERE u.numero_categorie = :categorie AND u.numero_participant = :participant ")
  Bulletin findByCategorieAndParticipant(@Param("categorie") int cat_id, @Param("participant") int part_id);
  
  @NativeQuery("SELECT DISTINCT u.* FROM vote.bulletins AS u WHERE u.numero_categorie = :categorie ")
  List<Bulletin> findByCategorie(@Param("categorie") int cat_id);

  @NativeQuery("SELECT DISTINCT u.* FROM vote.bulletins AS u WHERE u.numero_participant = :participant ")
  List<Bulletin> findByParticipant(@Param("participant") int part_id);

}
