package fr.triplea.demovote.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.dto.ProductionShort;
import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.Production;

public interface ProductionRepository extends JpaRepository<Production, Integer> 
{

  @NativeQuery("SELECT DISTINCT p.* FROM vote.productions AS p WHERE p.numero_production = :id AND p.flag_actif IS TRUE ")
  Production findById(@Param("id") int id);
  
  @NativeQuery("SELECT DISTINCT p.numero_production, p.titre, p.auteurs, p.groupes, p.plateforme, p.commentaire, p.informations_privees, p.numero_participant, p.nom_archive, p.vignette, p.numero_version FROM vote.productions AS p WHERE p.flag_actif IS TRUE ORDER BY p.titre ASC ")
  List<ProductionShort> findAllEnabled();
  
  @NativeQuery("SELECT DISTINCT p.date_creation, p.date_modification, p.numero_production, p.adresse_ip, p.type, p.titre, p.auteurs, p.groupes, p.plateforme, p.commentaire, p.informations_privees, p.numero_participant, p.nom_archive, p.vignette, p.numero_version FROM vote.productions AS p WHERE p.numero_participant = :participant AND p.flag_actif IS TRUE ")
  List<ProductionShort> findByParticipant(@Param("participant") Participant participant);

  @Override
  void delete(Production production);
  
}
