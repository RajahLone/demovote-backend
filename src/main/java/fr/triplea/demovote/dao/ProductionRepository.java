package fr.triplea.demovote.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.dto.ProductionFile;
import fr.triplea.demovote.dto.ProductionShort;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Production;


public interface ProductionRepository extends JpaRepository<Production, Integer> 
{

  @NativeQuery("SELECT DISTINCT p.* FROM vote.productions AS p WHERE p.numero_production = :id AND p.flag_actif IS TRUE ")
  Production findById(@Param("id") int id);
  
  @NativeQuery("SELECT DISTINCT " 
              + "TO_CHAR(p.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
              + "TO_CHAR(p.date_modification, 'DD/MM/YYYY HH24:MI:SS') as date_modification, "
              + "p.numero_production, "
              + "CAST(p.adresse_ip AS VARCHAR) AS adresse_ip, "
              + "p.type, "
              + "p.titre, "
              + "p.auteurs, "
              + "p.groupes, "
              + "p.plateforme, "
              + "p.commentaire, "
              + "p.informations_privees, "
              + "p.numero_participant AS numero_gestionnaire, "
              + "CONCAT(g.pseudonyme, ' = ', g.nom, ' ', g.prenom) AS nom_gestionnaire, "
              + "p.nom_archive, "
              + "p.vignette, "
              + "p.numero_version "
              + "FROM vote.productions AS p "
              + "INNER JOIN vote.participants AS g ON p.numero_participant = g.numero_participant "
              + "WHERE p.flag_actif IS TRUE "
              + "AND ((:numero = 0) OR (:numero = p.numero_participant)) "
              + "AND ((:type IS NULL) OR (p.type = (:type)::vote.type_production)) "
              + "ORDER BY p.titre ASC ")
  List<ProductionShort> findAllWithoutArchive(@Param("numero") int numeroGestionnaire, @Param("type") String type);
  
  @NativeQuery("SELECT DISTINCT " 
              + "TO_CHAR(p.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
              + "TO_CHAR(p.date_modification, 'DD/MM/YYYY HH24:MI:SS') as date_modification, "
              + "p.numero_production, "
              + "CAST(p.adresse_ip AS VARCHAR) AS adresse_ip, "
              + "p.type, "
              + "p.titre, "
              + "p.auteurs, "
              + "p.groupes, "
              + "p.plateforme, "
              + "p.commentaire, "
              + "p.informations_privees, "
              + "p.numero_participant AS numero_gestionnaire, "
              + "CONCAT(g.pseudonyme, ' = ', g.nom, ' ', g.prenom) AS nom_gestionnaire, "
              + "p.nom_archive, "
              + "p.vignette, "
              + "p.numero_version "
              + "FROM vote.productions AS p "
              + "INNER JOIN vote.participants AS g ON p.numero_participant = g.numero_participant "
              + "WHERE p.numero_production = :numeroProduction AND p.flag_actif IS TRUE ")
  ProductionShort findByIdWithoutArchive(@Param("numeroProduction") Integer numeroProduction);

  @NativeQuery("SELECT DISTINCT " 
      + "p.numero_production, "
      + "p.numero_participant AS numero_gestionnaire, "
      + "p.titre, "
      + "p.nom_archive, "
      + "'' AS archive "
      + "FROM vote.productions AS p "
      + "WHERE p.numero_production = :numeroProduction AND p.flag_actif IS TRUE ")
  ProductionFile findByIdForUpload(@Param("numeroProduction") Integer numeroProduction);
  
  @NativeQuery("SELECT DISTINCT " 
      + "TO_CHAR(p.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
      + "TO_CHAR(p.date_modification, 'DD/MM/YYYY HH24:MI:SS') as date_modification, "
      + "p.numero_production, "
      + "CAST(p.adresse_ip AS VARCHAR) AS adresse_ip, "
      + "p.type, "
      + "p.titre, "
      + "p.auteurs, "
      + "p.groupes, "
      + "p.plateforme, "
      + "p.commentaire, "
      + "p.informations_privees, "
      + "p.numero_participant AS numero_gestionnaire, "
      + "CONCAT(g.pseudonyme, ' = ', g.nom, ' ', g.prenom) AS nom_gestionnaire, "
      + "p.nom_archive, "
      + "p.vignette, "
      + "p.numero_version "
      + "FROM vote.productions AS p "
      + "INNER JOIN vote.participants AS g ON p.numero_participant = g.numero_participant "
      + "WHERE p.numero_participant = :participant AND p.flag_actif IS TRUE ORDER BY p.titre ASC ")
  List<ProductionShort> findByParticipantWithoutArchive(@Param("participant") Participant participant);
  
  
  @Override
  void delete(Production production);
  
}
