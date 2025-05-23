package fr.triplea.demovote.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.dto.ParticipantList;
import fr.triplea.demovote.dto.ParticipantOptionList;
import fr.triplea.demovote.dto.PseudonymeOptionList;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Role;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.numero_participant = :id AND p.flag_actif IS TRUE ")
  Participant findById(@Param("id") int id);
  
  @NativeQuery("SELECT DISTINCT "
      + "COUNT(p.*) AS nombre "
      + "FROM vote.participants AS p "
      + "WHERE p.flag_actif IS TRUE "
      + "AND ((:nom IS NULL) OR (UPPER(p.nom) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.prenom) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.pseudonyme) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.groupe) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.email) LIKE CONCAT('%', :nom, '%'))) "
      + "AND ((:statut = 0) OR (:statut = 1 AND p.statut = 'EN_ATTENTE'::vote.statut_participant)) "
      + "AND ((:arrive = 0) OR (:arrive = 1 AND p.flag_arrive = FALSE) OR (:arrive = 2 AND p.flag_arrive = TRUE)) ")
  Integer count(@Param("nom") String nom, @Param("statut") int statut, @Param("arrive") int arrive);
  
  @NativeQuery("SELECT DISTINCT "
      + "p.numero_participant, "
      + "p.nom, "
      + "p.prenom, "
      + "p.pseudonyme, "
      + "p.groupe, "
      + "p.email, "
      + "p.statut, "
      + "p.flag_jour1, "
      + "p.flag_jour2, "
      + "p.flag_jour3, "
      + "p.flag_dodo_sur_place, "
      + "p.flag_arrive "
      + "FROM vote.participants AS p "
      + "WHERE p.flag_actif IS TRUE "
      + "AND ((:nom IS NULL) OR (UPPER(p.nom) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.prenom) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.pseudonyme) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.groupe) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.email) LIKE CONCAT('%', :nom, '%'))) "
      + "AND ((:statut = 0) OR (:statut = 1 AND p.statut = 'EN_ATTENTE'::vote.statut_participant)) "
      + "AND ((:arrive = 0) OR (:arrive = 1 AND p.flag_arrive = FALSE) OR (:arrive = 2 AND p.flag_arrive = TRUE)) "
      + "ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC "
      + "LIMIT :limite OFFSET :debut ")
  List<ParticipantList> getPageOrderedByNom(@Param("nom") String nom, @Param("statut") int statut, @Param("arrive") int arrive, @Param("debut") int debut, @Param("limite") Integer limite);
  
  @NativeQuery("SELECT DISTINCT "
      + "p.numero_participant, "
      + "p.nom, "
      + "p.prenom, "
      + "p.pseudonyme, "
      + "p.groupe, "
      + "p.email, "
      + "p.statut, "
      + "p.flag_jour1, "
      + "p.flag_jour2, "
      + "p.flag_jour3, "
      + "p.flag_dodo_sur_place, "
      + "p.flag_arrive "
      + "FROM vote.participants AS p "
      + "WHERE p.flag_actif IS TRUE "
      + "AND ((:nom IS NULL) OR (UPPER(p.nom) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.prenom) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.pseudonyme) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.groupe) LIKE CONCAT('%', :nom, '%')) OR (UPPER(p.email) LIKE CONCAT('%', :nom, '%'))) "
      + "AND ((:statut = 0) OR (:statut = 1 AND p.statut = 'EN_ATTENTE'::vote.statut_participant)) "
      + "AND ((:arrive = 0) OR (:arrive = 1 AND p.flag_arrive = FALSE) OR (:arrive = 2 AND p.flag_arrive = TRUE)) "
      + "ORDER BY p.numero_participant ASC "
      + "LIMIT :limite OFFSET :debut ")
  List<ParticipantList> getPageOrderedByDateInscription(@Param("nom") String nom, @Param("statut") int statut, @Param("arrive") int arrive, @Param("debut") int debut, @Param("limite") Integer limite);

  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.flag_actif IS TRUE ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findAll();
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants_roles AS rp INNER JOIN vote.participants AS p ON rp.numero_participant = p.numero_participant INNER JOIN vote.roles AS r ON rp.numero_role = r.numero_role WHERE p.flag_actif IS TRUE AND r.flag_actif IS TRUE AND rp.numero_role = :role ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findByRole(@Param("role") Role role);

  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.flag_actif IS TRUE AND p.pseudonyme = :pseudo ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  Participant findByPseudonyme(@Param("pseudo") String pseudonyme);
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.status = :status AND p.flag_actif IS TRUE ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findByStatut(@Param("status") String status);

  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.flag_arrive = :arrive AND p.flag_actif IS TRUE ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findByArrived(@Param("arrive") boolean flag_arrive);

  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.flag_dodo_sur_place = :dodo AND p.flag_actif IS TRUE ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findBySleepingOnSite(@Param("dodo") boolean flag_dodo_sur_place);
  
  @Override
  void delete(Participant participant);

  @NativeQuery("SELECT DISTINCT p.numero_participant, p.pseudonyme, p.nom, p.prenom FROM vote.participants AS p WHERE p.flag_actif IS TRUE ORDER BY p.pseudonyme ASC, p.nom ASC, p.prenom ASC ")
  List<ParticipantOptionList> getParticipantOptionList();

  @NativeQuery("SELECT DISTINCT p.numero_participant, p.pseudonyme FROM vote.participants AS p WHERE (p.flag_actif IS TRUE) AND (p.numero_participant <> :id) AND (LENGTH(p.pseudonyme) > 0) ORDER BY p.pseudonyme ASC ")
  List<PseudonymeOptionList> getPseudonymeOptionList(@Param("id") int id);

  @Modifying
  @NativeQuery("UPDATE vote.participants SET flag_arrive = TRUE WHERE numero_participant IN :numeros ")
  void setFlagArrives(@Param("numeros") List<Integer> listeNumeroParticipants);

}
