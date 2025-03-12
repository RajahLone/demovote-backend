package fr.triplea.demovote.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.persistence.dao.VariableRepository;
import fr.triplea.demovote.persistence.dto.MessagesTransfer;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/demovote-api/v1/divers")
public class DiversController 
{

  @Autowired
  private VariableRepository variableRepository;
 
  @GetMapping(value = "/welcome")
  //@PreAuthorize("hasRole('LISTE_VARIABLES')")
  public ResponseEntity<MessagesTransfer> getWelcomeMessage() 
  { 
    MessagesTransfer mt = new MessagesTransfer();

    mt.setErreur(variableRepository.findByTypeAndCode("Messages", "ACCUEIL_ERREUR"));
    mt.setAlerte(variableRepository.findByTypeAndCode("Messages", "ACCUEIL_ALERTE"));
    mt.setInformation(variableRepository.findByTypeAndCode("Messages", "ACCUEIL_INFORMATION"));
    mt.setAutre(variableRepository.findByTypeAndCode("Messages", "ACCUEIL_AUTRE"));
    
    return ResponseEntity.ok(mt); 
  }


}
