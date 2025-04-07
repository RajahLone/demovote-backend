package fr.triplea.demovote.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.triplea.demovote.model.Webcam;

public interface WebcamRepository  extends JpaRepository<Webcam, Integer> 
{

  Webcam find(Integer id);

}
