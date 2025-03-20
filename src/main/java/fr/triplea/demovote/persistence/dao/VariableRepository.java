package fr.triplea.demovote.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.dto.VariableTypeOptionList;
import fr.triplea.demovote.persistence.model.Variable;


public interface VariableRepository extends JpaRepository<Variable, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT v.* FROM vote.variables AS v ORDER BY v.type ASC, v.code ASC ")
  List<Variable> findAll();
  
  @NativeQuery("SELECT DISTINCT v.* FROM vote.variables AS v WHERE v.numero_variable = :id ")
  Variable findById(@Param("id") int id);

  @NativeQuery("SELECT DISTINCT v.* FROM vote.variables AS v WHERE v.type = :type ORDER BY v.type ASC, v.code ASC ")
  List<Variable> findByType(@Param("type") String type);

  @NativeQuery("SELECT DISTINCT v.valeur FROM vote.variables AS v WHERE v.type = :type AND v.code = :code ")
  String findByTypeAndCode(@Param("type") String type, @Param("code") String code);

  @NativeQuery("SELECT DISTINCT v.type FROM vote.variables AS v ORDER BY v.type ASC ")
  List<VariableTypeOptionList> getTypes();
  
}
