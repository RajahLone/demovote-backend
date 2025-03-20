package fr.triplea.demovote.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.dto.ProductionFile;
import fr.triplea.demovote.persistence.dto.ProductionShort;
import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.Production;


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
              + "WHERE p.flag_actif IS TRUE ORDER BY p.titre ASC ")
  List<ProductionShort> findAllWithoutArchive();
  
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
