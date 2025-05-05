package fr.triplea.demovote.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.dto.BulletinShort;
import fr.triplea.demovote.model.Bulletin;

public interface BulletinRepository extends JpaRepository<Bulletin, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT "
             + " u.numero_bulletin, "
             + " u.numero_categorie, "
             + " u.numero_participant, "
             + " CASE WHEN u.numero_production01 IS NULL THEN 0 ELSE u.numero_production01 END AS numero_production01, "
             + " CASE WHEN u.numero_production02 IS NULL THEN 0 ELSE u.numero_production02 END AS numero_production02, "
             + " CASE WHEN u.numero_production03 IS NULL THEN 0 ELSE u.numero_production03 END AS numero_production03, "
             + " CASE WHEN u.numero_production04 IS NULL THEN 0 ELSE u.numero_production04 END AS numero_production04, "
             + " CASE WHEN u.numero_production05 IS NULL THEN 0 ELSE u.numero_production05 END AS numero_production05, "
             + " CASE WHEN u.numero_production06 IS NULL THEN 0 ELSE u.numero_production06 END AS numero_production06, "
             + " CASE WHEN u.numero_production07 IS NULL THEN 0 ELSE u.numero_production07 END AS numero_production07, "
             + " CASE WHEN u.numero_production08 IS NULL THEN 0 ELSE u.numero_production08 END AS numero_production08, "
             + " CASE WHEN u.numero_production09 IS NULL THEN 0 ELSE u.numero_production09 END AS numero_production09, "
             + " CASE WHEN u.numero_production10 IS NULL THEN 0 ELSE u.numero_production10 END AS numero_production10, "
             + " u.flag_valide "
             + "FROM vote.bulletins AS u "
             + "WHERE u.numero_categorie = :categorie AND u.numero_participant = :participant ")
  BulletinShort findByCategorieAndParticipant(@Param("categorie") int cat_id, @Param("participant") int part_id);

  @NativeQuery("SELECT DISTINCT "
             + " u.* "
             + "FROM vote.bulletins AS u "
             + "WHERE u.numero_categorie = :categorie AND u.numero_participant = :participant ")
  Bulletin getByCategorieAndParticipant(@Param("categorie") int cat_id, @Param("participant") int part_id);

  @NativeQuery("SELECT DISTINCT u.* FROM vote.bulletins AS u WHERE u.numero_categorie = :categorie ")
  List<Bulletin> findByCategorie(@Param("categorie") int cat_id);

  @NativeQuery("SELECT DISTINCT u.* FROM vote.bulletins AS u WHERE u.numero_participant = :participant ")
  List<Bulletin> findByParticipant(@Param("participant") int part_id);
  
  @NativeQuery("SELECT DISTINCT COUNT(u.*) AS nombre "
             + "FROM vote.bulletins AS u "
             + "WHERE (p.numero_production01 = :numero) "
             + "   OR (p.numero_production02 = :numero) "
             + "   OR (p.numero_production03 = :numero) "
             + "   OR (p.numero_production04 = :numero) "
             + "   OR (p.numero_production05 = :numero) "
             + "   OR (p.numero_production06 = :numero) "
             + "   OR (p.numero_production07 = :numero) "
             + "   OR (p.numero_production08 = :numero) "
             + "   OR (p.numero_production09 = :numero) "
             + "   OR (p.numero_production10 = :numero) ")
  Integer countIfProductionVoted(@Param("numero") int numeroProduction);

}
