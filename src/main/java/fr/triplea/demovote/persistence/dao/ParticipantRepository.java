package fr.triplea.demovote.persistence.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.Role;
import jakarta.transaction.Transactional;

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
  
  @Transactional 
  @Modifying
  @NativeQuery("INSERT INTO vote.participants ("
      + "  nom,"
      + "  prenom,"
      + "  pseudonyme,"
      + "  groupe,"
      + "  delai_deconnexion,"
      + "  mot_de_passe,"
      + "  adresse,"
      + "  code_postal,"
      + "  ville,"
      + "  pays,"
      + "  numero_telephone,"
      + "  email,"
      + "  commentaire,"
      + "  flag_machine,"
      + "  flag_jour1,"
      + "  flag_jour2,"
      + "  flag_jour3,"
      + "  flag_dodo_sur_place,"
      + "  flag_amigabus,"
      + "  status,"
      + "  mode_paiement,"
      + "  somme_recue,"
      + "  flag_arrive "
      + ") VALUES ("
      + "  :nom,"
      + "  :prenom,"
      + "  :pseudonyme,n"
      + "  :groupe,n"
      + "  :delai_deconnexion,"
      + "  :mot_de_passe,"
      + "  :adresse,"
      + "  :code_postal,"
      + "  :ville,"
      + "  :pays,"
      + "  :numero_telephone,"
      + "  :email,"
      + "  :commentaire,"
      + "  :flag_machine,"
      + "  :flag_jour1,"
      + "  :flag_jour2,"
      + "  :flag_jour3,"
      + "  :flag_dodo_sur_place,"
      + "  :flag_amigabus,"
      + "  :status::vote.status_participant,"
      + "  :mode_paiement::vote.mode_paiement,"
      + "  :somme_recue,"
      + "  :flag_arrive"
      + ") ")
  public void create(
    @Param("nom") String nom,
    @Param("prenom") String prenom,
    @Param("pseudonyme") String pseudonyme,
    @Param("groupe") String groupe,
    @Param("delai_deconnexion") int delai_deconnexion,
    @Param("mot_de_passe") String mot_de_passe,
    @Param("adresse") String adresse,
    @Param("code_postal") String code_postal,
    @Param("ville") String ville,
    @Param("pays") String pays,
    @Param("numero_telephone") String numero_telephone,
    @Param("email") String email,
    @Param("status") String tatus,
    @Param("flag_machine") boolean flag_machine,
    @Param("commentaire") String commentaire,
    @Param("flag_jour1") boolean flag_jour1,
    @Param("flag_jour2") boolean flag_jour2,
    @Param("flag_jour3") boolean flag_jour3,
    @Param("flag_dodo_sur_place") boolean flag_dodo_sur_place,
    @Param("flag_amigabus") boolean flag_amigabus,
    @Param("mode_paiement") String mode_paiement,
    @Param("somme_recue") BigDecimal somme_recue,
    @Param("flag_arrive") boolean flag_arrive
    );  
  
  
  @Override
  void delete(Participant participant);

}
