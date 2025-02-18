package fr.triplea.demovote.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.model.Privilege;
import fr.triplea.demovote.persistence.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> 
{

  @NativeQuery("SELECT DISTINCT r.* FROM vote.roles AS r WHERE r.numero_role = :id AND r.flag_actif IS TRUE ")
  Role findById(@Param("id") int id);
  
  @NativeQuery("SELECT DISTINCT r.* FROM vote.roles AS r WHERE r.libelle = :libelle AND  r.flag_actif IS TRUE ")
  Role findByLibelle(@Param("libelle") String libelle);
  
  @NativeQuery("SELECT DISTINCT r.* FROM vote.roles AS r WHERE r.flag_actif IS TRUE ")
  List<Role> findAll();
  
  @NativeQuery("SELECT DISTINCT r.* FROM vote.roles_privileges AS rp INNER JOIN vote.roles AS r ON rp.numero_role = r.numero_role INNER JOIN vote.privileges AS p ON rp.numero_privilege = r.numero_privilege WHERE r.flag_actif IS TRUE AND p.numero_privilege = :privilege ")
  List<Role> findbyPrivilege(@Param("privilege") Privilege privilege);

  @Override
  void delete(Role role);

}
