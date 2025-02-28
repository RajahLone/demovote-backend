package fr.triplea.demovote.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.Role;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.numero_participant = :id AND p.flag_actif IS TRUE ")
  Participant findById(@Param("id") int id);
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.flag_actif IS TRUE ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findAll();
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants_roles AS rp INNER JOIN vote.participants AS p ON rp.numero_participant = p.numero_participant INNER JOIN vote.roles AS r ON rp.numero_role = r.numero_role WHERE p.flag_actif IS TRUE AND r.flag_actif IS TRUE AND rp.numero_role = :role ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findByRole(@Param("role") Role role);

  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.flag_actif IS TRUE AND p.pseudonyme = :pseudo ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  Participant findByPseudonyme(@Param("pseudo") String pseudonyme);
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.status = :status AND p.flag_actif IS TRUE ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findByStatus(@Param("status") String status);

  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.flag_arrive = :arrive AND p.flag_actif IS TRUE ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findByArrived(@Param("arrive") boolean flag_arrive);

  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.flag_dodo_sur_place = :dodo AND p.flag_actif IS TRUE ORDER BYp.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findBySleepingOnSite(@Param("dodo") boolean flag_dodo_sur_place);
  
  @Override
  void delete(Participant participant);

}
