package fr.triplea.demovote.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.dto.ParticipantList;
import fr.triplea.demovote.dto.ParticipantOptionList;
import fr.triplea.demovote.dto.ParticipantTransfer;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Role;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.numero_participant = :id AND p.flag_actif IS TRUE ")
  Participant findById(@Param("id") int id);

  @NativeQuery("SELECT DISTINCT "
      + "TO_CHAR(p.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
      + "TO_CHAR(p.date_modification, 'DD/MM/YYYY HH24:MI:SS') as date_modification, "
      + "p.numero_participant, "
      + "p.nom, "
      + "p.prenom, "
      + "p.pseudonyme, "
      + "'' AS mot_de_passe, "
      + "p.groupe, "
      + "p.delai_deconnexion, "
      + "p.adresse, "
      + "p.code_postal, "
      + "p.ville, "
      + "p.pays, "
      + "p.numero_telephone, "
      + "p.email, "
      + "p.statut, "
      + "p.flag_machine, "
      + "p.commentaire, "
      + "p.flag_jour1, "
      + "p.flag_jour2, "
      + "p.flag_jour3, "
      + "p.flag_dodo_sur_place, "
      + "p.flag_amigabus, "
      + "p.mode_paiement, "
      + "TO_CHAR(p.date_inscription, 'DD/MM/YYYY HH24:MI:SS') as date_inscription, "
      + "CAST(p.somme_recue AS VARCHAR) AS somme_recue, "
      + "p.flag_arrive "
      + "FROM vote.participants AS p "
      + "WHERE p.numero_participant = :id AND p.flag_actif IS TRUE ")
  ParticipantTransfer searchById(@Param("id") int id);

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
      + "ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<ParticipantList> getList();

  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants AS p WHERE p.flag_actif IS TRUE ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findAll();
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.participants_roles AS rp INNER JOIN vote.participants AS p ON rp.numero_participant = p.numero_participant INNER JOIN vote.roles AS r ON rp.numero_role = r.numero_role WHERE p.flag_actif IS TRUE AND r.flag_actif IS TRUE AND rp.numero_role = :role ORDER BY p.nom ASC, p.prenom ASC, p.pseudonyme ASC ")
  List<Participant> findByRole(@Param("role") Role role);

  @NativeQuery("SELECT DISTINCT "
      + "TO_CHAR(p.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
      + "TO_CHAR(p.date_modification, 'DD/MM/YYYY HH24:MI:SS') as date_modification, "
      + "p.numero_participant, "
      + "p.nom, "
      + "p.prenom, "
      + "p.pseudonyme, "
      + "'' AS mot_de_passe, "
      + "p.groupe, "
      + "p.delai_deconnexion, "
      + "p.adresse, "
      + "p.code_postal, "
      + "p.ville, "
      + "p.pays, "
      + "p.numero_telephone, "
      + "p.email, "
      + "p.statut, "
      + "p.flag_machine, "
      + "p.commentaire, "
      + "p.flag_jour1, "
      + "p.flag_jour2, "
      + "p.flag_jour3, "
      + "p.flag_dodo_sur_place, "
      + "p.flag_amigabus, "
      + "p.mode_paiement, "
      + "TO_CHAR(p.date_inscription, 'DD/MM/YYYY HH24:MI:SS') as date_inscription, "
      + "CAST(p.somme_recue AS VARCHAR) AS somme_recue, "
      + "p.flag_arrive "
      + "FROM vote.participants AS p "
      + "WHERE p.pseudonyme = :pseudo AND p.flag_actif IS TRUE ")
  ParticipantTransfer searchByPseudonyme(@Param("pseudo") String pseudonyme);

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
  List<ParticipantOptionList> getOptionList();

}
