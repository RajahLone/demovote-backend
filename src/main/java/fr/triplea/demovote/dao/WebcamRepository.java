package fr.triplea.demovote.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.model.Webcam;

public interface WebcamRepository extends JpaRepository<Webcam, Integer> 
{

  @NativeQuery("SELECT DISTINCT w.* FROM vote.webcams AS w WHERE w.id = :id ")
  Webcam find(@Param("id") Integer id);

  @NativeQuery("SELECT DISTINCT w.id FROM vote.webcams AS w ORDER BY w.id ASC ")
  List<Integer> listId();

  @NativeQuery("SELECT DISTINCT w.* FROM vote.webcams AS w ORDER BY w.id ASC ")
  List<Webcam> findAll();
  
}
