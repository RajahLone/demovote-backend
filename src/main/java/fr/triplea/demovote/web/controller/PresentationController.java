package fr.triplea.demovote.web.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.dao.PresentationRepository;
import fr.triplea.demovote.model.Presentation;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/presentation")
public class PresentationController 
{

  @Autowired
  private PresentationRepository presentationRepository;
 
  @GetMapping(value = "/list")
  public List<Presentation> getList() 
  {
    return presentationRepository.findAll(); 
  }
  
}
