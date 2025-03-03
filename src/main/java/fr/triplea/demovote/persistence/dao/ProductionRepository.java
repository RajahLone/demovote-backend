package fr.triplea.demovote.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.Production;

public interface ProductionRepository extends JpaRepository<Production, Integer> 
{

  @NativeQuery("SELECT DISTINCT p.* FROM vote.productions AS p WHERE p.numero_production = :id AND p.flag_actif IS TRUE ")
  Production findById(@Param("id") int id);
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.productions AS p WHERE p.flag_actif IS TRUE ORDER BY p.titre ASC ")
  List<Production> findAll();

  @NativeQuery("SELECT DISTINCT p.* FROM vote.productions AS p WHERE p.numero_participant = :participant AND p.flag_actif IS TRUE ")
  List<Production> findByParticipant(@Param("participant") Participant participant);

  @Override
  void delete(Production production);
  
}
