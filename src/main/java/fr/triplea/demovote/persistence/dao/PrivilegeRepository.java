package fr.triplea.demovote.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.model.Privilege;
import fr.triplea.demovote.persistence.model.Role;

public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.privileges AS p WHERE p.numero_privilege = :id ")
  Privilege findById(@Param("id") int id);

  @NativeQuery("SELECT DISTINCT p.* FROM vote.privileges AS p WHERE p.libelle = :libelle ")
  Privilege findByLibelle(@Param("libelle") String libelle);
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.roles_privileges AS rp INNER JOIN vote.roles AS r ON rp.numero_role = r.numero_role INNER JOIN vote.privileges AS p ON rp.numero_privilege = r.numero_privilege WHERE r.flag_actif IS TRUE AND r.numero_role = :role ")
  List<Privilege> findbyRole(@Param("role") Role role);

  @Override
  void delete(Privilege privilege);
  
}
