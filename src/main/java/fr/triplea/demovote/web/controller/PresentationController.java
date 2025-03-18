package fr.triplea.demovote.web.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.persistence.dao.PresentationRepository;
import fr.triplea.demovote.persistence.model.Presentation;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/presentation")
public class PresentationController 
{

  @Autowired
  private PresentationRepository presentationRepository;
 
  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('ADMIN')")
  public List<Presentation> getList() 
  {
    return presentationRepository.findAll(); 
  }
  
}
