package fr.triplea.demovote.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.dto.EvenementTransfer;
import fr.triplea.demovote.model.Evenement;

public interface EvenementRepository extends JpaRepository<Evenement, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT e.* FROM vote.evenements AS e WHERE e.numero_evenement = :numero ")
  Evenement findById(@Param("numero") int numeroEvenement);
  
  @NativeQuery("SELECT DISTINCT e.* FROM vote.evenements AS e ORDER BY e.date_debut ASC ")
  List<Evenement> findAll();
 
  @NativeQuery("SELECT DISTINCT " 
      + "e.numero_evenement, "
      + "TO_CHAR(e.date_debut, 'YYYY-MM-DD') AS jour_debut, "
      + "TO_CHAR(e.date_debut, 'HH24hMI') AS heure_debut, "
      + "CASE WHEN e.date_fin IS NULL THEN '' ELSE TO_CHAR(e.date_fin, 'HH24hMI') END AS heure_fin, "
      + "CASE WHEN e.date_fin IS NULL THEN 0::integer ELSE FLOOR(EXTRACT(epoch FROM e.date_fin::timestamp - e.date_debut::timestamp) / 60)::integer END AS duree, "
      + "e.type, "
      + "e.intitule, "
      + "e.descriptif, "
      + "e.lien "
      + "FROM vote.evenements AS e "
      + "WHERE TO_CHAR(e.date_debut, 'YYYY-MM-DD') = :journee "
      + "ORDER BY heure_debut ASC ")
  List<EvenementTransfer> findByDay(@Param("journee") String journee);

  @Override
  void delete(Evenement evenement);

}
